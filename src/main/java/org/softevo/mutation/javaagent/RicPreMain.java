package org.softevo.mutation.javaagent;

import java.lang.instrument.Instrumentation;

public class RicPreMain {

	public static void premain(String agentArguments, Instrumentation instrumentation) {
		instrumentation.addTransformer(new RicFileTransformer());
	}

}
