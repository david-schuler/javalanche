package org.softevo.mutation.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Io {

	private static Logger logger = Logger.getLogger(Io.class);


	public static void writeFile(String content, File file){
		try {
			logger.info("Start writing: " + file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(content);
			bw.close();
			logger.info(file.getAbsoluteFile() + " written");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static List<Long> getIDsFromFile(File file) {
		List<Long> idList = new ArrayList<Long>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while (br.ready()) {
				String id = br.readLine();
				idList.add(Long.valueOf(id));
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return idList;
	}


	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			throw new IOException("File to large" + file.getAbsolutePath());
		}
		byte[] bytes = new byte[(int) length];
		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}
		is.close();
		return bytes;
	}
}
