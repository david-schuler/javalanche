package org.softevo.mutation.util;

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

import de.unisb.cs.st.invariants.abstractmodel.DaikonClassInvariants;

public class SerializeIo {

	private static Logger logger = Logger.getLogger(SerializeIo.class);

	public static void serializeToFile(Object o, File file) {
		logger.info(" -- Start serializing to file: " + file.getAbsolutePath());
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

	public static Object deserialize(String filename) {
		return deserialize(new File(filename));
	}

	public static <T> T get(File file) {
		Object o = deserialize(file);
		return (T) o;
	}

	public static <T> T get(String filename) {
		Object o = deserialize(filename);
		return (T) o;
	}

	public static void serializeToFile(Object o, String filename) {
		serializeToFile(o, new File(filename));
	}

	public static void main(String[] args) {
		DaikonClassInvariants dci = get("/scratch/schuler/subjects/jaxen-1.1.1/daikon-invariants.ser");
	}
}
