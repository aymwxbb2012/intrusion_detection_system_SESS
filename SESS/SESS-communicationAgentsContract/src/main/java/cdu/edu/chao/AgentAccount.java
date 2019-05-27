package cdu.edu.chao;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class AgentAccount {
    private String agentName;
    private PublicKey agentPublicKey;
    private PrivateKey agentPrivateKey;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) {
        AgentAccount a = new AgentAccount();
        a.generatePublicAndPrivateKey();
        System.out.println(a.getAgentPrivateKey().toString());
        String b = BlockChainUtil.privateKeyToString(a.getAgentPrivateKey());
        System.out.println(BlockChainUtil.loadPrivateKey(b));
    }
    public void generatePublicAndPrivateKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            this.agentPrivateKey = keyPair.getPrivate();
            this.agentPublicKey = keyPair.getPublic();


        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }




    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public PublicKey getAgentPublicKey() {
        return agentPublicKey;
    }

    public void setAgentPublicKey(PublicKey agentPublicKey) {
        this.agentPublicKey = agentPublicKey;
    }

    public PrivateKey getAgentPrivateKey() {
        return agentPrivateKey;
    }

    public void setAgentPrivateKey(PrivateKey agentPrivateKey) {
        this.agentPrivateKey = agentPrivateKey;
    }
}
