package org.softevo.mutation.bytecodeMutations.arithmetic.testclasses;

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
