package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.sysexit.classes;

public class SystemExit2TEMPLATE {

	private int x;

	public void run() {
		try {
			while (!Thread.currentThread().interrupted()) {
				evolve();
				calcFitness();
				System.gc();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public void evolve() {
		throw new RuntimeException();
	}

	public void calcFitness() {
	}

}
