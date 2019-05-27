package cdu.edu.chao;


import jade.core.Profile;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.File;


public class SESSApp {
    public static void main(String[] args) {

        Contract.initialiseBlockChain();
        File blockChainFile = Contract.getBlockChainFile();
        BlockUpdateUtil.initialiseLocalBlockchainFile(blockChainFile,"DACommunicationAgent");
        BlockUpdateUtil.initialiseLocalBlockchainFile(blockChainFile,"RCommunicationAgent");
        //DNNUtil.initializeModel(true);
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true");
        ContainerController cc = rt.createMainContainer(p);
        AgentController ac;
        try {
            ac = cc.createNewAgent("DACommunicationAgent", "cdu.edu.chao.agents.DAAgents.DACommunicationAgent", null);
            ac.start();

            ac = cc.createNewAgent("DetectionAgent", "cdu.edu.chao.agents.DAAgents.DetectionAgent", null);
            ac.start();

            ac = cc.createNewAgent("TrainingAgent", "cdu.edu.chao.agents.DAAgents.TrainingAgent", null);
            ac.start();
        } catch (StaleProxyException e) {

            e.printStackTrace();
        }
        String[] arg = {"-container","RCommunicationAgent:cdu.edu.chao.agents.RAgents.RCommunicationAgent;ResponseAgent:cdu.edu.chao.agents.RAgents.ResponseAgent"};


        jade.Boot.main(arg);

    }
}
