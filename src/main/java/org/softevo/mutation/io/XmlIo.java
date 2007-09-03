package org.softevo.mutation.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

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
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file));
			sb = new StringBuilder();
			while (bufferedReader.ready()) {
				sb.append(bufferedReader.readLine());
			}

			XStream xStream = new XStream();
			logger.info("Reading object from xml file: " + file.getAbsoluteFile());
			resultObject = xStream.fromXML(sb.toString());
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultObject;
	}
}
