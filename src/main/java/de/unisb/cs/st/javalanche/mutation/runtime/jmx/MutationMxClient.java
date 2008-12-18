package de.unisb.cs.st.javalanche.mutation.runtime.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.util.ArrayList;
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

public class MutationMxClient {

	private static final boolean DEBUG_ADD = false;

	public static boolean connectToAll(int i) {
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
				ObjectName objectName = new ObjectName(MXBeanRegisterer.OBJECT_NAME);
				Object numberOfMutations = mbsc.getAttribute(objectName, "NumberOfMutations");
				Object currentTest = mbsc.getAttribute(objectName, "CurrentTest");
				Object currentMutation = mbsc.getAttribute(objectName, "CurrentMutation");
				Object allMutations = mbsc.getAttribute(objectName, "Mutations");
				Object mutationsDuration = mbsc.getAttribute(objectName, "MutationDuration");
				Object testDuration = mbsc.getAttribute(objectName, "TestDuration");
				Object mutationSummary = mbsc.getAttribute(objectName, "MutationSummary");

				final RuntimeMXBean remoteRuntime =
		                ManagementFactory.newPlatformMXBeanProxy(
		                    mbsc,
		                    ManagementFactory.RUNTIME_MXBEAN_NAME,
		                    RuntimeMXBean.class);


		        System.out.println("Target VM : " + remoteRuntime.getName());
		        System.out.println("Running for : " + DurationFormatUtils.formatDurationHMS(remoteRuntime.getUptime()));






		        String mutationDurationFormatted = DurationFormatUtils.formatDurationHMS(Long.parseLong(mutationsDuration.toString()));
		        String testDurationFormatted = DurationFormatUtils.formatDurationHMS(Long.parseLong(testDuration.toString()));
		        if(DEBUG_ADD){
		        	System.out.println("Classpath: " + remoteRuntime.getClassPath());
		        	System.out.println("Args: " + remoteRuntime.getInputArguments());
		        	System.out.println("All Mutations: " + allMutations );
		        }
		        System.out.println(mutationSummary);
		        System.out.println("Current mutation (Running for: " + mutationDurationFormatted + "): " + currentMutation  );
		        System.out.println("Mutations tested: " + numberOfMutations);
		        System.out.println("Current test:   (Running for: " + testDurationFormatted + "): "+ currentTest);

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
			}
		}
		return true;
	}

	public static void main(String[] args) throws IOException {
		List<Integer> noConnection = new ArrayList<Integer>();
		for (int i = 0; i < 100; i++) {
			boolean result = connectToAll(i);
			if (!result) {
				noConnection.add(i);
			} else {
				System.out
						.println("--------------------------------------------------------------------------------");
			}

		}
		System.out.println("Got no connection for ids: " + noConnection);
		// if (false) {
		// // JMXServiceURL url = new JMXServiceURL(
		// // "service:jmx:rmi:///jndi/rmi://localhost:9999/server");
		// // JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		//
		// JMXServiceURL url = new JMXServiceURL(Main.ADDRESS);
		// JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		//
		// // Get an MBeanServerConnection
		// //
		// MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		// Integer beanCount = mbsc.getMBeanCount();
		//
		// try {
		// ObjectName objectName = new ObjectName(
		// "mutationTester:type=My2");
		// ObjectInstance objectInstance = mbsc
		// .getObjectInstance(objectName);
		// String className = objectInstance.getClassName();
		// MBeanInfo beanInfo = mbsc.getMBeanInfo(objectName);
		// for (MBeanAttributeInfo attr : beanInfo.getAttributes()) {
		// System.out.println(attr.getName());
		// Object attribute = mbsc.getAttribute(objectName, attr
		// .getName());
		// System.out.println("Value " + attribute);
		// }
		// Object attribute = mbsc.getAttribute(objectName, "NextSafe");
		// System.out.println("Before: " + attribute);
		// mbsc.setAttribute(objectName, new Attribute("NextSafe",
		// Boolean.FALSE));
		// attribute = mbsc.getAttribute(objectName, "NextSafe");
		// System.out.println("After: " + attribute);
		// System.out.println(className);
		// } catch (InstanceNotFoundException e) {
		// e.printStackTrace();
		// } catch (MalformedObjectNameException e) {
		// e.printStackTrace();
		// } catch (NullPointerException e) {
		// e.printStackTrace();
		// } catch (IntrospectionException e) {
		// e.printStackTrace();
		// } catch (ReflectionException e) {
		// e.printStackTrace();
		// } catch (AttributeNotFoundException e) {
		// e.printStackTrace();
		// } catch (MBeanException e) {
		// e.printStackTrace();
		// } catch (InvalidAttributeValueException e) {
		// e.printStackTrace();
		// }
		//
		// System.out.println("Number of beans " + beanCount);
		// }
	}
}
