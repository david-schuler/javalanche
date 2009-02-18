package de.unisb.cs.st.javalanche.mutation.runtime.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;
import javax.naming.ServiceUnavailableException;

import org.apache.log4j.Logger;

public class MXBeanRegisterer {
	private static Logger logger = Logger.getLogger(MXBeanRegisterer.class);

	private static final int PORT = 9994;
	public static final String ADDRESS = "service:jmx:rmi:///jndi/rmi://localhost:"
			+ PORT + "/server";
	public static final String OBJECT_NAME = "mutationTester:type=MutationTester";

	private JMXConnectorServer connectorServer;

	public MutationMX registerMutationMXBean(int id) {
		MutationMX mbean = null;
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name;
			name = new ObjectName("mutationTester:type=MutationTester");
			mbean = new MutationMX();
			mbs.registerMBean(mbean, name);
			JMXServiceURL url;
			Map<String, Object> env = null;

			env = new HashMap<String, Object>();
			env.put(RMIConnectorServer.JNDI_REBIND_ATTRIBUTE, "true");

			// rmiregistry 9994 &
			String address = ADDRESS + id;
			url = new JMXServiceURL(address);
			connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(
					url, env, mbs);
			connectorServer.start();
			System.out.println("MBean registered under adress: " + address);
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			if (e.getCause() != null
					&& e.getCause() instanceof ServiceUnavailableException) {
				logger
						.warn("Could not bind JMX bean for this process. Most likely the rmiregistry is not started. Consider to start the rmiregistry "
								+ " $> rmiregistry " + PORT + " &");
				mbean = null;
			} else {
				e.printStackTrace();
			}

		}
		return mbean;
	}

	public static void main(String[] args) {
		new MXBeanRegisterer().registerMutationMXBean(args.length);
	}

	public void unregister(MutationMXMBean bean) {

		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = new ObjectName(
					"mutationTester:type=MutationTester");
			mbs.unregisterMBean(name);
			if (connectorServer != null) {
				connectorServer.stop();

			}
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
