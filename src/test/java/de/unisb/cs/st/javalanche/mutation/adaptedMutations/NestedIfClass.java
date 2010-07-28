package de.unisb.cs.st.javalanche.mutation.adaptedMutations;

import java.io.File;
import java.io.FilenameFilter;

public class NestedIfClass {

	private static class C1 {

		private int y;

		public void m1(int x) {
			if (x > 1) {
				y = 4;
			}
		}
	}

	private FilenameFilter z;

	public void m1(int x) {
			z = new FilenameFilter() {

				public boolean accept(File dir, String name) {
					if (name != null && name.length() > 10) {
						return true;
					}
					return false;
				}
			};

		z = new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (name != null && name.length() > 10) {
					return true;
				}
				return false;
			}
		};
	}
}
