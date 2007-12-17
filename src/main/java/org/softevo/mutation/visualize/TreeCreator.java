package org.softevo.mutation.visualize;

import java.util.Random;

import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tree;

public class TreeCreator {

	static Random random;

	static Tree createTree() {
		Tree tree = new Tree();
		Node root = tree.addRoot();
		Table mNodes = tree.getNodeTable();
		mNodes.addColumn("name", String.class);
	mNodes.addColumn("size", double.class);
		mNodes.addColumn(MutationTreeData.NUMBER_OF_MUTATIONS , int.class);

		mNodes.addColumn(TreeMap.MOUSEOVER_LABEL, String.class);
		random = new Random();
		for (int i = 1; i < 15; i++) {
			Node n = tree.addChild(root);
			String name = "package" + i;
			n.set("name", name);
//			TreeCreator.addChildren(tree, n, random.nextInt(5), random.nextInt(10), name);
			TreeCreator.addChildren(tree, n, 5, 3, name)
			;
		}
		return tree;
	}


	static void addChildren(Tree tree2, Node n, int numberOfChildren,
			int depth, String name) {

		String childName = name + "." + "sub" + depth;
		for (int i = 1; i < numberOfChildren + 1; i++) {
			Node nadd = tree2.addChild(n);
			nadd.set("name", childName);
//			nadd.set("notkilled", random.nextInt(255));
//			nadd.setDouble("size", random.nextInt(4) * 5.00 +1 );
			nadd.setInt(MutationTreeData.NUMBER_OF_MUTATIONS, random.nextInt(100));
			if (depth > 0) {
				addChildren(tree2, nadd,2, depth - 1, childName);
			}
		}

	}

}
