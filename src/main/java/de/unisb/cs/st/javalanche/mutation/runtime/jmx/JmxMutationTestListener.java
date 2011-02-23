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
package de.unisb.cs.st.javalanche.mutation.runtime.jmx;

import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestListener;

public class JmxMutationTestListener implements MutationTestListener {

	private MutationMX bean;
	private MXBeanRegisterer beanReg = new MXBeanRegisterer();

	public void end() {
		// System.out.println("JmxMutationTestListener.end()");
		if (bean != null) {
			beanReg.unregister(bean);
		}
	}

	public void mutationEnd(Mutation mutation) {
	}

	public void mutationStart(Mutation mutation) {
		// System.out.println("JmxMutationTestListener.mutationStart()");
		if (bean != null) {
			bean.addMutation(mutation);
		}
	}

	public void start() {
		int runNumber = getRunNumber();
		if (runNumber >= 0) {
			bean = beanReg.registerMutationMXBean(runNumber);
		}

	}

	private static int getRunNumber() {
		int result = -1;
		String run = ConfigurationLocator.getJavalancheConfiguration()
				.getMutationIdFile().getName();
		int start = run.lastIndexOf('-') + 1;
		int end = run.lastIndexOf(".txt");
		if (start > -1 && end > 0) {
			String numberString = run.substring(start, end);
			try {
				result = Integer.parseInt(numberString);
			} catch (NumberFormatException e) {
			}
		}
		return result;
	}

	public void testEnd(String testName) {
	}

	public void testStart(String testName) {
		// System.out.println("JmxMutationTestListener.testStart()" + bean);
		if (bean != null) {
			bean.setTest(testName);
		}
	}

	public static void main(String[] args) {
		new JmxMutationTestListener().start();
	}
}
