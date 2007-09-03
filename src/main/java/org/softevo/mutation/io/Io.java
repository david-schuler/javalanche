package org.softevo.mutation.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class Io {

	private static Logger logger = Logger.getLogger(Io.class);


	public static void writeFile(String content, File file){
		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(content);
			bw.close();
			logger.info(file.getAbsoluteFile() + " written");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
