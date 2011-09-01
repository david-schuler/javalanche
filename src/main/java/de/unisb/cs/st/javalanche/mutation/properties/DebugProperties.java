package de.unisb.cs.st.javalanche.mutation.properties;

import static de.unisb.cs.st.javalanche.mutation.properties.PropertyUtil.*;

public class DebugProperties {

	/**
	 * 
	 * Debugging Properties. Enable some debugging functionality of Javalanche.
	 * 
	 * */
	public static String INSERT_ORIGINAL_INSTEAD_OF_MUTATION_KEY = "javalanche.debug.insert.original.code";

	public static boolean INSERT_ORIGINAL_INSTEAD_OF_MUTATION = getPropertyOrDefault(
			INSERT_ORIGINAL_INSTEAD_OF_MUTATION_KEY, false);

	public static final boolean QUERY_DB_BEFORE_START = false;

	/**
	 * When set true, System.println statements will be inserted that signal
	 * whether a mutation is covered.
	 */
	public static final String MUTATION_PRINT_STATEMENTS_ENABLED_KEY = "javalanche.mutation.print.statements";

	public static final boolean MUTATION_PRINT_STATEMENTS_ENABLED = getPropertyOrDefault(
			MUTATION_PRINT_STATEMENTS_ENABLED_KEY, false);

	public static final boolean TRACE_BYTECODE = getPropertyOrDefault(
			"javalanche.trace.bytecode", false);;

}
