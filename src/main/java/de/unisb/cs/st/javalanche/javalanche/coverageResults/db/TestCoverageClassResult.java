package de.unisb.cs.st.javalanche.mutation.coverageResults.db;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
public class TestCoverageClassResult {


	@Id
	@GeneratedValue
	private Long id;


	private String className;

	@OneToMany(cascade = CascadeType.ALL)
	private List<TestCoverageLineResult> lineResults;


	public TestCoverageClassResult() {
		super();
	}

	public TestCoverageClassResult(String className, List<TestCoverageLineResult> lineResults) {
		super();
		this.className = className;
		this.lineResults = lineResults;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the lineResults
	 */
	public List<TestCoverageLineResult> getLineResults() {
		return lineResults;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @param lineResults the lineResults to set
	 */
	public void setLineResults(List<TestCoverageLineResult> lineResults) {
		this.lineResults = lineResults;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ClassName: "  + className);
		for(TestCoverageLineResult	 tclr : lineResults){
				sb.append(tclr);
				sb.append('\n');
		}
		return sb.toString();

	}

}
