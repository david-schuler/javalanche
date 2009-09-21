/*
* Copyright (C) 2009 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import de.unisb.cs.st.ds.util.Util;
import de.unisb.cs.st.ds.util.io.Io;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.BytecodeTasks;
import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationScannerTransformer;
import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.MutationDecision;
import de.unisb.cs.st.javalanche.mutation.mutationPossibilities.MutationPossibilityCollector;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class EclipseScanner implements ClassFileTransformer {

	public static final String TEST_SUITE_KEY = "testSuite";

	public static final String RUN_CONFIG_KEY = "runConfig";

	private static Logger logger = Logger.getLogger(EclipseScanner.class);

	private MutationPossibilityCollector mpc = new MutationPossibilityCollector();

	private MutationScannerTransformer mutationScannerTransformer = new MutationScannerTransformer(
			mpc);

	private MutationDecision md = new MutationDecision() {

		Set<String> classes = getClasses();

		public boolean shouldBeHandled(String classNameWithDots) {
			return classes.contains(classNameWithDots);
		}

		private Set<String> getClasses() {
			Set<String> classSet = new HashSet<String>();
			String classesProperty = System
					.getProperty(MutationProperties.CLASSES_TO_MUTATE_KEY);
			logger.warn("Looking for these classes " + classesProperty);
			if (classesProperty == null) {
				String message = "No files to scan are specified. Property "
						+ MutationProperties.CLASSES_TO_MUTATE_KEY
						+ " not set ";
				throw new RuntimeException(message);
			}
			classesProperty = classesProperty.replace("\"", "");
			String[] split = classesProperty.split(":");
			for (String st : split) {
				classSet.add(st);
			}
			System.out.println("Looking for these classes " + classSet);
			return classSet;
		}

	};

	public EclipseScanner() {
		MutationScanner.addShutDownHook();
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Thread() {
			@Override
			public void run() {
				Set<Long> covered = MutationCoverageFile.getCoveredMutations();
				List<Long> mutationIds = QueryManager
						.getMutationsWithoutResult(covered, 1000);
				StringBuilder sb = new StringBuilder();
				for (Long l : mutationIds) {
					sb.append(l);
					sb.append("\n");
				}
				File idFile = new File("mutation-ids-"
						+ MutationProperties.TEST_SUITE + ".txt");
				Io.writeFile(sb.toString(), idFile);
				System.out.println("TESTSETETS");
			}

		});
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className != null) {
			try {
				String classNameWithDots = className.replace('/', '.');
				if (BytecodeTasks.shouldIntegrate(classNameWithDots)) {
					classfileBuffer = BytecodeTasks.integrateTestSuite(
							classfileBuffer, classNameWithDots);
				}
				logger.debug("Handling class " + classNameWithDots);
				if (md.shouldBeHandled(classNameWithDots)) {
					if (!isTest(classfileBuffer)) {
						classfileBuffer = mutationScannerTransformer
								.transformBytecode(classfileBuffer);
						logger.warn(mpc.size()
								+ " mutation possibilities found for class "
								+ className);
						associateRunConfig(mpc);

						mpc.updateDB();
						mpc.clear();
					}
				} else {
					logger.debug("Skipping class " + className);
				}

			} catch (Throwable t) {
				t.printStackTrace();
				String message = "Exception during instrumentation";
				logger.warn(message, t);
				logger.warn(Util.getStackTraceString());
				System.out.println(message + " - exiting");
				System.exit(1);
			}
		}
		return classfileBuffer;
	}

	private static void associateRunConfig(MutationPossibilityCollector mpc) {
		Session session = HibernateUtil.openSession();
		Transaction tx = session.beginTransaction();

		int saved = 0;
		List<Mutation> possibilities = mpc.getPossibilities();
		for (Mutation mutation : possibilities) {
			logger.info("Setting add info "
					+ MutationProperties.ECLIPSE_RUN_CONFIG_NAME
					+ " for mutation  " + mutation.getId());
			Long id = mutation.getId();
			if (id != null) {
				Mutation mutationFromDB = (Mutation) session.get(
						Mutation.class, id);
				mutationFromDB.setAddInfo(getPropertyString());
				saved++;
				if (saved % 20 == 0) { // 20, same as the JDBC batch size
					// flush a batch of inserts and release memory:
					// see
					// http://www.hibernate.org/hib_docs/reference/en/html/batch.html
					session.flush();
					session.clear();
				}
			} else {
				logger.warn("Mutation got no id:  " + mutation);
			}
		}
		if (session.isOpen()) {
			tx.commit();
			session.close();
		}
	}

	private static String getPropertyString() {
		Map<String, String> props = new HashMap<String, String>();
		props.put(RUN_CONFIG_KEY, MutationProperties.ECLIPSE_RUN_CONFIG_NAME);
		props.put(TEST_SUITE_KEY, MutationProperties.TEST_SUITE);
		return encodeProperties(props);
	}

	private static String encodeProperties(Map<String, String> props) {
		StringBuilder sb = new StringBuilder();
		Set<Entry<String, String>> entrySet = props.entrySet();
		for (Entry<String, String> entry : entrySet) {
			sb.append(entry.getKey());
			sb.append('=');
			sb.append(entry.getValue());
			sb.append(':');
		}
		return sb.toString();
	}

	private boolean isTest(byte[] bytecode) {
		ClassReader cr = new ClassReader(bytecode);
		ClassWriter cw = new ClassWriter(0);
		IsTestVisitor cv = new IsTestVisitor(cw);
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		return cv.isTest();
	}

	public static Map<String, String> decodeProperties(String properties) {
		String[] split = properties.split(":");
		Map<String, String> result = new HashMap<String, String>();
		for (String string : split) {
			int index = string.indexOf('=');
			String key = string.substring(0, index);
			String value = string.substring(index + 1);
			result.put(key, value);
		}
		return result;
	}
}
