package de.unisb.cs.st.javalanche.coverage.distance.classes;

public class A {

	public void m1() {

	}

	public void m2() {
		m1();
	}

	public void m3() {
		m2();
	}

	public void m4() {
		m3();
	}

	public void m5() {
		m4();
	}

	public void m6() {
		m5();
	}

	public void m7() {
		m6();
	}
}
