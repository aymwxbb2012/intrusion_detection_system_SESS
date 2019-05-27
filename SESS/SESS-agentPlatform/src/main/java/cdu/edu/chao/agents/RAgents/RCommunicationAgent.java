package cdu.edu.chao.agents.RAgents;

import cdu.edu.chao.AgentAccount;
import cdu.edu.chao.Contract;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RCommunicationAgent extends Agent {

    private AgentAccount RCommunicationAgentAccount = new AgentAccount();

    @Override
    protected void setup() {
        // TODO Auto-generated method stub
        super.setup();
        RCommunicationAgentAccount.setAgentName("RCommunicationAgent");
        RCommunicationAgentAccount.generatePublicAndPrivateKey();
        Contract.addAgentPublicKey(RCommunicationAgentAccount.getAgentName(),RCommunicationAgentAccount.getAgentPublicKey());
        addBehaviour(new CyclicBehaviour(){
            @Override
            public void action() {
                ACLMessage msg=receive();
                if(msg!=null) {
                    String receivedCm = msg.getUserDefinedParameter("Communication");
                    if(receivedCm!=null) {

                        if (receivedCm.equals("DetectionReport")) {
                            String receivedInfo = Contract.getInfoFromCm(msg.getContent());
                            File localReportFile= new File("RDetectionReport.json");
                            try {
                                FileUtils.writeStringToFile(localReportFile, "\n"+receivedInfo, StandardCharsets.UTF_8,true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println(receivedInfo);
                            addBehaviour(new OneShotBehaviour() {

                                @Override
                                public void action() {
                                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                                    msg.addUserDefinedParameter("Command", "DrawDetectionChart");
                                    System.out.println("Rcommunication");
                                    //msg.setContent("Send");
                                    msg.addReceiver(new AID("ResponseAgent", AID.ISLOCALNAME));
                                    send(msg);

                                }

                            });

                        }


                        if (receivedCm.equals("TrainingReport")) {
                            String receivedInfo = Contract.getInfoFromCm(msg.getContent());
                            File localReportFile= new File("RTrainingReport.json");
                            try {
                                FileUtils.writeStringToFile(localReportFile, "\n"+receivedInfo, StandardCharsets.UTF_8,true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println(receivedInfo);
                            addBehaviour(new OneShotBehaviour() {

                                @Override
                                public void action() {
                                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                                    msg.addUserDefinedParameter("Command", "DrawTrainingChart");
                                    System.out.println("RcommunicationT");
                                    //msg.setContent("Send");
                                    msg.addReceiver(new AID("ResponseAgent", AID.ISLOCALNAME));
                                    send(msg);

                                }

                            });

                        }

                    }

                }
                else block();
            }

        });


    }
}
