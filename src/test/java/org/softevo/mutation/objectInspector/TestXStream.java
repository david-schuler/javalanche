package org.softevo.mutation.objectInspector;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.softevo.mutation.io.XmlIo;

import com.thoughtworks.xstream.XStream;

public class TestXStream {

	@Test
	public void testXstream() {
		List<String> l = Arrays.asList(new String[] { "1", "2", "A", "B" });
		XStream xstream = new XStream();
		String xml = xstream.toXML(l);
		System.out.println(xml);
		File f = new File("./test-x-stream-testfile.xml");
		XmlIo.toXML(l, f);
		Object l2 = XmlIo.fromXml(f);
		System.out.println(l.equals(l2));
		l2.getClass();
		f.deleteOnExit();
	}
}
