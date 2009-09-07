/**
 * 
 */
package de.unisb.cs.st.javalanche.coverage.distance;

class Tuple {
	MethodDescription start;
	MethodDescription end;

	Tuple(MethodDescription m1, MethodDescription m2) {
		super();
		this.start = m1;
		this.end = m2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple other = (Tuple) obj;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		return true;
	}

	public MethodDescription getStart() {
		return start;
	}

	public MethodDescription getEnd() {
		return end;
	}
	
	@Override
	public String toString() {
		return start + " - " + end;
	}
}