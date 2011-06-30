package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.unaryOperatorInsertion;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.CoverageDataUtil;

public class UnaryOperatorPossibilitiesAdapter extends
		AbstractUnaryOperatorMethodAdapater {

	private final MutationPossibilityCollector mpc;

	public UnaryOperatorPossibilitiesAdapter(MethodVisitor mv,
			String className, String methodName,
			Map<Integer, Integer> possibilities, String desc,
			MutationPossibilityCollector mpc) {
		super(mv, className, methodName, possibilities, desc);
		this.mpc = mpc;
	}

	@Override
	protected void handleMutation(Mutation mutation, Integer type) {
		Mutation mUnaryMinus = Mutation.copyMutation(mutation);
		mUnaryMinus.setOperatorAddInfo(MINUS);
		mUnaryMinus.setAddInfo("Insert unary minus");
		mpc.addPossibility(mUnaryMinus);
		CoverageDataUtil.insertCoverageCalls(mv, mUnaryMinus);
		QueryManager.saveMutation(mUnaryMinus);
		Long id = mUnaryMinus.getId();

		Mutation mUnaryNegate = Mutation.copyMutation(mutation);
		mUnaryNegate.setOperatorAddInfo(BITWISE_NEGATE);
		mUnaryNegate.setAddInfo("Insert bitwise negation");
		mUnaryNegate.setBaseMutationId(id);
		QueryManager.saveMutation(mUnaryNegate);
		if (mUnaryNegate.getId() != null) {
			MutationCoverageFile.addDerivedMutation(id, mUnaryNegate.getId());
		}
		mpc.addPossibility(mUnaryNegate);

	}

}
