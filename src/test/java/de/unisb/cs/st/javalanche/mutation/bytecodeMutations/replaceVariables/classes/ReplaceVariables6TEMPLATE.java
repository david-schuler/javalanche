package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes;

public class ReplaceVariables6TEMPLATE {

	static int i = 2;

	static long l = 5l;
	
	static float f = 6f;
	
	static double d = 7d;
	
	static String s = "a";

	static Object o = "b";

	public int m1() {
		return i;
	}

	public long m2() {
		return l;
	}

	public float m3() {
		return f;
	}

	public double m4() {
		return d;
	}

	public String m5() {
		return s;
	}

	public Object m6() {
		return o;
	}

	public String m7() {
		Object a = Integer.MAX_VALUE;
		String b = "B";
		return b;
	}

}
