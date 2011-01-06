package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

import static de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


import de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps.JumpInfo;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.Mutation.MutationType;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class ParseResultAnalyzer {

	private static Logger logger = Logger.getLogger(ParseResultAnalyzer.class);

	public static List<AdaptedMutationDescription> analyzeJump(
			Map<String, ASTParseResult> astParseResults) {
		List<AdaptedMutationDescription> res = new ArrayList<AdaptedMutationDescription>();
		for (ASTParseResult astParseResult : astParseResults.values()) {
			List<AdaptedMutationDescription> analyze = analyzeJump(astParseResult);
			res.addAll(analyze);
		}
		return res;
	}

	public static List<AdaptedMutationDescription> analyzeJump(
			ASTParseResult astParseResult) {
		List<AdaptedMutationDescription> res = new ArrayList<AdaptedMutationDescription>();
		List<IfStatementInfo> ifStatementInfos = astParseResult
				.getIfStatementInfos();
		String className = astParseResult.getClassName();
		for (IfStatementInfo stmtInfo : ifStatementInfos) {
			if (stmtInfo.isInTryBlock()) {
				continue;
			}
			if (!stmtInfo.hasElse()) {

				AdaptedMutationDescription desc = new AdaptedMutationDescription(
						ADAPTED_JUMP, ADAPTED_REMOVE_CHECK, className, stmtInfo
								.getStart(), stmtInfo.getEnd());
				res.add(desc);

				AdaptedMutationDescription desc2 = new AdaptedMutationDescription(
						ADAPTED_JUMP, ADAPTED_SKIP_IF, className, stmtInfo
								.getStart(), stmtInfo.getEnd());
				res.add(desc2);

			}
			if (stmtInfo.hasElse()) {
				AdaptedMutationDescription desc = new AdaptedMutationDescription(
						ADAPTED_JUMP, ADAPTED_SKIP_ELSE, className, stmtInfo
								.getStart(), stmtInfo.getEnd());
				res.add(desc);
				if (!stmtInfo.hasInnerIf() && !stmtInfo.hasBreak()) {
					AdaptedMutationDescription desc2 = new AdaptedMutationDescription(
							ADAPTED_JUMP, ADAPTED_ALWAYS_ELSE, className,
							stmtInfo.getStart(), stmtInfo.getElseStart(),
							stmtInfo.getEnd());
					res.add(desc2);
				}
			}
			AdaptedMutationDescription desc = new AdaptedMutationDescription(
					ADAPTED_JUMP, ADAPTED_NEGATE_JUMP_IN_IF, className,
					stmtInfo.getStart(), stmtInfo.getEnd());
			res.add(desc);
		}
		return res;
	}

	public static List<AdaptedMutationDescription> analyzeReplace(
			Map<String, ASTParseResult> astParseResults) {
		List<AdaptedMutationDescription> res = new ArrayList<AdaptedMutationDescription>();
		for (ASTParseResult astParseResult : astParseResults.values()) {
			List<AdaptedMutationDescription> analyze = analyzeReplace(astParseResult);
			res.addAll(analyze);
		}
		return res;
	}

	public static List<AdaptedMutationDescription> analyzeReplace(
			ASTParseResult astParseResult) {
		List<AdaptedMutationDescription> res = new ArrayList<AdaptedMutationDescription>();
		List<MethodCallInfo> methodCallInfos = astParseResult
				.getMethodCallInfos();
		String className = astParseResult.getClassName();
		for (MethodCallInfo methodCallInfo : methodCallInfos) {
			AdaptedMutationDescription desc = new AdaptedMutationDescription(
					ADAPTED_REPLACE, ADAPTED_REPLACE_METHOD_ARG, className,
					methodCallInfo.getLine());
			res.add(desc);
		}

		Map<Integer, AssignmentInfo> assMap = getAssignmentMap(astParseResult);
		Map<Integer, FieldInfo> fieldMap = getFieldMap(astParseResult);
		List<MethodInfo> methodInfos = astParseResult.getMethodInfos();

		List<FieldInfo> fieldInfos = astParseResult.getFieldInfos();
		for (FieldInfo fieldInfo : fieldInfos) {
			AdaptedMutationDescription desc = new AdaptedMutationDescription(
					ADAPTED_REPLACE_STORE, MutationType.ADAPTED_REPLACE_FIELD,
					className, fieldInfo.getLineNumber());
			res.add(desc);
		}
		List<AssignmentInfo> assignmentInfos = astParseResult
				.getAssignmentInfos();
		for (AssignmentInfo assignmentInfo : assignmentInfos) {
			AdaptedMutationDescription desc = new AdaptedMutationDescription(
					ADAPTED_REPLACE_STORE,
					MutationType.ADAPTED_REPLACE_ASSIGNMENT, className,
					assignmentInfo.getLineNumber());
			res.add(desc);
		}

		List<ReturnInfo> returnInfos = astParseResult.getReturnInfos();
		for (ReturnInfo returnInfo : returnInfos) {
			AdaptedMutationDescription desc = new AdaptedMutationDescription(
					ADAPTED_REPLACE, MutationType.ADAPTED_REPLACE_RETURN,
					className, returnInfo.getLineNumber());
			res.add(desc);
		}
		return res;
	}

	public static List<Mutation> writeMutations(
			List<AdaptedMutationDescription> list) {
		List<Mutation> writtenMutations = new ArrayList<Mutation>();

		Set<Long> coveredAdd = new HashSet<Long>();
		for (AdaptedMutationDescription desc : list) {
			int lineNumber = desc.getLineNumber();
			MutationType type = desc.getType();
			String className = desc.getClassName();
			List<Mutation> mutations = QueryManager.getMutations(className,
					type, lineNumber);

			for (Mutation mutation : mutations) {
				Mutation m2 = getMutation(mutation, desc);
				writtenMutations.add(m2);
				if (MutationCoverageFile.isCovered(mutation.getId())) {
					MutationCoverageFile.copyCoverageData(mutation.getId(), m2
							.getId());
					coveredAdd.add(m2.getId());
				}
				QueryManager.delete(mutation);
			}
		}
		QueryManager.deleteMutations(MutationType.ADAPTED_REPLACE);
		QueryManager.deleteMutations(MutationType.ADAPTED_REPLACE_STORE);
		MutationCoverageFile.addCoveredMutations(coveredAdd);
		return writtenMutations;
	}

	private static Map<Integer, FieldInfo> getFieldMap(
			ASTParseResult astParseResult) {
		HashMap<Integer, FieldInfo> res = new HashMap<Integer, FieldInfo>();
		List<FieldInfo> assignmentInfos = astParseResult.getFieldInfos();
		for (FieldInfo assignmentInfo : assignmentInfos) {
			res.put(assignmentInfo.getLineNumber(), assignmentInfo);
		}
		return res;
	}

	private static Map<Integer, AssignmentInfo> getAssignmentMap(
			ASTParseResult astParseResult) {
		Map<Integer, AssignmentInfo> res = new HashMap<Integer, AssignmentInfo>();
		List<AssignmentInfo> assignmentInfos = astParseResult
				.getAssignmentInfos();
		for (AssignmentInfo assignmentInfo : assignmentInfos) {
			res.put(assignmentInfo.getLineNumber(), assignmentInfo);
		}
		return res;
	}

	public static void writeJumpMutations(
			List<AdaptedMutationDescription> adaptedDescriptions) {
		Map<Long, Integer> jumpMap = new HashMap<Long, Integer>();
		Map<Long, Integer> endMap = new HashMap<Long, Integer>();
		Set<Long> coveredAdd = new HashSet<Long>();

		for (AdaptedMutationDescription desc : adaptedDescriptions) {
			int lineNumber = desc.getLineNumber();
			MutationType type = desc.getType();
			String className = desc.getClassName();
			List<Mutation> mutations = QueryManager.getMutations(className,
					type, lineNumber);
			for (Mutation mutation : mutations) {
				Mutation m2 = getMutation(mutation, desc);
				Long id = m2.getId();
				if (id == null) {
					continue;
					// throw new RuntimeException("Mutation has no id: " + m2);
				}
				jumpMap.put(id, desc.getJumpLine());
				if (desc.getEndLine() != -1) {
					endMap.put(id, desc.getEndLine());
				}
				if (MutationCoverageFile.isCovered(mutation.getId())) {

					MutationCoverageFile.copyCoverageData(mutation.getId(), id);
					coveredAdd.add(id);
				}
				// QueryManager.delete(mutation);
			}
		}

		QueryManager.deleteMutations(MutationType.ADAPTED_JUMP);
		MutationCoverageFile.addCoveredMutations(coveredAdd);
		JumpInfo.writeMap(jumpMap);
		JumpInfo.writeEndMap(endMap);
	}

	private static Mutation getMutation(Mutation m,
			AdaptedMutationDescription desc) {
		Mutation r = new Mutation(m.getClassName(), m.getMethodName(), m
				.getLineNumber(), m.getMutationForLine(), desc.getSubType());
		logger.info("Storing mutation " + r);
		QueryManager.saveMutation(r);
		return r;
	}

	public static void main(String[] args) {

		String sourceDir = MutationProperties.PROJECT_SOURCE_DIR;
		File dir = new File(sourceDir);
		if (!dir.exists()) {
			throw new RuntimeException("Directory does not exist: " + dir);
		}
		Map<String, ASTParseResult> results = SourceScanner.parseDirectory(dir);
		List<AdaptedMutationDescription> analyzeJump = analyzeJump(results);
		writeJumpMutations(analyzeJump);
		List<AdaptedMutationDescription> analyzeReplace = analyzeReplace(results);
		writeMutations(analyzeReplace);
	}
}
