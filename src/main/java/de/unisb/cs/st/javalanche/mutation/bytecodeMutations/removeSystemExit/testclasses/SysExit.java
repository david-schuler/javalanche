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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.removeSystemExit.testclasses;

public class SysExit {

	private int lastFails;

	private int lastErrors;

	public int method1() {
		int i = Integer.parseInt("1");
		System.exit(0);
		return i;
	}
	
	public void method2() {
		int a = 4 + 4;
		if (a != Integer.MAX_VALUE) {
			System.exit(0);
		}
	}

	protected void systemExit(String messages) {
		int num = lastFails; // messages.numMessages(IMessage.FAIL, true);
		if (0 < num) {
			System.exit(-num);
		}
		num = lastErrors; // messages.numMessages(IMessage.ERROR, false);
		if (0 < num) {
			System.exit(num);
		}
		System.exit(0);
	}

}
