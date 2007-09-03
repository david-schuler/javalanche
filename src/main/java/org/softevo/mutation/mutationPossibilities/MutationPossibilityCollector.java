package org.softevo.mutation.mutationPossibilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.softevo.bytecodetransformer.processFiles.FileTransformer;
import org.softevo.mutation.bytecodeMutations.replaceIntegerConstant.RICCollectorTransformer;
import org.softevo.mutation.properties.MutationProperties;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.Mutation.MutationType;
import org.softevo.mutation.results.persistence.QueryManager;

public class MutationPossibilityCollector {

	private List<Mutation> possibilities = new ArrayList<Mutation>();

	public void addPossibility(Mutation mutationPossibility) {
		possibilities.add(mutationPossibility);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Mutation mutation : possibilities) {
			sb.append(mutation);
			sb.append('\n');
		}
		return sb.toString();
	}

	// public void toXML(){
	// XmlIo.toXML(possibilities,new
	// File(MutationProperties.MUTATIONS_TO_APPLY_FILE));
	// }
	//
	public void toDB() {
		for (Mutation mutation : possibilities) {
			QueryManager.saveMutation(mutation);
		}

	}

	public static void generateUnmutated() {
		List<Mutation> allMutations = QueryManager.getAllMutations();
		for (Mutation m : allMutations) {
			if (!QueryManager.hasUnmutated(m.getClassName(), m.getLineNumber())) {
				Mutation unmutated = new Mutation(m.getClassName(), m
						.getLineNumber(),m.getMutationForLine(), MutationType.NO_MUTATION);
				QueryManager.saveMutation(unmutated);
			}
		}

	}

	public static void generateTestDataInDB(String classFileName) {
		FileTransformer ft = new FileTransformer(new File(classFileName));
		MutationPossibilityCollector mpc = new MutationPossibilityCollector();
		ft.process(new RICCollectorTransformer(mpc));
		mpc.toDB();
	}

	public static void main(String[] args) {
		generateTestDataInDB(MutationProperties.SAMPLE_FILE);
		generateUnmutated();
	}
}
