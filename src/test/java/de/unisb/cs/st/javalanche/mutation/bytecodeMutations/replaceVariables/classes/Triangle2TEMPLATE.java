package de.unisb.cs.st.javalanche.mutation.bytecodeMutations.replaceVariables.classes;

public class Triangle2TEMPLATE {

	public int exe(int a, int b, int c) {
		if (a > b) { // 4
			int tmp = a;// 2
			a = b;// 3
			b = tmp;// 3
		}

		if (a > c) {// 4
			int tmp = a;// 2
			a = c;// 3
			c = tmp;// 3
		}

		if (b > c) {// 4
			int tmp = b;// 2
			b = c;// 3
			c = tmp;// 3
		}

		if (c >= a + b)// 6
			return 1;
		else {
			if (a == b && b == c)// 8
				return 4;
			else if (a == b || b == c)// 8
				return 3;
			else
				return 2;
		}
	}
}