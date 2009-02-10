package de.unisb.cs.st.javalanche.mutation.results;

import java.util.Collection;
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

	public InvariantAddResult(Map<String, Collection<Integer>> violationsPerTest) {
		Set<Entry<String, Collection<Integer>>> entrySet = violationsPerTest
				.entrySet();
		for (Entry<String, Collection<Integer>> entry : entrySet) {
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
