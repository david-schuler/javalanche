package org.softevo.mutation.bytecodeMutations.negateJumps.forOwnClass.jumps;

public class Jumps {

	public int method1(int value) {
		if (value > 0) {
			return 1;
		} else {
			return -1;
		}
	}

	public int method2(int value) {
		if (value > 0) {
			return 1;
		} else if (value == 0) {
			return 0;
		} else {
			return -1;
		}
	}

	public int method3(int value) {
		int r = 0;
		for (int i = 0; i < value; i++) {
			r -= 1;
		}
		return r;
	}

	public boolean method4(int a) {
		Integer i1 = new Integer(a);
		Integer i2 = new Integer(12);
		if(a > -1){
			i2 = i1;
		}
		if(i1 == i2){
			return true;
		}
		return false;
	}
}
