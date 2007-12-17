package org.softevo.mutation.visualize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.softevo.mutation.results.Mutation;
import org.softevo.mutation.results.persistence.HibernateUtil;

import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tree;
import prefuse.util.ui.UILib;

public class MutationTreeData {

	private static Logger logger = Logger.getLogger(MutationTreeData.class);

	private static final String NAME = "name";

	public static final String NUMBER_OF_MUTATIONS = "numberOfMutations";

	private static final String SHORT_NAME = "shortName";

	private static Map<String, Integer> iBugsData = GetIBugsData
			.getBugsForClasses();

	static final String IBUGS_FAILURES = "ibugsFailures";

	private static class MNode {

		String name;

		Map<String, MNode> children = new HashMap<String, MNode>();

		List<Mutation> mutations = new ArrayList<Mutation>();

		public MNode(String name) {
			this.name = name;
		}

		public MNode appendIfNeeded(String name) {
			MNode returnNode = null;
			if (children.containsKey(name)) {
				returnNode = children.get(name);
			} else {
				returnNode = new MNode(name);
				children.put(name, returnNode);
			}
			return returnNode;
		}

		public void addMutation(Mutation mutation) {
			mutations.add(mutation);
		}

		public Tree getTreeData() {
			Tree tree = new Tree();
			Node root = tree.addRoot();
			Table nodes = tree.getNodeTable();
			nodes.addColumn(NAME, String.class);
			nodes.addColumn(NUMBER_OF_MUTATIONS, int.class);
			nodes.addColumn(TreeMap.NOT_KILLED, Integer.class);
			nodes.addColumn(SHORT_NAME, String.class);
			nodes.addColumn(IBUGS_FAILURES, int.class);
			nodes.addColumn("size", double.class);
			nodes.addColumn(TreeMap.MOUSEOVER_LABEL, String.class);
			Node child = tree.addChild(root);
			this.addTreeData(child, tree);
			return tree;
		}

		private void addTreeData(Node node, Tree tree) {
			node.set(NAME, name);
			String shortname = null;
			String prefix = "org.aspectj.";
			if (name.startsWith(prefix)) {
				shortname = name.substring(prefix.length());
			} else {
				shortname = name;
			}
			node.set(TreeMap.NOT_KILLED, mutations.size());

			node.set(SHORT_NAME, shortname);
			// node.setInt(NUMBER_OF_MUTATIONS, (int) Math.min(mutations.size(),
			// 255));
			node.setInt(NUMBER_OF_MUTATIONS, mutations.size());

			node.set(TreeMap.NOT_KILLED, mutations.size());
			node.setDouble("size", 1.);
			if (iBugsData.containsKey(name)) {
				logger.info("Ibugs data found for class" + name);
				node.set(IBUGS_FAILURES, iBugsData.get(name));
			}
			else{
				node.set(IBUGS_FAILURES, 0);
			}
			for (MNode mnode : children.values()) {
				Node child = tree.addChild(node);
				mnode.addTreeData(child, tree);
			}
		}
	}

	MNode root = new MNode("");

	public static void main(String[] args) {
		List<Mutation> mutations = getMutations();
		MutationTreeData mutationTreeData = new MutationTreeData();
		for (Mutation mutation : mutations) {
			mutationTreeData.addNode(mutation);
		}
		showTreeMap(mutationTreeData);
	}

	private static void showTreeMap(MutationTreeData mutationTreeData) {
		UILib.setPlatformLookAndFeel();
		String label = SHORT_NAME;
		Tree tree = mutationTreeData.root.getTreeData();
		printTree(tree);
		JComponent treemap = TreeMap.getTreemapComponent(label, tree);

		JFrame frame = new JFrame("m u t a t i o n |  t r e e m a p");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(treemap);
		frame.pack();
		frame.setVisible(true);
	}

	private static void printTree(Tree tree) {
		Node n = tree.getRoot();
		printNode(n);
	}

	private static void printNode(Node n) {
		Iterator childrenIt = n.children();
		while (childrenIt.hasNext()) {
			Node child = (Node) childrenIt.next();
			printNode(child);
		}
	}

	private void addNode(Mutation mutation) {
		String name = mutation.getClassName();
		String[] names = name.split("[.]");
		StringBuilder current = new StringBuilder();
		MNode actualNode = root;
		for (String splitName : names) {
			if (current.length() > 0) {
				current.append("." + splitName);
			} else {
				current.append(splitName);
			}
			actualNode = actualNode.appendIfNeeded(current.toString());
		}
		actualNode.addMutation(mutation);
	}

	@SuppressWarnings("unchecked")
	private static List<Mutation> getMutations() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("from Mutation");
		// query.setMaxResults(10000);
		List l = query.list();
		List<Mutation> mutations = (List<Mutation>) l;
		tx.commit();
		session.close();
		return mutations;
	}
}
