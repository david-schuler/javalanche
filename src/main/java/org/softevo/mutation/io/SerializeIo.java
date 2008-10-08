package org.softevo.mutation.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/**
 * Class to write and read (serialize and deserialize) objects to/from files.
 *
 * @author David Schuler
 *
 */
public class SerializeIo {

	private static Logger logger = Logger.getLogger(SerializeIo.class);

	/**
	 * Serializes an object to given file.
	 *
	 * @param o
	 *            the object to serialize
	 * @param file
	 *            the file to serialize to
	 */
	public static void serializeToFile(Object o, File file) {
		logger.info("Start serializing to file: " + file.getAbsolutePath());
		boolean serialized = false;
		try {
			ObjectOutput out = new ObjectOutputStream(
					new FileOutputStream(file));
			out.writeObject(o);
			out.close();
			serialized = true;
		} catch (FileNotFoundException e) {
			logger.info("Exception thrown: ", e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("Exception thrown: ", e);
			e.printStackTrace();
		} finally {
			if (serialized) {
				logger.info("Object serialized");
			} else {
				logger.warn("Object not serialized");
			}
		}
	}

	/**
	 * Serializes an object to given file.
	 *
	 * @param o
	 *            the object to serialize
	 * @param filename
	 *            the name of the file to serialize to
	 */
	public static void serializeToFile(Object o, String filename) {
		serializeToFile(o, new File(filename));
	}

	/**
	 * Reads an object from a file.
	 *
	 * @param file
	 *            the file to read from
	 * @return the read object
	 */
	public static Object deserialize(File file) {
		Object o = null;
		long time = System.currentTimeMillis();
		try {
			// logger.debug("Start reading: " + file + " Stacktrace:" +
			// Util.getStackTraceString());
			logger.info(" --Start reading: " + file.getAbsolutePath());
			ObjectInputStream in = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(file)));
			o = in.readObject();
			in.close();
			long timePassed = System.currentTimeMillis() - time;
			logger.info("Time needed " + timePassed);
		} catch (FileNotFoundException e) {
			logger.info("Exception thrown: ", e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("Exception thrown: ", e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			logger.info("Exception thrown: ", e);
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * Reads an object from a file.
	 *
	 * @param filename
	 *            the name of the file to read from
	 * @return the read object
	 */
	public static Object deserialize(String filename) {
		return deserialize(new File(filename));
	}

	/**
	 * Reads an object from a file and casts the object to the parameterized
	 * type.
	 *
	 * @param file
	 *            the file to read from
	 * @return the read object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(File file) {
		Object o = deserialize(file);
		return (T) o;
	}

	/**
	 * Reads an object from a file and casts the object to the parameterized
	 * type.
	 *
	 * @param filename
	 *            the name of the file to read from
	 * @return the read object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String filename) {
		Object o = deserialize(filename);
		return (T) o;
	}

}
