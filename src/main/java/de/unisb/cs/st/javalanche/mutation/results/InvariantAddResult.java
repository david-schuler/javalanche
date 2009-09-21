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
package de.unisb.cs.st.javalanche.mutation.results;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class InvariantAddResult extends AddResult {

	@OneToMany(cascade = CascadeType.ALL)
	Map<String, InvariantSet> violationsPerTest = new HashMap<String, InvariantSet>();

	public InvariantAddResult() {
	}

	public InvariantAddResult(Map<String, Set<Integer>> violationsPerTest) {
		Set<Entry<String, Set<Integer>>> entrySet = violationsPerTest
				.entrySet();
		for (Entry<String, Set<Integer>> entry : entrySet) {
			InvariantSet is = new InvariantSet();
			is.setInvariants(entry.getValue());
			this.violationsPerTest.put(entry.getKey(), is);
		}
	}

	/**
	 * @return the violationsPerTest
	 */
	public Map<String, InvariantSet> getViolationsPerTest() {
		return violationsPerTest;
	}

	/**
	 * @param violationsPerTest the violationsPerTest to set
	 */
	public void setViolationsPerTest(Map<String, InvariantSet> violationsPerTest) {
		this.violationsPerTest = violationsPerTest;
	}

}
