package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes;

public class ReplaceVariables1TEMPLATE {

	public static int a = 1;

	public static int b = 2;

	public static int c = 3;

	public int m1() {
		int r = a;
		// System.out.println("ReplaceVariables1TEMPLATE.m1() ");
		return r;
	}

	public int m2() {

		System.out.println("A");
		return 2;
	}

}
