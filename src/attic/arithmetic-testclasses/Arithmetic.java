/*
* Copyright (C) 2010 Saarland University
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
package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.arithmetic.testclasses;

public class Arithmetic { static{System.out.println("Arithmetic - test class loaded"  );}

	public int method1(int i){
		return i + i;
	}

	public int method2(int i){
		return i - i;
	}

	public int method3(int i){
		i *= -1;
		return i;
	}

	public int method4(int i){
		int m = i / 5;
		return m;
	}

	public boolean method5(int i){
		return i % 10 == 0;
	}

}
