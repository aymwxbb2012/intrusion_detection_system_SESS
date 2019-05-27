package cdu.edu.chao;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.util.Arrays;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class BlockChainUtil {

    public static byte[] generateSha256(byte[] input) {
        MessageDigest digest = DigestUtils.getSha256Digest();
        byte[] hash = digest.digest(input);
        return hash;
    }

    public static byte[] appendByteArray(byte[] byte1, byte[] byte2) {
        byte[] newByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, newByte, 0, byte1.length);

        System.arraycopy(byte2, 0, newByte, byte1.length, byte2.length);
        return newByte;
    }



    public static PrivateKey loadPrivateKey(String key64)  {
        byte[] clear = Base64.getDecoder().decode(key64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = null;
        try {
            fact = KeyFactory.getInstance("ECDSA", "BC");
            PrivateKey priv = fact.generatePrivate(keySpec);
            Arrays.fill(clear, (byte) 0);
            return priv;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static PublicKey loadPublicKey(String stored)  {

        try {
            byte[] data = Base64.getDecoder().decode(stored);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
            KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");
            return fact.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String privateKeyToString(PrivateKey priv)  {

        try {
            KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");
            PKCS8EncodedKeySpec spec = null;
            spec = fact.getKeySpec(priv,
                    PKCS8EncodedKeySpec.class);
            byte[] packed = spec.getEncoded();
            String key64 = Base64.getEncoder().encodeToString(packed);

            Arrays.fill(packed, (byte) 0);
            return key64;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String publicKeyToString(PublicKey publ)  {

        try {
            KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");
            X509EncodedKeySpec spec = null;
            spec = fact.getKeySpec(publ,
                    X509EncodedKeySpec.class);
            return Base64.getEncoder().encodeToString(spec.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }


    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
