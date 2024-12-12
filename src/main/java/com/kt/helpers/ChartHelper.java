package com.kt.helpers;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;


// Eksperimentas
public class ChartHelper {

	public static void drawGraph(List<Integer> mValues, List<Long> durations) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < mValues.size(); i++) {
			dataset.addValue(durations.get(i), "Time", mValues.get(i));
		}

		JFreeChart lineChart = ChartFactory.createLineChart(
				"Image Transmission Time",
				"Degree(m)",
				"Time (ms)",
				dataset,
				PlotOrientation.VERTICAL,
				true, true, false);

		CategoryPlot plot = (CategoryPlot) lineChart.getPlot();
		LineAndShapeRenderer renderer = new LineAndShapeRenderer();

		renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setDefaultItemLabelsVisible(true);

		plot.setRenderer(renderer);

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new Dimension(800, 600));

		JFrame frame = new JFrame("Transmission Time Chart");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(chartPanel);
		frame.pack();
		frame.setVisible(true);
	}
}
