/*
* Copyright (C) 2010 Saarland University
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

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;
import de.unisb.cs.st.javalanche.mutation.results.persistence.HibernateUtil;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;
import de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestDriver;

public class MutationMxClient {


	private static Logger logger = Logger.getLogger(MutationMxClient.class);

	private static final boolean DEBUG_ADD = false;

	public static boolean connect(int i) {
		JMXConnector jmxc = null;
		JMXServiceURL url = null;

		try {
			url = new JMXServiceURL(MXBeanRegisterer.ADDRESS + i);
			jmxc = JMXConnectorFactory.connect(url, null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			return false;
			// System.out.println("Could not connect to address: " + url);
			// e.printStackTrace();
		}
		if (jmxc != null) {
			try {
				MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
				ObjectName objectName = new ObjectName(
						MXBeanRegisterer.OBJECT_NAME);
				Object numberOfMutations = mbsc.getAttribute(objectName,
						"NumberOfMutations");
				Object currentTest = mbsc.getAttribute(objectName,
						"CurrentTest");
				Object currentMutation = mbsc.getAttribute(objectName,
						"CurrentMutation");
				Object allMutations = mbsc
						.getAttribute(objectName, "Mutations");
				Object mutationsDuration = mbsc.getAttribute(objectName,
						"MutationDuration");
				Object testDuration = mbsc.getAttribute(objectName,
						"TestDuration");
//				Object mutationSummary = mbsc.getAttribute(objectName,
//						"MutationSummary");

				final RuntimeMXBean remoteRuntime = ManagementFactory
						.newPlatformMXBeanProxy(mbsc,
								ManagementFactory.RUNTIME_MXBEAN_NAME,
								RuntimeMXBean.class);

				final MemoryMXBean remoteMemory = ManagementFactory
						.newPlatformMXBeanProxy(mbsc,
								ManagementFactory.MEMORY_MXBEAN_NAME,
								MemoryMXBean.class);
				System.out.print("Connection: " + i + "  ");
				System.out.println("Target VM: " + remoteRuntime.getName()
						+ " - " + remoteRuntime.getVmVendor() + " - "
						+ remoteRuntime.getSpecVersion() + " - "
						+ remoteRuntime.getVmVersion());
				System.out.println("Running for: "
						+ DurationFormatUtils.formatDurationHMS(remoteRuntime
								.getUptime()));
				System.out.println("Memory usage: Heap - "
						+ formatMemory(remoteMemory.getHeapMemoryUsage()
								.getUsed())
						+ "  Non Heap - "
						+ formatMemory(remoteMemory.getNonHeapMemoryUsage()
								.getUsed()));

				String mutationDurationFormatted = DurationFormatUtils
						.formatDurationHMS(Long.parseLong(mutationsDuration
								.toString()));
				String testDurationFormatted = DurationFormatUtils
						.formatDurationHMS(Long.parseLong(testDuration
								.toString()));
				if (DEBUG_ADD) {
					System.out.println("Classpath: "
							+ remoteRuntime.getClassPath());
					System.out.println("Args: "
							+ remoteRuntime.getInputArguments());
					System.out.println("All Mutations: " + allMutations);
				}
//				System.out.println(mutationSummary);
				System.out.println("Current mutation (Running for: "
						+ mutationDurationFormatted + "): " + currentMutation);
				System.out.println("Mutations tested: " + numberOfMutations);
				System.out.println("Current test:   (Running for: "
						+ testDurationFormatted + "): " + currentTest);


			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MalformedObjectNameException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (AttributeNotFoundException e) {
				e.printStackTrace();
			} catch (InstanceNotFoundException e) {
				e.printStackTrace();
			} catch (MBeanException e) {
				e.printStackTrace();
			} catch (ReflectionException e) {
				e.printStackTrace();
			}finally{
				try {
					jmxc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private static String formatMemory(long used) {
		double mem = used / (1024. * 1024.);
		return String.format("%.2f MB", mem);
	}

	public static void main(String[] args) throws IOException {
		List<Integer> noConnection = new ArrayList<Integer>();
		boolean oneConnection = false;
		for (int i = 0; i < 100; i++) {
			boolean result = connect(i);
			if (!result) {
				noConnection.add(i);
			} else {
				oneConnection = true;
				System.out
						.println("--------------------------------------------------------------------------------");
			}

		}
		if (!oneConnection) {
			System.out.println("Got no connection for ids: " + noConnection);
		}
		analyzeDB();
	}

	@SuppressWarnings("unchecked")
	private static void analyzeDB() {
		Session session = HibernateUtil.openSession();
		Transaction transaction = session.beginTransaction();
		Query query = session
				.createQuery("from Mutation WHERE mutationResult != null AND className LIKE '"
						+ MutationProperties.PROJECT_PREFIX + "%' ");
		List<Mutation> list = query.list();
		System.out.printf("Already executed mutations for project %s:  %d. \n",
				MutationProperties.PROJECT_PREFIX, list.size());
		RunInfo r = getRunInfo(list);
		transaction.commit();
		session.close();
		long averageRuntimeMutation = r.totalDuration / r.mutations;
		long averageRuntimeTest = r.totalDuration / r.tests;
		System.out.printf("Already executed mutations for project %s:  %d. Number of tests: %d  Total Runtime: %s\n",
				MutationProperties.PROJECT_PREFIX, r.mutations, r.tests, DurationFormatUtils
						.formatDurationHMS(r.totalDuration));
		System.out.printf("Average mutation runtime: %s\n", DurationFormatUtils
				.formatDurationHMS(averageRuntimeMutation));
		System.out.printf("Average test runtime: %s\n", DurationFormatUtils
				.formatDurationHMS(averageRuntimeTest));
		System.out.printf("Restarts %d.\n", r.restarts);

	}

	private static RunInfo getFastRunInfo(List<Mutation> list) {
		long totalDuration = QueryManager.getResultFromSQLCountQuery("SELECT sum(duration) FROM TestMessage T");
		long mutations = QueryManager.getResultFromSQLCountQuery("SELECT count(*) FROM Mutation M WHERE mutationResult_id AND className LIKE '" + MutationProperties.PROJECT_PREFIX + "%'");
		long restarts = QueryManager.getResultFromSQLCountQuery("SELECT count(*) FROM TestMessage T WHERE message LIKE '" + MutationTestDriver.RESTART_MESSAGE + "%'");
		long tests = QueryManager.getResultFromSQLCountQuery("SELECT count(*) FROM TestMessage T");
		return new RunInfo(totalDuration, (int) mutations, (int) restarts, tests);
	}

	public static RunInfo getRunInfo(List<Mutation> list) {
		long totalDuration = 0;
		int mutations = 0;
		int restarts = 0;
		long tests = 0;

		for (Mutation mutation : list) {
			mutations++;
			MutationTestResult mutationResult = mutation.getMutationResult();
			Collection<TestMessage> allTestMessages = mutationResult
					.getAllTestMessages();
			for (TestMessage testMessage : allTestMessages) {
				tests++;
				long pre = totalDuration;
				totalDuration += testMessage.getDuration();
				if(pre>totalDuration){
					logger.warn("Overflow when computing total duration");
				}
				if (testMessage.getMessage().equals(
						MutationTestDriver.RESTART_MESSAGE)) {
					restarts++;
				}
			}
		}
		return new RunInfo(totalDuration, mutations, restarts, tests);
	}

	private static class RunInfo {
		long totalDuration = 0;
		int mutations = 0;
		int restarts = 0;
		long tests = 0;

		public RunInfo(long totalDuration, int mutations, int restarts,
				long tests) {
			super();
			this.totalDuration = totalDuration;
			this.mutations = mutations;
			this.restarts = restarts;
			this.tests = tests;
		}

	}
}
