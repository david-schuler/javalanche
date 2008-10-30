package de.unisb.cs.st.javalanche.mutation.results;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class TestName {

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = 1000)
	private String name;

	@ManyToMany
	private List<Invariant> invariants = new ArrayList<Invariant>();

	public TestName() {
	}

	public TestName(String name) {
		super();
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
