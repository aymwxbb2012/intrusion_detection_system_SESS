package cdu.edu.chao;

import cdu.edu.chao.Block;
import cdu.edu.chao.GsonUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BlockUpdateUtil {


    private static String getLocalContext(){
        File directory = new File("");

        try {
            String localBlockChainFileDir = directory.getCanonicalPath();
            return localBlockChainFileDir;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void initialiseLocalBlockchainFile(File blockChainFile, String agentName){

        try {
            String localBlockChainFileDir = getLocalContext() + "/Ledger/";

            File file = new File(localBlockChainFileDir);

            if (!file.exists()) {

                file.mkdir();
            }
            String localAgentBlockChainDir = localBlockChainFileDir + "/"+agentName+"/";

            file = new File(localAgentBlockChainDir);

            if (!file.exists()) {

                file.mkdir();
            }
            file = new File(localAgentBlockChainDir+"blockchain.bin");
            if (!file.exists()) {

                file.createNewFile();
            }

            FileUtils.copyFile(blockChainFile,file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateLocalBlockchainFile(String blockJson){
        String localBlockChainFileDir = getLocalContext() + "/DALedger/";
        File localBlockFile= new File(localBlockChainFileDir+"blockchain.bin");
        try {
            FileUtils.writeStringToFile(localBlockFile, blockJson, StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
