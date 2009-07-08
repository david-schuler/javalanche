package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

/**
 * Abstract Method adapter that provides functionality that is common for all
 * mutation operators, e.g. keeping track of the current line number and the
 * number of mutations that can be applied in each line.
 * 
 * @author David Schuler
 * 
 */
public abstract class AbstractMutationAdapter extends MethodAdapter {

	private static final Logger logger = Logger
			.getLogger(AbstractMutationAdapter.class);

	private int lineNumber = -1;

	protected String className;

	protected String methodName;

	protected boolean insertCoverageCalls = true;

	private Map<Integer, Integer> possibilities;

	protected boolean mutationCode = false;

	protected final boolean isClassInit;

	public AbstractMutationAdapter(MethodVisitor mv, String className,
			String methodName, Map<Integer, Integer> possibilities) {
		super(mv);
		this.className = className;
		this.methodName = methodName;
		this.possibilities = possibilities;
		this.isClassInit = methodName.equals("<clinit>");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.asm.MethodAdapter#visitLineNumber(int,
	 * org.objectweb.asm.Label)
	 */
	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
		lineNumber = line;
	}

	/**
	 * @return the current line number.
	 */
	protected int getLineNumber() {
		// if (lineNumber < 0) {
		// rhino produces classes with no line number information. AspectJ also
		// produces such files
		// throw new RuntimeException(String.format(
		// "Line number not available for class: %s method: %s",
		// className, methodName));
		// }
		return lineNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.asm.MethodAdapter#visitLabel(org.objectweb.asm.Label)
	 */
	@Override
	public void visitLabel(Label label) {
		super.visitLabel(label);
		if (label.info instanceof MutationMarker) {
			MutationMarker marker = (MutationMarker) label.info;
			logger.debug("Found mutation marker: "
					+ (marker.isStart() ? "start" : "end") + " in line "
					+ getLineNumber() + "  " + this);
			mutationCode = marker.isStart();
		}
	}

	/**
	 * Adds a mutation possibility for the current line.
	 */
	protected void addPossibilityForLine() {
		if (possibilities.containsKey(lineNumber)) {
			int pos = possibilities.get(lineNumber);
			possibilities.put(lineNumber, pos + 1);
		} else
			possibilities.put(lineNumber, 1);
	}

	/**
	 * @return the mutation possibilities for the current line found up to this
	 *         point.
	 */
	protected int getPossibilityForLine() {
		int pos = 0;
		if (possibilities.containsKey(lineNumber)) {
			pos = possibilities.get(lineNumber);
		}
		return pos;
	}

}
