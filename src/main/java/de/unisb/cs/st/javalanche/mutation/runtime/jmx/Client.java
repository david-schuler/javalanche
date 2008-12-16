package de.unisb.cs.st.javalanche.mutation.runtime.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.util.Arrays;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.time.DurationFormatUtils;

public class Client {

	public static void connectToAll(int i) {
		JMXConnector jmxc = null;
		JMXServiceURL url = null;
		try {
			url = new JMXServiceURL(MXBeanRegisterer.ADDRESS + i);
			jmxc = JMXConnectorFactory.connect(url, null);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not connect to address: " + url);
			// e.printStackTrace();
		}
		if (jmxc != null) {
			try {
				MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
				ObjectName objectName = new ObjectName(MXBeanRegisterer.OBJECT_NAME);
				Object attribute = mbsc.getAttribute(objectName, "NextSafe");
				System.out.println("Before: " + attribute);
				mbsc.setAttribute(objectName, new Attribute("NextSafe",
						Boolean.FALSE));
				attribute = mbsc.getAttribute(objectName, "NextSafe");
				System.out.println("After:  " + attribute);
				final RuntimeMXBean remoteRuntime =
		                ManagementFactory.newPlatformMXBeanProxy(
		                    mbsc,
		                    ManagementFactory.RUNTIME_MXBEAN_NAME,
		                    RuntimeMXBean.class);


		        System.out.println("Target VM : " + remoteRuntime.getName());
		        System.out.println("Running for : " + DurationFormatUtils.formatDurationHMS(remoteRuntime.getUptime()));
		        System.out.println("Classpath: " + remoteRuntime.getClassPath());
		        System.out.println("Args: " + remoteRuntime.getInputArguments());

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
			} catch (InvalidAttributeValueException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		for (int i = 0; i < 10; i++) {
			connectToAll(i);
		}
//		if (false) {
//			// JMXServiceURL url = new JMXServiceURL(
//			// "service:jmx:rmi:///jndi/rmi://localhost:9999/server");
//			// JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
//
//			JMXServiceURL url = new JMXServiceURL(Main.ADDRESS);
//			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
//
//			// Get an MBeanServerConnection
//			//
//			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
//			Integer beanCount = mbsc.getMBeanCount();
//
//			try {
//				ObjectName objectName = new ObjectName(
//						"mutationTester:type=My2");
//				ObjectInstance objectInstance = mbsc
//						.getObjectInstance(objectName);
//				String className = objectInstance.getClassName();
//				MBeanInfo beanInfo = mbsc.getMBeanInfo(objectName);
//				for (MBeanAttributeInfo attr : beanInfo.getAttributes()) {
//					System.out.println(attr.getName());
//					Object attribute = mbsc.getAttribute(objectName, attr
//							.getName());
//					System.out.println("Value " + attribute);
//				}
//				Object attribute = mbsc.getAttribute(objectName, "NextSafe");
//				System.out.println("Before: " + attribute);
//				mbsc.setAttribute(objectName, new Attribute("NextSafe",
//						Boolean.FALSE));
//				attribute = mbsc.getAttribute(objectName, "NextSafe");
//				System.out.println("After: " + attribute);
//				System.out.println(className);
//			} catch (InstanceNotFoundException e) {
//				e.printStackTrace();
//			} catch (MalformedObjectNameException e) {
//				e.printStackTrace();
//			} catch (NullPointerException e) {
//				e.printStackTrace();
//			} catch (IntrospectionException e) {
//				e.printStackTrace();
//			} catch (ReflectionException e) {
//				e.printStackTrace();
//			} catch (AttributeNotFoundException e) {
//				e.printStackTrace();
//			} catch (MBeanException e) {
//				e.printStackTrace();
//			} catch (InvalidAttributeValueException e) {
//				e.printStackTrace();
//			}
//
//			System.out.println("Number of beans " + beanCount);
//		}
	}
}
