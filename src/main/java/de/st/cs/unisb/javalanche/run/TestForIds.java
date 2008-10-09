package org.softevo.mutation.run;

import org.softevo.mutation.javaagent.MutationForRun;

public class TestForIds {
	public static void main(String[] args) {
		System
				.setProperty("mutation.file",
						"scratch2/schuler/mutation-test-config/res-back/mutation-task-49.txt");
		MutationForRun.getInstance();
	}
}
