package cdu.edu.chao;

import com.google.gson.JsonArray;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

public class Communication {



    public byte[] prevCmHash;

    public byte[] signature;

    private byte[] hash;

    public String  data;

    public String sender;
    public String recipient;


    public Communication(byte[] prevHash, String v, PublicKey sender, PublicKey recipient) {
        if (prevHash == null)
            this.prevCmHash = null;
        else
            this.prevCmHash = Arrays.copyOf(prevHash, prevHash.length);

        this.data = v;
        this.sender = BlockChainUtil.publicKeyToString(sender);
        this.recipient = BlockChainUtil.publicKeyToString(recipient);

    }


    public void generateSignature(PrivateKey privateKey) {
        String rawData = sender + recipient + getData();
        this.signature = BlockChainUtil.applyECDSASig(privateKey,rawData);
    }

    public boolean verifySignature() {
        String rawData = sender + recipient + getData();
        return BlockChainUtil.verifyECDSASig(BlockChainUtil.loadPublicKey(sender), rawData, signature);
    }

    public byte[] getRawCm() {
        ArrayList<Byte> rawCm = new ArrayList<Byte>();


        if (prevCmHash != null)
            for (int i = 0; i < prevCmHash.length; i++)
                rawCm.add(prevCmHash[i]);

        if (signature != null)
            for (int i = 0; i < signature.length; i++)
                rawCm.add(signature[i]);


        byte[] dataBytes = data.getBytes();
        byte[] senderBytes = sender.getBytes();
        byte[] recipientBytes = recipient.getBytes();
        for (int i = 0; i < dataBytes.length; i++) {
            rawCm.add(dataBytes[i]);
        }
        for (int i = 0; i < senderBytes.length; i++) {
            rawCm.add(senderBytes[i]);
        }
        for (int i = 0; i < recipientBytes.length; i++) {
            rawCm.add(recipientBytes[i]);
        }


        byte[] cm = new byte[rawCm.size()];
        int i = 0;
        for (Byte b : rawCm)
            cm[i++] = b;
        return cm;
    }


    public void generateHash() {


        this.hash = BlockChainUtil.generateSha256(getRawCm());

    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getHash() {
        return hash;
    }

    public byte[] getPrevCmHash() {
        return prevCmHash;
    }

    public void setPrevCmHash(byte[] prevCmHash) {
        this.prevCmHash = prevCmHash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
