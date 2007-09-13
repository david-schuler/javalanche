package org.softevo.mutation.javaagent;

import java.lang.instrument.Instrumentation;

public class MutationPreMain {


	public static void premain(String agentArguments, Instrumentation instrumentation) {
		instrumentation.addTransformer(new MutationFileTransformer());
	}

}
