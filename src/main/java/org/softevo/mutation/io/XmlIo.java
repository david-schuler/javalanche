package org.softevo.mutation.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.BaseException;

public class XmlIo {

	private static Logger logger = Logger.getLogger(XmlIo.class);

	public static void toXML(Object o, File file) {
		XStream xStream = new XStream();
		String xml = xStream.toXML(o);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(xml);
			bw.close();
			logger.info(file.getAbsoluteFile() + " written");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Object fromXml(File file) {
		StringBuilder sb = null;
		Object resultObject = null;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			sb = new StringBuilder();
			while (bufferedReader.ready()) {
				sb.append(bufferedReader.readLine());
			}

			XStream xStream = new XStream();
			logger.info("XML file read. Size: " + sb.length());
			logger.info("Start reading object from xml file: "
					+ file.getAbsoluteFile());

			String xml = sb.toString();
			logger.info("got string - size: " + xml.length());
			resultObject = xStream.fromXML(xml);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (BaseException be) {
			logger.warn("Exception thrown when deserializing " + be);
			be.printStackTrace();
		} finally {
			logger.info("Reading finished: " + file.getAbsoluteFile());
			logger.info("Read Object of type " + resultObject.getClass());
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return resultObject;
	}
}
