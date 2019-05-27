package cdu.edu.chao;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

public class Contract {

    private static Map<String, AgentAccount> genesisAccounts = new HashMap<String, AgentAccount>();
    private static List<Block> blockChain = new ArrayList<Block>();
    private static Map<String, PublicKey> agentAccountPublicKeys = new HashMap<String, PublicKey>();
    private static File dataFile = new File("blockchain.bin");
    private static Block unpackBlock = new Block();
    private static final Gson gson = GsonUtil.getGson();

    public static void main(String[] args) {
        initialiseBlockChain();
        System.out.println(blockChain.size());
        System.out.println(blockChain.get(blockChain.size()-1));
        System.out.println(blockChain.get(blockChain.size()-1).getHash());


    }

    public static void generateGenesisAccounts() {
        if (genesisAccounts.isEmpty()) {
            AgentAccount GAAgent = new AgentAccount();
            GAAgent.setAgentName("GenesisAgent1");
            GAAgent.generatePublicAndPrivateKey();
            genesisAccounts.put("GenesisAgent1", GAAgent);

            AgentAccount GBAgent = new AgentAccount();
            GBAgent.setAgentName("GenesisAgent2");
            GBAgent.generatePublicAndPrivateKey();
            genesisAccounts.put("GenesisAgent2", GBAgent);

        }


    }

    public static void initialiseBlockChain() {




        generateGenesisAccounts();
        try {
            if (dataFile.length() == 0) {

                Communication genesisCommunication = new Communication("0".getBytes(), "first communication", genesisAccounts.get("GenesisAgent1").getAgentPublicKey(), genesisAccounts.get("GenesisAgent2").getAgentPublicKey());
                genesisCommunication.generateHash();
                genesisCommunication.generateSignature(genesisAccounts.get("GenesisAgent1").getAgentPrivateKey());


                Block genesisBlock = new Block();
                genesisBlock.setPrevBHash("0".getBytes());
                genesisBlock.setGenerateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
                genesisBlock.addCommunication(genesisCommunication);
                genesisBlock.setMerkleRoot(genesisBlock.calculateMerkelRoot());
                genesisBlock.setHash(genesisBlock.calculateHash(genesisBlock));

                blockChain.add(genesisBlock);
                String json = gson.toJson(genesisBlock);
                FileUtils.writeStringToFile(dataFile, json, StandardCharsets.UTF_8, true);

            } else {
                List<String> list = FileUtils.readLines(dataFile, StandardCharsets.UTF_8);
                for (String line : list) {
                    blockChain.add(gson.fromJson(line, Block.class));
                }
            }
            TimeUnit.SECONDS.sleep(2);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addCommunicationInBlock(Communication communication) {
        unpackBlock.addCommunication(communication);
        System.out.println(unpackBlock.getCommunications().size());
        if (unpackBlock.getCommunications().size() == unpackBlock.getMAX_BLOCK_HEIGHT()) {
            try {
                byte[] prevBHash = blockChain.get(blockChain.size()-1).getHash();
                unpackBlock.setPrevBHash(prevBHash);
                unpackBlock.setGenerateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
                unpackBlock.setMerkleRoot(unpackBlock.calculateMerkelRoot());
                unpackBlock.setHash(unpackBlock.calculateHash(unpackBlock));
                blockChain.add(unpackBlock);
                String json = gson.toJson(unpackBlock);
                FileUtils.writeStringToFile(dataFile, "\n"+json, StandardCharsets.UTF_8, true);
                File directory = new File("");
                String localBlockChainFileDir = directory.getCanonicalPath()+"/Ledger/";
                Set<String> agentNames = agentAccountPublicKeys.keySet();
                agentNames.stream().forEach(a->{
                  String BlockChainFileDir = localBlockChainFileDir+a;
                    File localBlockFile= new File(BlockChainFileDir+"/blockchain.bin");
                            try {
                                FileUtils.writeStringToFile(localBlockFile, "\n"+json, StandardCharsets.UTF_8, true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                );

                unpackBlock = new Block();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static PublicKey getPublicKeyByName(String name){
        PublicKey publicKey = agentAccountPublicKeys.get(name);
        return  publicKey;
    }

    public static void addAgentPublicKey(String agentName, PublicKey agentPublicKey){

        agentAccountPublicKeys.put(agentName,agentPublicKey);
    }

    public static File getBlockChainFile() {
        File dataFile = new File("blockchain.bin");
        return dataFile;

    }

    public static String generateCommunication(String prevCmHash, String information, AgentAccount agentAccount, PublicKey recipient) {
        if (prevCmHash == "0") {
            if (agentAccount.getAgentName() == "RCommunicationAgent") {
                return null;
            } else {
                if (agentAccount.getAgentName() == "RCommunicationAgent" && !(recipient.equals(agentAccountPublicKeys.get("RCommunicationAgent")))) {
                    return null;
                }
            }

        }
        Communication communication = new Communication(prevCmHash.getBytes(), information, agentAccount.getAgentPublicKey(), recipient);
        communication.generateHash();
        communication.generateSignature(agentAccount.getAgentPrivateKey());
        addCommunicationInBlock(communication);
        String communicationJson = gson.toJson(communication);

        return communicationJson;

    }

    public static String getInfoFromCm(String communication){
        return gson.fromJson(communication, Communication.class).data;
    }



    }
