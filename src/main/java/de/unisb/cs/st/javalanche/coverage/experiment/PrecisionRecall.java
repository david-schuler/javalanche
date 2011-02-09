/*
* Copyright (C) 2011 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.unisb.cs.st.javalanche.coverage.experiment;

public class PrecisionRecall {

	private static final int ALL = 140;
	private final int equivalent;
	private final int nonEquivalent;

	public PrecisionRecall(int equivalent, int nonEquivalent) {
		this.equivalent = equivalent;
		this.nonEquivalent = nonEquivalent;
	}

	public static void main(String[] args) {

		int equivalent = 63;
		int nonEquivalent = ALL - equivalent;
		PrecisionRecall p = new PrecisionRecall(equivalent, nonEquivalent);

		int nonEquivalentLineImpact = 43;
		int equivalentLineImpact = 14;
		p.printPR("Line Impact", nonEquivalentLineImpact, equivalentLineImpact);

		int nonEquivalentLineImpactDistance = 38;
		int equivalentLineImpactDistance = 10;
		p.printPR("Line Impact Distance", nonEquivalentLineImpactDistance,
				equivalentLineImpactDistance);

		int nonEquivalentReturnImpact = 37;
		int equivalentReturnImpact = 17;
		p.printPR("Return Impact", nonEquivalentReturnImpact,
				equivalentReturnImpact);
		
		int nonEquivalentReturnImpactDistance = 34;
		int equivalentReturnImpactDistance = 16;
		p.printPR("Return Impact Distance", nonEquivalentReturnImpactDistance,
				equivalentReturnImpactDistance);
		
		int nonEquivalentAllImpact = 47;
		int equivalentAllImpact = 20;

		p.printPR("All Impact ", nonEquivalentAllImpact, equivalentAllImpact);
		
		int nonEquivalentAllImpactDistance = 42;
		int equivalentAllImpactDistance = 17;
		p.printPR("All Impact Distance", nonEquivalentAllImpactDistance,
				equivalentAllImpactDistance);
		
		int nonEquivalentInvariantImpact = 15;
		int equivalentInvariantImpact = 6;
		p.printPR("Invariant Impact ", nonEquivalentInvariantImpact,
				equivalentInvariantImpact);
		
		int nonEquivalentInvariantImpactViolatons = 10;
		int equivalentInvariantImpactViolations = 2;
		p.printPR("Invariant Impact Number of Violations",
				nonEquivalentInvariantImpactViolatons,
				equivalentInvariantImpactViolations);
		
	}

	private void printPR(String message, int right, int wrong) {
		double precision = precision(right, wrong);
		double recall = recall(right);
		System.out.printf("%s Precision: %.2f  Recall %.2f \n", message,
				precision, recall);
	}

	private double recall(int right) {

		return ((double) right) / nonEquivalent;
	}

	private double precision(int right, int wrong) {
		return (double) right / ((double) (right + wrong));
	}
}
