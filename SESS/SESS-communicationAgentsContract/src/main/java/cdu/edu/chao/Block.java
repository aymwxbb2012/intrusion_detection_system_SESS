package cdu.edu.chao;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class Block {


    private String generateTime;

    private byte[] hash;

    private byte[] prevBHash;

    private byte[] merkleRoot;

    private final int MAX_BLOCK_HEIGHT = 3;


    private List<Communication> communications = new ArrayList<Communication>();


    public static byte[] calculateHash(Block block) {
        byte[] raw = BlockChainUtil.appendByteArray(BlockChainUtil.appendByteArray(block.getPrevBHash(), block.getMerkleRoot()), block.getGenerateTime().getBytes());
        byte[] hash = BlockChainUtil.generateSha256(raw);
        return hash;
    }

    public List<byte[]> calculateMerkelRootByLayer(List<byte[]> previousTreeLayer) {

        List<byte[]> currentTreeLayer = new ArrayList<byte[]>();
        for (int i = 1; i < previousTreeLayer.size(); i += 2) {
            if (previousTreeLayer.get(i) == null) {
                currentTreeLayer.add(previousTreeLayer.get(i - 1));
            } else
                currentTreeLayer.add(BlockChainUtil.generateSha256(BlockChainUtil.appendByteArray(previousTreeLayer.get(i - 1), previousTreeLayer.get(i))));
        }
        int count = currentTreeLayer.size();

        if (count > 1) {
            calculateMerkelRootByLayer(currentTreeLayer);
        }
        return currentTreeLayer;

    }

    public byte[] calculateMerkelRoot() {

        int count = communications.size();

        List<byte[]> previousTreeLayer = new ArrayList<byte[]>();
        for (Communication communication : communications) {
            previousTreeLayer.add(communication.getHash());
        }
        List<byte[]> currentTreeLayer = previousTreeLayer;

        if (count > 1) {
            currentTreeLayer = calculateMerkelRootByLayer(currentTreeLayer);
        }
        // if(currentTreeLayer.size()!=1){
        //     throw new BlockChainException();
        // }
        return currentTreeLayer.get(0);

    }


    public boolean addCommunication(Communication communication) {

        if (communication == null) {
            return false;
        }

        if (!(communication.verifySignature())) {

            return false;

        }

        communications.add(communication);

        return true;
    }


    public static boolean isBlockValid(Block newBlock, Block oldBlock) {

        if (!oldBlock.getHash().equals(newBlock.getPrevBHash())) {
            return false;
        }
        if (!calculateHash(newBlock).equals(newBlock.getHash())) {
            return false;
        }
        return true;
    }


    public static Block generateBlock(Block oldBlock, List<Communication> communications) {
        Block newBlock = new Block();
        newBlock.communications = communications;
        newBlock.setGenerateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        newBlock.setPrevBHash(oldBlock.getHash());
        newBlock.setMerkleRoot(newBlock.calculateMerkelRoot());
        newBlock.setHash(newBlock.calculateHash(newBlock));
        return newBlock;
    }


    public String getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(String generateTime) {
        this.generateTime = generateTime;
    }

    public List<Communication> getCommunications() {
        return communications;
    }

    public void setCommunications(List<Communication> communications) {
        this.communications = communications;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getPrevBHash() {
        return prevBHash;
    }

    public void setPrevBHash(byte[] prevBHash) {
        this.prevBHash = prevBHash;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(byte[] merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public int getMAX_BLOCK_HEIGHT() {
        return MAX_BLOCK_HEIGHT;
    }

    @Override
    public String toString() {
        return "Block{" +
                "generateTime='" + generateTime + '\'' +
                ", hash=" + Arrays.toString(hash) +
                ", prevBHash=" + Arrays.toString(prevBHash) +
                ", merkleRoot=" + Arrays.toString(merkleRoot) +
                ", MAX_BLOCK_HEIGHT=" + MAX_BLOCK_HEIGHT +
                '}';
    }
}
