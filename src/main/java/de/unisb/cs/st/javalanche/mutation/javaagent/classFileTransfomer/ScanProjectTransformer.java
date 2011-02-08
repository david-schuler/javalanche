/*
* Copyright (C) 2010 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import de.unisb.cs.st.javalanche.mutation.javaagent.classFileTransfomer.mutationDecision.Excludes;
import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

/**
 * Transformer that collects all classes with the current mutation prefix, and
 * saves them to a file. This file can later be used to exclude classes from
 * mutation testing.
 * 
 */
public class ScanProjectTransformer implements ClassFileTransformer {

	private List<String> classes = new ArrayList<String>();


	public ScanProjectTransformer() {
		Runtime r = Runtime.getRuntime();
		r.addShutdownHook(new Thread() {
			public void run() {
				Excludes.getInstance().addClasses(classes);
				Excludes.getInstance().writeFile();
				System.out.println("Got " + classes.size()
						+ " classes with prefix: "
						+ MutationProperties.PROJECT_PREFIX);
			}
		});
	}
	
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String classNameWithDots = className.replace('/', '.');
		if (classNameWithDots.startsWith(MutationProperties.PROJECT_PREFIX)) {
			classes.add(classNameWithDots);
		}
		return classfileBuffer;
	}


}
