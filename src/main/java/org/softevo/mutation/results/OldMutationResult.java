/**
 *
 */
package org.softevo.mutation.results;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
public class OldMutationResult {

	@Id
	@GeneratedValue
	private Long id;

	@OneToOne(cascade= CascadeType.ALL)
	private SingleTestResult normalResult;

	@OneToOne(cascade= CascadeType.ALL)
	private SingleTestResult mutatedResult;

	@OneToOne(cascade= CascadeType.ALL)
	private Mutation mutation;

	private OldMutationResult() {
	}

	public OldMutationResult(SingleTestResult normal, SingleTestResult mutated,
			Mutation mutation) {
		super();
		this.normalResult = normal;
		this.mutatedResult = mutated;
		this.mutation = mutation;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (normalResult == null || normalResult.getRuns() == 0) {
			sb.append("No mutation applied because line not covered by tests");
			sb.append('\n');
			sb.append(mutation);

		} else {
			sb.append("Results for:").append('\n');
			sb.append(mutation.toString()).append('\n');
			sb.append("Normal:   ").append(normalResult.toString())
					.append('\n');
			sb.append("Mutation: ").append(mutatedResult.toString()).append(
					'\n');
		}
		return sb.toString();
	}

	/**
	 * @return the normal
	 */
	public SingleTestResult getNormal() {
		return normalResult;
	}

	/**
	 * @param normal
	 *            the normal to set
	 */
	public void setNormal(SingleTestResult normal) {
		this.normalResult = normal;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
}