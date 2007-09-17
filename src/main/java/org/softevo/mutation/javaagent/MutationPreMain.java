package org.softevo.mutation.javaagent;

import java.lang.instrument.Instrumentation;

import org.softevo.mutation.properties.MutationProperties;

public class MutationPreMain {

	public static boolean scanningEnabled;

	public static void premain(String agentArguments,
			Instrumentation instrumentation)  {
		try{
		String scanForMutations = System
				.getProperty(MutationProperties.SCAN_FOR_MUTATIONS);
		if (scanForMutations != null) {
			if (!scanForMutations.equals("false")) {
				scanningEnabled = true;
				System.out.println("Scanning for mutations");
   				instrumentation.addTransformer(new MutationScanner());
				return;
			}
		}
		instrumentation.addTransformer(new MutationFileTransformer());
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
