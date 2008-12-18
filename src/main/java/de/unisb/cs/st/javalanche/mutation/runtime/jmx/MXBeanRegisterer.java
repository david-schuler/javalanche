package de.unisb.cs.st.javalanche.mutation.runtime.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;

public class MXBeanRegisterer {

	public static final String ADDRESS = "service:jmx:rmi:///jndi/rmi://localhost:9994/server";
	public static final String OBJECT_NAME = "mutationTester:type=MutationTester";

	public static MutationMX registerMutationMXBean(int id) {
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
			JMXConnectorServer cs = JMXConnectorServerFactory
					.newJMXConnectorServer(url, env, mbs);
			cs.start();
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
			e.printStackTrace();
		}
		return mbean;
	}

	public static void main(String[] args) {
		registerMutationMXBean(args.length);
	}

	public static void unregister(MutationMXMBean bean) {
		// TODO Auto-generated method stub

	}
}
