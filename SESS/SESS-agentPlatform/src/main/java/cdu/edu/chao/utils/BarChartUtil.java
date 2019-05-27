package cdu.edu.chao.utils;

import javax.swing.*;

import cdu.edu.chao.GsonUtil;
import cdu.edu.chao.domain.DetectionReport;
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


public class BarChartUtil extends JFrame {


    private static final long serialVersionUID = 1L;

    public BarChartUtil(String appTitle, int size) {
        super(appTitle);


        CategoryDataset dataset = createDataset(size);


        JFreeChart chart = ChartFactory.createBarChart3D(
                "IoT Network Intrusion Detection BarChart",
                "Detection Date",
                "Number of data package",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    private CategoryDataset createDataset(int size) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        File dataFile = new File("RDetectionReport.json");
        List<String> list = null;
        try {
            list = FileUtils.readLines(dataFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (size >= list.size()) {
            for (String line : list) {
                DetectionReport detectionReport = GsonUtil.getGson().fromJson(line, DetectionReport.class);

                dataset.addValue(detectionReport.getAttackNumber(), "Anomaly", detectionReport.getDetectionDate());
                dataset.addValue(detectionReport.getTotalDataNumber(), "Total", detectionReport.getDetectionDate());
            }
        } else {
            for (int i = list.size() - 1; i >= list.size() - size; i--) {
                DetectionReport detectionReport = GsonUtil.getGson().fromJson(list.get(i), DetectionReport.class);

                dataset.addValue(detectionReport.getAttackNumber(), "Anomaly", detectionReport.getDetectionDate());
                dataset.addValue(detectionReport.getTotalDataNumber(), "Total", detectionReport.getDetectionDate());
            }
        }


        return dataset;
    }


    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                BarChartUtil example = new BarChartUtil("Detection Analysis", 3);
                example.setSize(800, 400);
                example.setLocationRelativeTo(null);
                example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                example.setVisible(true);
            }
        });
    }

}

