package org.softevo.mutation.visualize;

import static org.softevo.mutation.visualize.MutationBugTreeData.BUGS;
import static org.softevo.mutation.visualize.MutationBugTreeData.FRACTION;
import static org.softevo.mutation.visualize.MutationBugTreeData.KILLED;
import static org.softevo.mutation.visualize.MutationBugTreeData.NAME;
import static org.softevo.mutation.visualize.MutationBugTreeData.NUMBER_OF_MUTATIONS;
import static org.softevo.mutation.visualize.MutationBugTreeData.SHORT_NAME;
import static org.softevo.mutation.visualize.MutationBugTreeData.SLOC;
import static org.softevo.mutation.visualize.MutationBugTreeData.SURVIVED;
import static org.softevo.mutation.visualize.MutationBugTreeData.TOTAL;

import java.awt.Color;
import java.awt.Container;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.softevo.mutation.results.Mutation;

import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tree;
import prefuse.util.ui.UILib;

public class BugTreeMap {
	static Map<String, MutationsClassBugData> mutationData;

	private static class BugMutationDataNode {

		String name;

		Map<String, BugMutationDataNode> children = new HashMap<String, BugMutationDataNode>();

		List<Mutation> mutations = new ArrayList<Mutation>();

		private MutationsClassBugData bugData;

		public BugMutationDataNode(String name) {
			this.name = name;
		}

		public BugMutationDataNode appendIfNeeded(String name) {
			BugMutationDataNode returnNode = null;
			if (children.containsKey(name)) {
				returnNode = children.get(name);
			} else {
				returnNode = new BugMutationDataNode(name);
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
			nodes.addColumn(TreeMap.NOT_KILLED, int.class);
			nodes.addColumn(KILLED, int.class);
			nodes.addColumn(SURVIVED, int.class);
			nodes.addColumn(TOTAL, int.class);
			nodes.addColumn(FRACTION, int.class);
			nodes.addColumn(SHORT_NAME, String.class);
			nodes.addColumn(BUGS, int.class);
			nodes.addColumn(SLOC, int.class);
			nodes.addColumn("size", double.class);
			nodes.addColumn(TreeMap.MOUSEOVER_LABEL, String.class);
			Node child = tree.addChild(root);
			this.addTreeData(child, tree);
			return tree;
		}

		private void addTreeData(Node node, Tree tree) {
			node.set(NAME, name);
			String shortname = getShortName();
			node.set(SHORT_NAME, shortname);
			node.set(TreeMap.NOT_KILLED, mutations.size());
			MutationsClassBugData mutationsClassData = mutationData.get(name);
			if (mutationsClassData != null) {
				node.setInt(NUMBER_OF_MUTATIONS, mutationsClassData
						.getMutationsTotal());
				node.setInt(BUGS, mutationsClassData.getNumberOfBugs());
				node.set(KILLED, mutationsClassData.getMutationsKilled());
				node.set(SURVIVED, mutationsClassData.getMutationsSurvived());
				node.set(TOTAL, mutationsClassData.getMutationsTotal());
				node.set(FRACTION, (int) ((1. * mutationsClassData
						.getMutationsSurvived() / mutationsClassData
						.getMutationsTotal()) * 100));
				node.set(TreeMap.NOT_KILLED, mutationsClassData
						.getMutationsSurvived());
				node.set(SLOC, mutationsClassData
						.getSloc());

			}
			node.setDouble("size", 1.);
			for (BugMutationDataNode mnode : children.values()) {
				Node child = tree.addChild(node);
				mnode.addTreeData(child, tree);
			}
		}

		private String getShortName() {
			String shortname = null;
			String prefix = "org.aspectj.";
			if (name.startsWith(prefix)) {
				shortname = name.substring(prefix.length());
			} else {
				shortname = name;
			}
			return shortname;
		}

		public void addData(MutationsClassBugData bugData) {
			this.bugData = bugData;
		}
	}

	public static void main(String[] args) throws URISyntaxException {
		InputStream resource  = BugTreeMap.class.getResourceAsStream("/bugdata_v2.csv");
//		System.out.println(resource);
		mutationData = MutationsClassBugData.fromCsvFile(resource);
		BugTreeMap bugTreeMap = new BugTreeMap();
		for (MutationsClassBugData data : mutationData.values()) {
			bugTreeMap.addNode(data);
		}
		showTreeMap(bugTreeMap);

	}

	private BugMutationDataNode root = new BugMutationDataNode("");

	private void addNode(MutationsClassBugData bugData) {
		String name = bugData.getClassName();
		String[] names = name.split("[.]");
		StringBuilder current = new StringBuilder();
		BugMutationDataNode actualNode = root;
		for (String splitName : names) {
			if (current.length() > 0) {
				current.append("." + splitName);
			} else {
				current.append(splitName);
			}
			actualNode = actualNode.appendIfNeeded(current.toString());
		}
		actualNode.addData(bugData);
	}

	private static void showTreeMap(BugTreeMap mutationTreeData) {
		UILib.setPlatformLookAndFeel();
		final int SPACE = 70;
		String label = SHORT_NAME;
		Tree tree = mutationTreeData.root.getTreeData();
		JComponent treemap = TreeMap.getTreemapComponent(label, tree);
//		JFrame frame = new JFrame("m u t a t i o n |  t r e e m a p");
		JFrame frame = new JFrame("aspectJ bugs | treemap");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cp = frame.getContentPane();
		// BorderLayout layout = new BorderLayout();
		// SpringLayout layout = new SpringLayout();
		JPanel boxPanel  = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel,BoxLayout.PAGE_AXIS));
		boxPanel.add(Box.createVerticalStrut(40));
		BoxLayout layout = new BoxLayout(cp, 0);
		boxPanel.add(treemap);
		frame.setBackground(Color.BLACK);
		// layout.putConstraint(SpringLayout.WEST, treemap, 10,
		// SpringLayout.WEST, cp);
		// layout.putConstraint(SpringLayout.NORTH, treemap, 10,
		// SpringLayout.NORTH, cp);
//		cp.setLayout(layout);
		JPanel outerBoxPanel  = new JPanel();
		outerBoxPanel.setLayout(new BoxLayout(outerBoxPanel,BoxLayout.LINE_AXIS));
		outerBoxPanel.add(Box.createHorizontalStrut(SPACE));
		outerBoxPanel.add(boxPanel);
		cp.add(outerBoxPanel);
		frame.pack();
		frame.setVisible(true);
	}
}
