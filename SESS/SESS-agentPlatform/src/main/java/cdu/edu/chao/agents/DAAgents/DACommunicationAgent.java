package cdu.edu.chao.agents.DAAgents;

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

public class DACommunicationAgent extends Agent {

	private AgentAccount DACommunicationAgentAccount = new AgentAccount();
	
	@Override
	protected void setup() {

		super.setup();
		DACommunicationAgentAccount.setAgentName("DACommunicationAgent");
		DACommunicationAgentAccount.generatePublicAndPrivateKey();
		Contract.addAgentPublicKey(DACommunicationAgentAccount.getAgentName(),DACommunicationAgentAccount.getAgentPublicKey());

		addBehaviour(new CyclicBehaviour(){
			@Override
			public void action() {
				ACLMessage msg=receive();
				if(msg!=null) {
					String msgContent = msg.getContent();
					if(msgContent!=null) {

						if (msgContent.equals("New Data Package")) {
							//System.out.println(	msg.getUserDefinedParameter("Update"));
							addBehaviour(new OneShotBehaviour() {

								@Override
								public void action() {
									ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
									String commandStr = "Detecting attacks";
									String recipientAgent = "DetectionAgent";
									msg.addUserDefinedParameter("Command", commandStr);
									msg.addReceiver(new AID(recipientAgent, AID.ISLOCALNAME));
									send(msg);

								}

							});
						}

						if (msgContent.equals("New Training Set")) {
							addBehaviour(new OneShotBehaviour() {

								@Override
								public void action() {
									ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
									String commandStr = "Training model";
									String recipientAgent = "TrainingAgent";
									msg.addUserDefinedParameter("Command", commandStr);
									//msg.setContent("Send");
									msg.addReceiver(new AID(recipientAgent, AID.ISLOCALNAME));
									send(msg);

								}

							});

						}
					}
					String msgFeedback = msg.getUserDefinedParameter("Feedback");
					if(msgFeedback!=null){

					if(msg.getUserDefinedParameter("Feedback").equals("Detection Report")){
						addBehaviour(new OneShotBehaviour() {

							@Override
							public void action() {
								ACLMessage newMsg = new ACLMessage(ACLMessage.INFORM);

								File localReportFile = new File("DADetectionReport.json");
								try {
									String information = FileUtils.readLines(localReportFile, StandardCharsets.UTF_8).get(0);

									String recipientAgent = "RCommunicationAgent";
									String communicationStr = Contract.generateCommunication("0", information, DACommunicationAgentAccount, Contract.getPublicKeyByName(recipientAgent));
									newMsg.addUserDefinedParameter("Communication", "DetectionReport");
									newMsg.setContent(communicationStr);
									newMsg.addReceiver(new AID(recipientAgent, AID.ISLOCALNAME));
									send(newMsg);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}

							});


					}

					if(msg.getUserDefinedParameter("Feedback").equals("Training Report")){
						addBehaviour(new OneShotBehaviour() {

							@Override
							public void action() {
								ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
								File localReportFile = new File("DATrainingReport.json");
								try {
									String information = FileUtils.readLines(localReportFile, StandardCharsets.UTF_8).get(0);
								String recipientAgent = "RCommunicationAgent";
								String communicationStr = Contract.generateCommunication("0",information,DACommunicationAgentAccount,Contract.getPublicKeyByName(recipientAgent));
								msg.addUserDefinedParameter("Communication","TrainingReport");
								msg.setContent(communicationStr);
								msg.addReceiver(new AID(recipientAgent,AID.ISLOCALNAME));
								send(msg);
								} catch (IOException e) {
									e.printStackTrace();
								}

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
