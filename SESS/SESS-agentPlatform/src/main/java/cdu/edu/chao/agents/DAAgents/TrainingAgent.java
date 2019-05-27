package cdu.edu.chao.agents.DAAgents;

import javax.swing.JOptionPane;

import cdu.edu.chao.GsonUtil;
import cdu.edu.chao.domain.DetectionReport;
import cdu.edu.chao.domain.TrainingReport;
import cdu.edu.chao.utils.DNNUtil;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TrainingAgent extends Agent{

	
	@Override
	protected void setup() {

		super.setup();
		System.out.println("Training agent started");
		addBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage msg=receive();
				if(msg!=null) {
						if(msg.getUserDefinedParameter("Command").equals("Training model")){

						final TrainingReport trainingReport = DNNUtil.training("/Test+SubDataSet2.csv",1200);
							String tJson = GsonUtil.getGson().toJson(trainingReport);

							File localReportFile= new File("DATrainingReport.json");
							try {
								FileUtils.writeStringToFile(localReportFile, tJson, StandardCharsets.UTF_8,false);
							} catch (IOException e) {
								e.printStackTrace();
							}
						addBehaviour(new OneShotBehaviour() {

							@Override
							public void action() {
								ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
								String commandStr = "Training Report";
								String recipientAgent = "DACommunicationAgent";
								msg.addUserDefinedParameter("Feedback", commandStr);
								msg.setContent(GsonUtil.getGson().toJson(trainingReport));
								msg.addReceiver(new AID(recipientAgent, AID.ISLOCALNAME));
								send(msg);

							}

						});
					}

					
				}else block();
			}
			
		});

		
	}




}


