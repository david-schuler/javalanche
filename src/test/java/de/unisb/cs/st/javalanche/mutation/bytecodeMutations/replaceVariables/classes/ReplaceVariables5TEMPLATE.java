package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes;

public class ReplaceVariables5TEMPLATE {

	public double m1() {
		double a = 1.;
		double b = 2.2;
		double c = 3.2;
		return a;
	}

	public float m2() {
		float a = 1.f;
		float b = 1.2f;
		return a;
	}

	public long m3() {
		long a = 1l;
		long b = 1234567890l;
		return a;
	}

	public Object m4() {
		String a = "A";
		String b = "B";
		return a;
	}

	public Object m5() {
		Object a = "A";
		String b = "B";
		return a;
	}

}
