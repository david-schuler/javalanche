package de.unisb.cs.st.javalanche.mutation.util.sufficient.classes;

public class UoiTEMPLATE {

	public static int m1(int x) {
		int i = 2;
		return i * x;
	}

	public static double m2(double x) {
		double factor = 2;
		return factor * x;
	}

	public static float m3(float x) {
		long y = 1;
		y = ~y;
		return x;
	}
}
