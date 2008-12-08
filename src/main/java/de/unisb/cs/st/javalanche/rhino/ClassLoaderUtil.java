package de.unisb.cs.st.javalanche.rhino;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ClassLoaderUtil {

	private static Logger logger = Logger.getLogger(ClassLoaderUtil.class);

	public static ClassLoader getClassLoader(ClassLoader parent) {
		String property = System.getProperty("java.class.path");
		String[] entries = property.split(":");
		List<URL> urls = new ArrayList<URL>();
		for (String entry : entries) {
			File f = new File(entry);
			try {
				urls.add(f.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		ClassLoader classLoader = new MyURLClassLoader(
				urls.toArray(new URL[0]), parent);
		return classLoader;
	}

	/**
	 * Class loader that tries to load classes from a package in this class
	 * loader while delegating all other class loads to a parent class loader.
	 *
	 * @author David Schuler
	 *
	 */
	public static class MyURLClassLoader extends URLClassLoader {

		public MyURLClassLoader(URL[] urls, ClassLoader parent) {
			super(urls, parent);
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			Class c = null;
			if (name.startsWith("org.mozilla.javascript.tools.shell")) {
				System.out
						.println("MyURLClassLoader.loadClass() class " + name);
				if (null != findLoadedClass(name)) {
					logger.debug(this
							+ " -  Class already loaded by this classloader "
							+ name);
				} else {
					logger.debug(this
							+ " -  Class not loaded by this classloader "
							+ name);

				}
				c = findLoadedClass(name);

				if (c == null) {
					try {

						logger
								.debug("Trying to load class in special class loader "
										+ name);
						c = findClass(name);
					} catch (ClassNotFoundException cnfe) {
					}
				}
			}

			if (c == null) {
				if (getParent() != null) {
					c = getParent().loadClass(name);
				} else {
					c = getSystemClassLoader().loadClass(name);
				}
			}

			return c;
		}
	}

}
