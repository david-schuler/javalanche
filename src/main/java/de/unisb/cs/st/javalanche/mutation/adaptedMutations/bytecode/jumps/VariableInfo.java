package de.unisb.cs.st.javalanche.mutation.adaptedMutations.bytecode.jumps;

import org.objectweb.asm.Type;

public class VariableInfo {

	private final int index;
	private final Type type;

	public VariableInfo(int index, Type t) {
		super();
		this.index = index;
		this.type = t;
	}

	public int getIndex() {
		return index;
	}

	public Type getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		VariableInfo other = (VariableInfo) obj;
		if (index != other.index)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
