/*
* Copyright (C) 2011 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * Class does instrumentation of bytecode by using appropriate ASM
 * ClassVisitors.
 *
 *
 * @author schuler
 *
 */
public abstract class BytecodeTransformer {

	/**
	 * Factory that provides a ASM class visitor chain that processes the
	 * bytecode. The given {@link ClassWriter} ends the chain.
	 *
	 * @param cw
	 *            Classwriter to end the chain
	 *
	 * @return An ASM ClassVisitorChain
	 */
	protected abstract ClassVisitor classVisitorFactory(ClassWriter cw);

	/**
	 * Transforms the given bytecode
	 *
	 * @param bytecode
	 *            original bytecode
	 * @return The transformed bytecode.
	 */
	public final byte[] transformBytecode(byte[] bytecode) {
		ClassReader cr = new ClassReader(bytecode);
		return transformBytecode(cr);
	}

	/**
	 * Transforms he bytecode for given {@link ClassReader}
	 *
	 * @param cr
	 *            {@link ClassReader} for the class to transform
	 * @return The transformed bytecode.
	 */
	public final byte[] transformBytecode(ClassReader cr) {
		return transformBytecode(cr, ClassReader.EXPAND_FRAMES);
	}

	/**
	 * Transforms he bytecode for given {@link ClassReader}
	 *
	 * @param cr
	 *            {@link ClassReader} for the class to transform
	 * @param readerBitmask
	 *            the bitmask that is passed to the class reader
	 * @return The transformed bytecode.
	 */
	public final byte[] transformBytecode(ClassReader cr, int readerBitmask) {
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassVisitor cv = classVisitorFactory(cw);
		cr.accept(cv, readerBitmask);
		return cw.toByteArray();
	}

}
