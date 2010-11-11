package de.unisb.cs.st.javalanche.coverage.distance.classes;

public class B extends A {

	public void m1() {
		super.m1();
	}

	public void m3() {
		m1();
	}

}
