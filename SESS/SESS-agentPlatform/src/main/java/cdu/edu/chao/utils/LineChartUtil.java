package cdu.edu.chao.utils;

import javax.swing.*;


import cdu.edu.chao.GsonUtil;

import cdu.edu.chao.domain.TrainingReport;
import org.apache.commons.io.FileUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LineChartUtil extends JFrame {

    private static final long serialVersionUID = 1L;

    public LineChartUtil(String appTitle, int size) {
        super(appTitle);


        CategoryDataset dataset = createDataset(size);


        JFreeChart chart = ChartFactory.createLineChart(
                "Intrusion Detection",
                "Detection Date",
                "Accurate Rate",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );


        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    private CategoryDataset createDataset(int size) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        File dataFile = new File("RTrainingReport.json");
        List<String> list = null;
        try {
            list = FileUtils.readLines(dataFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (size >= list.size()) {
            for (String line : list) {
                TrainingReport trainingReport = GsonUtil.getGson().fromJson(line, TrainingReport.class);

                dataset.addValue(trainingReport.getAccurateRate(), "Training Accurate Rate", trainingReport.getTrainingDate());

            }
        } else {
            for (int i = list.size() - size; i < list.size(); i++) {
                TrainingReport trainingReport = GsonUtil.getGson().fromJson(list.get(i), TrainingReport.class);

                dataset.addValue(trainingReport.getAccurateRate(), "Training Accurate Rate", trainingReport.getTrainingDate());

            }
        }


        return dataset;
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                LineChartUtil example = new LineChartUtil("Bar Chart Window", 4);
                example.setSize(800, 400);
                example.setLocationRelativeTo(null);
                example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                example.setVisible(true);
            }
        });
    }


}
