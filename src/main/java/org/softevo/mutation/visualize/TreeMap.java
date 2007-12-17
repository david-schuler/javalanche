package org.softevo.mutation.visualize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.DataSizeAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.SquarifiedTreeMapLayout;
import prefuse.controls.ControlAdapter;
import prefuse.controls.WheelZoomControl;
import prefuse.data.Schema;
import prefuse.data.Tree;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.query.SearchQueryBinding;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.ColorMap;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTree;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

/**
 * Treemap visualization of mutation test outcomes. Based on the prefuse tree
 * map demo.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author David Schuler
 *
 */
public class TreeMap extends Display {

	private static Logger logger = Logger.getLogger(TreeMap.class);

	static final String MOUSEOVER_LABEL = "arg1";

	public static final String TREE_CHI = "data/chi-ontology.xml.gz";

	public static final String NOT_KILLED = "notkilled";

	// create data description of labels, setting colors, fonts ahead of time
	private static final Schema LABEL_SCHEMA = PrefuseLib.getVisualItemSchema();
	static {
		LABEL_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
		LABEL_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(200));
		LABEL_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", 16));
	}

	private static final Schema SMALL_LABEL_SCHEMA = PrefuseLib
			.getVisualItemSchema();
	static {
		SMALL_LABEL_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
		SMALL_LABEL_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(200));
		SMALL_LABEL_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont(
				"Tahoma", 8));
	}

	private static final String tree = "tree";

	private static final String treeNodes = "tree.nodes";

	private static final String treeEdges = "tree.edges";

	private static final String labels = "labels";

	private static final String labels1 = "labels1";

	private static final String IBUGS_PAINT = "iBugsPaint";

	private SearchQueryBinding searchQ;

	private int[] palette = ColorLib.getInterpolatedPalette(ColorLib.rgb(0, 0,
			0), ColorLib.rgb(255, 0, 0));

	// ColorLib.getHotPalette();
	// new int[] { ColorLib.rgb(0, 0, 0),ColorLib.rgb(127, 0, 0),
	// ColorLib.rgb(255, 0, 0) };

	public TreeMap(Tree t, String label) {
		super(new Visualization());
		// add the tree to the visualization
		VisualTree vt = m_vis.addTree(tree, t);
		m_vis.setVisible(treeEdges, null, false);

		// ensure that only leaf nodes are interactive
		Predicate noLeaf = (Predicate) ExpressionParser.parse("childcount()>0");
		m_vis.setInteractive(treeNodes, noLeaf, false);

		DataColorAction fill = new DataColorAction("tree.nodes",
				MutationTreeData.NUMBER_OF_MUTATIONS, Constants.LINEAR_SCALE,
				VisualItem.FILLCOLOR, palette);
		fill.setFilterPredicate((Predicate) ExpressionParser
				.parse("childcount()=0"));


		DataColorAction bugsFill = new DataColorAction("tree.nodes",
				MutationTreeData.IBUGS_FAILURES, Constants.LINEAR_SCALE,
				VisualItem.FILLCOLOR, palette);
		bugsFill.setFilterPredicate((Predicate) ExpressionParser
				.parse("childcount()=0"));

		DataSizeAction dataSizeAction = new DataSizeAction("tree.nodes",
				MutationTreeData.NUMBER_OF_MUTATIONS);
		dataSizeAction.setMaximumSize(200.0);
		dataSizeAction.setMinimumSize(0.1);
		dataSizeAction.setIs2DArea(true);
		dataSizeAction.setScale(Constants.LINEAR_SCALE);

		ActionList size = new ActionList();
		size.add(dataSizeAction);

		m_vis.putAction("size", size);

		addControlListener(new WheelZoomControl());

		// add labels to the visualization
		// first create a filter to show labels only at top-level nodes

		Predicate labelP = (Predicate) ExpressionParser.parse("treedepth()=1");
		Predicate label_leaf = (Predicate) ExpressionParser
				.parse("childcount()=0");

		// now create the labels as decorators of the nodes
		m_vis.addDecorators(labels, treeNodes, labelP, LABEL_SCHEMA);
		m_vis.addDecorators(labels1, treeNodes, label_leaf, SMALL_LABEL_SCHEMA);

		// set up the renderers - one for nodes and one for labels
		DefaultRendererFactory rf = new DefaultRendererFactory();
		rf.add(new InGroupPredicate(treeNodes), new NodeRenderer());
		rf.add(new InGroupPredicate(labels), new LabelRenderer(label));
		rf.add(new InGroupPredicate(labels1),
				new LabelRenderer(MOUSEOVER_LABEL));
		m_vis.setRendererFactory(rf);

		// border colors
		final ColorAction borderColor = new BorderColorAction(treeNodes);
		final ColorAction fillColor = new FillColorAction(treeNodes);

		// color settings
		ActionList colors = new ActionList();
		colors.add(fill);
		colors.add(borderColor);

		m_vis.putAction("colors", colors);

		// animate paint change
		ActionList animatePaint = new ActionList(400);
		animatePaint.add(new ColorAnimator(treeNodes));
		animatePaint.add(new RepaintAction());
		animatePaint.add(new FillColorAction(treeNodes));
		m_vis.putAction("animatePaint", animatePaint);

		ActionList iBugsPaint = new ActionList(400);
		iBugsPaint.add(new ColorAnimator(treeNodes));
		iBugsPaint.add(new RepaintAction());
//		iBugsPaint.add(new IBugsColorAction(treeNodes));
		iBugsPaint.add(bugsFill);
		m_vis.putAction(IBUGS_PAINT, iBugsPaint);

		// create the single filtering and layout action list
		ActionList layout = new ActionList();

		layout.add(new SquarifiedTreeMapLayout(tree));
		layout.add(new LabelLayout(labels));
		layout.add(new LabelLayout(labels1));
		layout.add(colors);
		layout.add(new RepaintAction());
		m_vis.putAction("layout", layout);

		// initialize our display
		setSize(700, 600);
		setItemSorter(new TreeDepthItemSorter());
		addControlListener(new ControlAdapter() {
			public void itemEntered(VisualItem item, MouseEvent e) {
				item.setStrokeColor(borderColor.getColor(item));
				double realSize = item.getSize();
				double intendedSize = item.getDouble("size");
				// item.set(MOUSEOVER_LABEL, "Actualsize: " + realSize
				// + " Intended Size: " + intendedSize + " Ratio: "
				// + realSize / intendedSize);
				item.set(MOUSEOVER_LABEL, item.get("name"));
				item.getVisualization().repaint();
			}

			public void itemExited(VisualItem item, MouseEvent e) {
				item.setStrokeColor(item.getEndStrokeColor());
				item.set(MOUSEOVER_LABEL, null);
				item.getVisualization().repaint();
			}
		});

		searchQ = new SearchQueryBinding(vt.getNodeTable(), label);
		m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, searchQ.getSearchSet());
		searchQ.getPredicate().addExpressionListener(new UpdateListener() {
			public void update(Object src) {
				m_vis.cancel("animatePaint");
				m_vis.run("colors");
				m_vis.run("animatePaint");
			}
		});

		// perform layout
		m_vis.run("size");
		m_vis.run("layout");
	}

	public SearchQueryBinding getSearchQuery() {
		return searchQ;
	}

	public static void main(String argv[]) {
		UILib.setPlatformLookAndFeel();

		String label = "name";
		Tree myTree = TreeCreator.createTree();

		JComponent treemap = getTreemapComponent(label, myTree);

		JFrame frame = new JFrame("m u t a t i o n |  t r e e m a p");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(treemap);
		frame.pack();
		frame.setVisible(true);
	}

	public static JComponent demo() {
		return getTreemapComponent("name", TreeCreator.createTree());
	}

	public static JComponent getTreemapComponent(final String label,
			final Tree tree) {
		// create a new treemap
		final TreeMap treemap = new TreeMap(tree, label);

		// create a search panel for the tree map
		JSearchPanel search = treemap.getSearchQuery().createSearchPanel();
		search.setShowResultCount(true);
		search.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
		search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));

		final JFastLabel title = new JFastLabel("                 ");
		title.setPreferredSize(new Dimension(350, 20));
		title.setVerticalAlignment(SwingConstants.BOTTOM);
		title.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));

		treemap.addControlListener(new ControlAdapter() {
			public void itemEntered(VisualItem item, MouseEvent e) {
				title.setText(item.get(MutationTreeData.NUMBER_OF_MUTATIONS)
						+ " ");
			}

			public void itemExited(VisualItem item, MouseEvent e) {
				title.setText(null);
			}
		});

		final JFastLabel t2 = new JFastLabel("                 ");
		t2.setPreferredSize(new Dimension(350, 20));
		t2.setVerticalAlignment(SwingConstants.BOTTOM);
		t2.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		t2.setFont(FontLib.getFont("Arial", Font.PLAIN, 10));
		treemap.addControlListener(new ControlAdapter() {
			public void itemEntered(VisualItem item, MouseEvent e) {
				t2.setText(item.getString(label));
			}

			public void itemExited(VisualItem item, MouseEvent e) {
				t2.setText(null);
			}
		});

		Action action = new AbstractAction("Toggle Bug Data") {

			public void actionPerformed(ActionEvent e) {
				treemap.toggleBugData();
			}
		};

		JButton button = new JButton(action);

		Box box = UILib.getBox(new Component[] { title, t2, button }, true, 10,
				3, 0);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(treemap, BorderLayout.CENTER);
		panel.add(box, BorderLayout.SOUTH);
		UILib.setColor(panel, Color.BLACK, Color.GRAY);
		return panel;
	}

	boolean showBugData;

	public void toggleBugData() {
		// m_vis.cancel("animatePaint");
		// m_vis.run("colors");
		// m_vis.run("animatePaint");

		if (showBugData) {
			logger.info("Running bug data paint");
			m_vis.cancel(IBUGS_PAINT);
//			m_vis.run("colors");
			m_vis.run(IBUGS_PAINT);
			showBugData = false;
		} else {
			logger.info("Running color paint"  );
			m_vis.cancel(IBUGS_PAINT);
			m_vis.run("colors");
			m_vis.repaint();
			showBugData = true;
		}

	}

	/**
	 * Set the stroke color for drawing treemap node outlines. A graded
	 * grayscale ramp is used, with higher nodes in the tree drawn in lighter
	 * shades of gray.
	 */
	public static class BorderColorAction extends ColorAction {

		public BorderColorAction(String group) {
			super(group, VisualItem.STROKECOLOR);
		}

		public int getColor(VisualItem item) {
			NodeItem nitem = (NodeItem) item;
			if (nitem.isHover())
				return ColorLib.rgb(99, 130, 191);

			int depth = nitem.getDepth();
			if (depth < 2) {
				return ColorLib.gray(100);
			} else if (depth < 4) {
				return ColorLib.gray(75);
			} else {
				return ColorLib.gray(50);
			}
		}
	}

	/**
	 * Set fill colors for treemap nodes. The nodes are coloured regarding their
	 * escaped mutants while search items are colored in blue.
	 */
	public static class FillColorAction extends ColorAction {

		private ColorMap cmap = new ColorMap(ColorLib.getInterpolatedPalette(
				10, ColorLib.rgb(85, 85, 85), ColorLib.rgb(0, 0, 0)), 0, 9);

		public FillColorAction(String group) {
			super(group, VisualItem.FILLCOLOR);
		}

		public int getColor(VisualItem item) {
			if (item instanceof NodeItem) {
				NodeItem nitem = (NodeItem) item;

				if (nitem.getChildCount() > 0) {
					return 0; // no fill for parent nodes
				} else {
					Integer notKilled = (Integer) nitem
							.get(MutationTreeData.NUMBER_OF_MUTATIONS);

					if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS))
						return ColorLib.rgb(0, 0, 255);
					else {
						double MAX_CHARS = 15;
						int redValue = notKilled == null ? 0 : notKilled;
						redValue = Math.min(255, redValue);
						// (int) (255. * Math.min((double) notKilled
						// .length()
						// / MAX_CHARS, 1.));
						int greenValue = 255 - redValue;
						return ColorLib.rgb(redValue, 0, 0);
						// if(name.toLowerCase().startsWith("d")){
						// return ColorLib.rgb(125,0, 0);
						// }
						// return cmap.getColor(nitem.getDepth());
					}
				}
			} else {
				return cmap.getColor(0);
			}
		}

	} // end of inner class TreeMapColorAction

	/**
	 * Set label positions. Labels are assumed to be DecoratorItem instances,
	 * decorating their respective nodes. The layout simply gets the bounds of
	 * the decorated node and assigns the label coordinates to the center of
	 * those bounds.
	 */
	public static class LabelLayout extends Layout {
		public LabelLayout(String group) {
			super(group);
		}

		public void run(double frac) {
			Iterator iter = m_vis.items(m_group);
			while (iter.hasNext()) {
				DecoratorItem item = (DecoratorItem) iter.next();
				VisualItem node = item.getDecoratedItem();
				Rectangle2D bounds = node.getBounds();
				setX(item, null, bounds.getCenterX());
				setY(item, null, bounds.getCenterY());
			}
		}
	} // end of inner class LabelLayout

	/**
	 * A renderer for treemap nodes. Draws simple rectangles, but defers the
	 * bounds management to the layout.
	 */
	public static class NodeRenderer extends AbstractShapeRenderer {
		private Rectangle2D m_bounds = new Rectangle2D.Double();

		public NodeRenderer() {
			m_manageBounds = false;
		}

		protected Shape getRawShape(VisualItem item) {
			// System.out.println("Visual Item " + item.getSize());
			// System.out.println(item.get("size"));
			// Rectangle2D rect = item.getBounds();
			// item.setBounds(rect.getX(), rect.getY(),20 /*
			// item.getInt("notkilled")*/, rect.getHeight());
			m_bounds.setRect(item.getBounds());
			return m_bounds;
		}
	} // end of inner class NodeRenderer

	public static class IBugsColorAction extends ColorAction {



		public IBugsColorAction(String group) {
			super(group, VisualItem.FILLCOLOR);
		}

		public int getColor(VisualItem item) {
			if (item.getInt(MutationTreeData.IBUGS_FAILURES) > 0) {
				logger.info("iBugs count" + item.getInt(MutationTreeData.IBUGS_FAILURES));
				return ColorLib.rgb(0, 0, 255);
			}
			return item.getFillColor();
		}

	}

} // end of class TreeMap
