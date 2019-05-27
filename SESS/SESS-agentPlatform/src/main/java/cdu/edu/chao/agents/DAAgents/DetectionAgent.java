package cdu.edu.chao.agents.DAAgents;

import javax.swing.JOptionPane;

import cdu.edu.chao.GsonUtil;
import cdu.edu.chao.domain.DetectionReport;
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

public  class DetectionAgent extends Agent {

	
	@Override
	protected void setup() {

		super.setup();
		System.out.println("Detection agent started");
		addBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage msg=receive();
				if(msg!=null) {


					if(msg.getUserDefinedParameter("Command").equals("Detecting attacks")){

						DetectionReport detectionReport = DNNUtil.detection("/Test+SubDataSet2.csv",1200);
						String dJson = GsonUtil.getGson().toJson(detectionReport);

						File localReportFile= new File("DADetectionReport.json");
						try {
							FileUtils.writeStringToFile(localReportFile, dJson, StandardCharsets.UTF_8,false);
						} catch (IOException e) {
							e.printStackTrace();
						}


						addBehaviour(new OneShotBehaviour() {

							@Override
							public void action() {
								ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
								String commandStr = "Detection Report";
								String recipientAgent = "DACommunicationAgent";
								msg.addUserDefinedParameter("Feedback", commandStr);
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
