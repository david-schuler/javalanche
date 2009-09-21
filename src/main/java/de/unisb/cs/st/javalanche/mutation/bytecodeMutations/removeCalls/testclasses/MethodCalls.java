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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeCalls.testclasses;

public class MethodCalls {

	public int supressFail1() {
		int result = 5;
		if (getBoolean()) {
			result = 23;
		}
		return result;
	}

	private boolean getBoolean() {
		return true;
	}

	public int supressFail2() {
		int result = 5;
		if (getObject() != null) {
			result = 23;
		}
		return result;
	}

	private Object getObject() {
		return new Integer(3);
	}

	public int supressFail3() {
		int result = 5;
		if (getInt() != 0) {
			result = 23;
		}
		return result;
	}

	private int getInt() {
		return 55;
	}

	public int ignoreMethodForResult() {
		int result = 23;
		int i = 3;
		if (getBoolean()) {
			i *= 2;
		}
		return result;
	}

}
