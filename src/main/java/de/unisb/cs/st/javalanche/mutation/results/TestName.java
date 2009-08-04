package de.unisb.cs.st.javalanche.mutation.results;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import de.unisb.cs.st.javalanche.mutation.properties.MutationProperties;

@Entity
public class TestName {

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = 1000)
	private String name;

	// @ManyToMany
	// private List<Invariant> invariants = new ArrayList<Invariant>();

	private String project;

	private long duration;

	public TestName() {
	}

	public TestName(String name) {
		this(name, MutationProperties.PROJECT_PREFIX, 0);
	}

	public  TestName(String name, String project, long duration) {
		super();
		this.name = name;
		this.project = project;
		this.duration = duration;
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

	/**
	 * @return the project
	 */
	public String getProject() {
		return project;
	}

	/**
	 * @param project
	 *            the project to set
	 */
	public void setProject(String project) {
		this.project = project;
	}

	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

}
