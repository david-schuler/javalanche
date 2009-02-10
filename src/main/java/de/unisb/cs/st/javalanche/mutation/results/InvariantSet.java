package de.unisb.cs.st.javalanche.mutation.results;

import java.util.Collection;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.CollectionOfElements;

@Entity
public class InvariantSet {

	@Id
	@GeneratedValue
	private Long id;

	@CollectionOfElements
	private Collection<Integer> invariants;


	public InvariantSet() {
	}

	/**
	 * @return the invariants
	 */
	public Collection<Integer> getInvariants() {
		return invariants;
	}

	/**
	 * @param invariants the invariants to set
	 */
	public void setInvariants(Collection<Integer> invariants) {
		this.invariants = invariants;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
}
