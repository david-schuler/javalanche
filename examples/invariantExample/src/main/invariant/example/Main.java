package invariant.example;

public class Main {

	public int method1() {
		int limit = 10;

		for (int i = 0; i < 10; i++) {
			limit++;
		}
		Class1 c1 = new Class1();
		int res = c1.method1(limit);
		res += new Class3().method1(limit);
		return res;
	}

	public static void main(String[] args) {
		new Main().method1();
	}
}
