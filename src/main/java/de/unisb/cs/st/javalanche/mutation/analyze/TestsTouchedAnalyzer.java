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
package de.unisb.cs.st.javalanche.mutation.analyze;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Joiner;

import de.unisb.cs.st.ds.util.io.XmlIo;
import de.unisb.cs.st.javalanche.mutation.analyze.html.HtmlReport;
import de.unisb.cs.st.javalanche.mutation.properties.ConfigurationLocator;
import de.unisb.cs.st.javalanche.mutation.results.Mutation;
import de.unisb.cs.st.javalanche.mutation.results.MutationCoverageFile;
import de.unisb.cs.st.javalanche.mutation.results.MutationTestResult;
import de.unisb.cs.st.javalanche.mutation.results.TestMessage;

/**
 * This analyzer class creates a CSV file (tests_tuoched.csv) of mutation_id,
 * touched_tests. The touched_tests in the CSV file are seperated by spaces. 
 *
 * @author Kevin Jalbert <kevin.j.jalbert@gmail.com>
 */
public class TestsTouchedAnalyzer implements MutationAnalyzer {

    private static final Logger logger = Logger.getLogger(MutationResultAnalyzer.class);

    Joiner spaceJoiner = Joiner.on(' ');

    // List to hold the output of the mutation_id and tests_touched
    private List<String> mutantTestTouched = new ArrayList<String>();

    /**
     * Iterates through all the mutations and adds the set of tests that
     * touched a mutant.
     *
     * @param mutations iterable set of mutations from system under test
     */
    private void collectMutantsTestsTouched(Iterable<Mutation> mutations) {

        for (Mutation mutation : mutations) {

            if (mutation == null) {
                throw new RuntimeException("Null fetched from db");
            }

            MutationTestResult mutationResult = mutation.getMutationResult();

            // Acquire set of tests for this mutant
            Set<String> tests = new HashSet<String>();
            if (mutationResult != null) {
                for (TestMessage testMessage : mutationResult.getAllTestMessages()) {
                    tests.add(testMessage.getTestCaseName());
                }
            }

            // Add mutant_id -> tests_touched pairing
            mutantTestTouched.add(mutation.getId() + "," + spaceJoiner.join(tests));
        }
    }

    /**
     * The analyze method that is called by the analyzeResults task. This method
     * identifies all the tests that a mutant touched and outputs this pairing
     * to a CSV file.
     *
     * @param mutations iterable set of mutations from system under test
     * @param report the HTML report that is being produced (untouched)
     * @return the location that the CSV file was written
     */
    public String analyze(Iterable<Mutation> mutations, HtmlReport report) {

        StringBuilder sb = new StringBuilder();
        sb.append("(mutation_id -> tests_touched) pairing located in tests_touched.csv");

        // Build up output for tests touched
        mutantTestTouched.add("MUTANT_ID,TESTS_TOUCHED");
        collectMutantsTestsTouched(mutations);


        // Write collected data to CSV
        try {
            FileUtils.writeLines(new File(ConfigurationLocator.getJavalancheConfiguration().getOutputDir()
                    + "/tests_touched.csv"),	mutantTestTouched);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }
}
