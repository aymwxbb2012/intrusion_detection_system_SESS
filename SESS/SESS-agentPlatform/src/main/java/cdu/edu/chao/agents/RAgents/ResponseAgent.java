package cdu.edu.chao.agents.RAgents;


import cdu.edu.chao.GsonUtil;
import cdu.edu.chao.domain.DetectionReport;
import cdu.edu.chao.utils.BarChartUtil;
import cdu.edu.chao.utils.DNNUtil;
import cdu.edu.chao.utils.LineChartUtil;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;


public class ResponseAgent extends Agent {

    @Override
    protected void setup() {
        super.setup();
        System.out.println("Response agent started");
        addBehaviour(new CyclicBehaviour() {

            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {


                    if (msg.getUserDefinedParameter("Command").equals("DrawDetectionChart")) {

                        try {
                            SwingUtilities.invokeAndWait(new Runnable() {
                                @Override
                                public void run() {
                                    BarChartUtil example = new BarChartUtil("Detection Analysis", 5);
                                    example.setSize(800, 400);
                                    example.setLocationRelativeTo(null);
                                    example.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                                    example.setVisible(true);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    if (msg.getUserDefinedParameter("Command").equals("DrawTrainingChart")) {

                        try {
                            SwingUtilities.invokeAndWait(new Runnable() {
                                @Override
                                public void run() {
                                    LineChartUtil example = new LineChartUtil("Bar Chart Window", 5);
                                    example.setSize(800, 400);
                                    example.setLocationRelativeTo(null);
                                    example.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                                    example.setVisible(true);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }


                } else block();
            }

        });

    }
}
