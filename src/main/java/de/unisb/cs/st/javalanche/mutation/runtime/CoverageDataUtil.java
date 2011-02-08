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
package de.unisb.cs.st.javalanche.mutation.runtime;

import static org.objectweb.asm.Opcodes.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import de.unisb.cs.st.javalanche.mutation.bytecodeMutations.MutationMarker;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.persistence.QueryManager;

public class CoverageDataUtil {

	private static Logger logger = Logger.getLogger(CoverageDataUtil.class);

	public static void insertCoverageCalls(MethodVisitor mv, Mutation mutation) {
		Label endLabel = new Label();
		endLabel.info = new MutationMarker(false);
		Label mutationStartLabel = new Label();
		mutationStartLabel.info = new MutationMarker(true);
		mv.visitLabel(mutationStartLabel);
		Long id = mutation.getId();
		if (id == null) {
			Mutation mutationFromDb = QueryManager.getMutationOrNull(mutation);
			if (mutationFromDb != null) {
				id = mutationFromDb.getId();
			} else {
				QueryManager.saveMutation(mutation);
				id = mutation.getId();
			}
		}
		logger.debug("Inserting Coverage calls for:  " + id + " " + mutation);
		mv.visitLdcInsn(id);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/CoverageDataRuntime",
				"touch", "(J)V");
		mv.visitLabel(endLabel);
	}

	private static int saveCount;

	public static void saveAndEmpty() {
		Map<Long, Set<String>> coverageData = CoverageDataRuntime
				.getCoverageData();
		Set<String> testsRun = CoverageDataRuntime.getTestsRun();
		logger.info("Saving coverage data for " + coverageData.size()
				+ " mutations");
		MutationCoverageFile.saveCoverageData(coverageData);
		// QueryManager.saveCoverageResults(coverageData);
		// QueryManager.saveTestsWithNoCoverage(testsRun);
		// XmlIo.toXML(coverageData, "coverageData-" + saveCount + ".xml");
		saveCount++;
		coverageData = new HashMap<Long, Set<String>>();
	}

	public static void endCoverage() {
		saveAndEmpty();
	}

}
