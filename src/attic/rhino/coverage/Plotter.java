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
//package de.unisb.cs.st.javalanche.rhino.coverage;
//
//import java.awt.BasicStroke;
//import java.awt.Container;
//import java.awt.Graphics2D;
//import java.awt.geom.Rectangle2D;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.imageio.ImageIO;
//import javax.swing.ImageIcon;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.WindowConstants;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.xy.XYItemRenderer;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;
//
//import com.google.common.base.Join;
//
//import de.unisb.cs.st.ds.util.io.Io;
//
//public class Plotter {
//
//	public static void main(String[] args) {
//
//		XYSeries xySeries1 = parseCsvFile("total-coverage.csv");
//		XYSeries xySeries2 = parseCsvFile("additional-coverage.csv");
//		displayChart(new XYSeries[] { xySeries1, xySeries2 });
//	}
//
//	private static XYSeries parseCsvFile(String filename) {
//		XYSeries xySeries = new XYSeries(filename.substring(0, filename
//				.indexOf('.')));
//		File f = new File(filename);
//		List<String> linesFromFile = Io.getLinesFromFile(f);
//		List<Integer> x = new ArrayList<Integer>();
//		List<Integer> y = new ArrayList<Integer>();
//		for (String line : linesFromFile) {
//			String[] split = line.split(",");
//			if (split.length >= 3) {
//				int testPos = Integer.parseInt(split[0]);
//				int failures = Integer.parseInt(split[2]);
//				xySeries.add(testPos, failures);
//				x.add(testPos);
//				y.add(failures);
//			}
//		}
//		String joinX = Join.join(",", x);
//		String joinY = Join.join(",", y);
//		System.out.println("x <- c(" + joinX + " )");
//		System.out.println("y <- c(" + joinY + " )");
//
//		return xySeries;
//	}
//
//	private static void displayChart(XYSeries[] xySeries) {
//		XYSeriesCollection xyDataset = new XYSeriesCollection();
//		for (XYSeries series : xySeries) {
//			xyDataset.addSeries(series);
//		}
//		JFreeChart chart = ChartFactory.createXYLineChart("Coverage", // Title
//				"Tests", // X-Axis label
//				"Bugs detected", // Y-Axis label
//				xyDataset, // Dataset
//				PlotOrientation.VERTICAL, true // Show legend
//				, false, false);
//
//		XYPlot plot = chart.getXYPlot();
//		XYItemRenderer renderer = plot.getRenderer(0);
//		float lineWidth = 0.2f;
//		float dash[] = { 5.0f };
////		float dot[] = { lineWidth };
//		BasicStroke basicStroke = new BasicStroke(lineWidth,
//				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
//		// new BasicStroke(lineWidth,
//		// BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dot, 0.0f);
//		basicStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE,
//				BasicStroke.JOIN_ROUND, 0f, new float[] { 10.0f, 6.0f }, 0.0f);
//		renderer.setSeriesStroke(0, basicStroke);
//		try {
//			saveToFile(chart, "chart.png", 750, 450, 100);
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//		}
//
//		JFrame frame = new JFrame("T E S T");
//		Container contentPane = frame.getContentPane();
//		BufferedImage image = chart.createBufferedImage(500, 300);
//		JLabel lblChart = new JLabel();
//		lblChart.setIcon(new ImageIcon(image));
//		contentPane.add(lblChart);
//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setVisible(true);
//	}
//
//	public static void saveToFile(JFreeChart chart, String outFileName,
//			int width, int height, double quality)
//			throws FileNotFoundException, IOException {
//		BufferedImage img = draw(chart, width, height);
//		ImageIO.write(img, "png", new File(outFileName));
//	}
//
//	protected static BufferedImage draw(JFreeChart chart, int width, int height)
//
//	{
//		BufferedImage img = new BufferedImage(width, height,
//				BufferedImage.TYPE_INT_RGB);
//		Graphics2D g2 = img.createGraphics();
//		chart.draw(g2, new Rectangle2D.Double(0, 0, width, height));
//		g2.dispose();
//		return img;
//	}
//
//}
