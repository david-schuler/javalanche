/*
* Copyright (C) 2009 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.unisb.cs.st.javalanche.coverage;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.junit.Test;

import de.unisb.cs.st.ds.util.io.XmlIo;

public class CoverageMutationListenerTest {

	private static Map<String, Map<Integer, Integer>> loadTrace(String fileName) {
		ObjectInputStream ois = null;

		int numClasses, numLines;
		String className;

		Map<String, Map<Integer, Integer>> classMap;
		Map<Integer, Integer> lineMap;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(
					new GZIPInputStream(new FileInputStream(fileName))));
			numClasses = ois.readInt();
			System.out.println("Classes " + numClasses);
			classMap = new HashMap<String, Map<Integer, Integer>>();
			for (int i = 0; i < numClasses; i++) {

				className = ois.readUTF();
				System.out.println("Class name: " + className);
				numLines = ois.readInt();
				System.out.println("Lines " + numLines);
				lineMap = new HashMap<Integer, Integer>();
				for (int j = 0; j < numLines; j++) {
					int i1 = ois.readInt();
					int i2 = ois.readInt();
					System.out.println(i1 + "=" + i2);
					lineMap.put(i1, i2);
				}
				classMap.put(className, lineMap);
			}
			ois.close();
		} catch (Exception e) {
			throw new RuntimeException("Error reading trace. File name "
					+ fileName, e);
		}

		return classMap;
	}

	private static AtomicBoolean lock = new AtomicBoolean(false);

	public void generateData(String fileName) throws FileNotFoundException,
			IOException {
		ObjectOutputStream oos = new ObjectOutputStream(
				new BufferedOutputStream(new GZIPOutputStream(
						new FileOutputStream(fileName))));

		oos.writeInt(12);
		oos
				.writeUTF("xcom.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat@access$000");
		oos.writeInt(1);
		oos.writeInt(36);
		oos.writeInt(13);
		oos.writeUTF("xcom.thoughtworks.xstream.core.util.Pool@putInPool");
		oos.writeInt(5);
		oos.writeInt(68);
		oos.writeInt(226);
		oos.writeInt(69);
		oos.writeInt(226);
		oos.writeInt(70);
		oos.writeInt(227);
		oos.writeInt(66);
		oos.writeInt(254);
		oos.writeInt(67);
		oos.writeInt(244);
		oos
				.writeUTF("xcom.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat$1@newInstance");
		oos.writeInt(3);
		oos.writeInt(47);
		oos.writeInt(13);
		oos.writeInt(49);
		oos.writeInt(13);
		oos.writeInt(48);
		oos.writeInt(13);
		oos
				.writeUTF("xcom.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter@<init>");
		oos.writeInt(1);
		oos.writeInt(25);
		oos.writeInt(1);
		oos
				.writeUTF("xcom.thoughtworks.xstream.converters.basic.DateConverter@fromString");
		oos.writeInt(1);
		oos.writeInt(136);
		oos.writeInt(243);
		oos
				.writeUTF("xcom.thoughtworks.xstream.converters.basic.DateConverter@<init>");
		oos.writeInt(12);
		oos.writeInt(51);
		oos.writeInt(1);
		oos.writeInt(52);
		oos.writeInt(1);
		oos.writeInt(72);
		oos.writeInt(1);
		oos.writeInt(73);
		oos.writeInt(1);
		oos.writeInt(106);
		oos.writeInt(1);
		oos.writeInt(105);
		oos.writeInt(1);
		oos.writeInt(119);
		oos.writeInt(1);
		oos.writeInt(118);
		oos.writeInt(1);
		oos.writeInt(125);
		oos.writeInt(4);
		oos.writeInt(124);
		oos.writeInt(5);
		oos.writeInt(121);
		oos.writeInt(1);
		oos.writeInt(128);
		oos.writeInt(1);
		oos.writeUTF("xcom.thoughtworks.xstream.core.util.Pool@fetchFromPool");

		oos.writeInt(13);
		oos.writeInt(42);
		oos.writeInt(1);
		oos.writeInt(43);
		oos.writeInt(1);
		oos.writeInt(40);
		oos.writeInt(233);
		oos.writeInt(41);
		oos.writeInt(220);
		oos.writeInt(47);
		oos.writeInt(204);
		oos.writeInt(44);
		oos.writeInt(4);
		oos.writeInt(55);
		oos.writeInt(183);
		oos.writeInt(59);
		oos.writeInt(9);
		oos.writeInt(58);
		oos.writeInt(9);
		oos.writeInt(57);
		oos.writeInt(7);
		oos.writeInt(56);
		oos.writeInt(177);
		oos.writeInt(62);
		oos.writeInt(187);
		oos.writeInt(61);
		oos.writeInt(178);
		oos
				.writeUTF("xcom.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat$1@<init>");
		oos.writeInt(1);
		oos.writeInt(45);
		oos.writeInt(5);
		oos
				.writeUTF("xcom.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat@<init>");
		oos.writeInt(5);
		oos.writeInt(42);
		oos.writeInt(5);
		oos.writeInt(43);
		oos.writeInt(5);
		oos.writeInt(44);
		oos.writeInt(5);
		oos.writeInt(45);
		oos.writeInt(5);
		oos.writeInt(53);
		oos.writeInt(5);
		oos
				.writeUTF("xcom.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat@fetchFromPool");
		oos.writeInt(5);
		oos.writeInt(76);
		oos.writeInt(181);
		oos.writeInt(77);
		oos.writeInt(184);
		oos.writeInt(79);
		oos.writeInt(170);
		oos.writeInt(74);
		oos.writeInt(232);
		oos.writeInt(75);
		oos.writeInt(182);
		oos
				.writeUTF("xcom.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat@parse");
		oos.writeInt(3);
		oos.writeInt(69);
		oos.writeInt(252);
		oos.writeInt(65);
		oos.writeInt(243);
		oos.writeInt(67);
		oos.writeInt(159);
		oos.writeUTF("xcom.thoughtworks.xstream.core.util.Pool@<init>");
		oos.writeInt(6);
		oos.writeInt(30);
		oos.writeInt(5);
		oos.writeInt(34);
		oos.writeInt(5);
		oos.writeInt(35);
		oos.writeInt(5);
		oos.writeInt(32);
		oos.writeInt(5);
		oos.writeInt(33);
		oos.writeInt(5);
		oos.writeInt(36);
		oos.writeInt(5);
		oos.flush();
		oos.close();
	}

	 @Test
	public void testDebugB() {

	}
	public void testDebug4() {
		final Map<String, String> map = new ConcurrentHashMap<String, String>();
		for (int i = 0; i < 20; i++) {
			Thread t = getWritingThread(map);
			t.start();
		}
		Thread t2 = new Thread() {
			public void run() {
				while (true) {
					try {
						Map<String, String> mymap = map;
					int size = map.size();
					Iterator<String> it = map.keySet().iterator();
					int count = 0;
					while (it.hasNext()) {
						it.next();
						count++;
					}
					if (count != size) {
						lock.set(true);
							System.out.println(size + " " + count + " "
									+ map.size());
						System.out
								.println("CoverageMutationListenerTest.testDebug4()");
						lock.set(false);
					}
					} catch (ConcurrentModificationException e) {
						// e.printStackTrace();
					}
					// System.out.println(size);
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t2.start();
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	private Thread getWritingThread(final Map<String, String> map) {
		Thread t = new Thread() {
			public void run() {
				Random r = new Random();
				while (true) {
					String val = r.nextInt() + "";
					if (!lock.get()) {
						// System.out.println("Puting val");
						map.put(val, val);
					} else {
						// System.out.println("SIZE " + map.size());
					}
					try {
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		return t;
	}

	// @Test
	public void testDebug() throws FileNotFoundException, IOException {
		generateData("TestFile.gz");
		loadTrace("TestFile.gz");
	}

	// @Test

	public void testDebug2() {
		String home = System.getProperty("user.home");

		String name = home
				+ "/xcom.thoughtworks.xstream.converters.extended.ISO8601GregorianCalendarConverterTest.testIsThreadSafe.gz";
		// "/xcom.thoughtworks.xstream.converters.basic.DateConverterTest.testIsThreadSafe.gz";
		loadTrace(name);
	}

	public void testDebug1() {
		String home = System.getProperty("user.home");
		assertTrue(home.contains("schuler"));
		File f = new File(
				home
						+ "/xcom.thoughtworks.xstream.converters.extended.ISO8601GregorianCalendarConverterTest.testIsThreadSafe.gz.xml");
		assertTrue(f.exists());
		Map<String, Map<Integer, Integer>> map = (Map<String, Map<Integer, Integer>>) XmlIo
				.fromXml(f);
		Set<String> keySet = map.keySet();
		for (String string : keySet) {
			System.out.println(string);
		}

		File f2 = new File("mutation-files/tracer/line/0/");
		f.mkdirs();
		System.out.println(map);
		CoverageMutationListener coverageMutationListener = new CoverageMutationListener();
		coverageMutationListener.start();
		coverageMutationListener.testStart("TestTest");
		// coverageMutationListener.serializeHashMap(map, "TestTest");
		Map<String, Map<String, Map<Integer, Integer>>> load = CoverageTraceUtil
				.loadLineCoverageTrace("0");

		assertEquals(map, load.get("TestTest"));
	}

	public void testSerializeHashMap() {
		File f = new File("mutation-files/tracer/line/0/");
		f.mkdirs();
		CoverageMutationListener coverageMutationListener = new CoverageMutationListener();
		Map<String, Map<Integer, Integer>> classMapCopy = new HashMap<String, Map<Integer, Integer>>();
		classMapCopy.put(null, null);
		classMapCopy.put("a", new HashMap<Integer, Integer>());
		classMapCopy.put("b", new HashMap<Integer, Integer>());
		assertEquals(3, classMapCopy.size());
		coverageMutationListener.start();
		coverageMutationListener.testStart("TestTest");
		// coverageMutationListener.serializeHashMap(classMapCopy);
	}

}
