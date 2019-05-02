package com.vegvisir.core.config;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import com.isaacsheff.charlotte.proto.CryptoId;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.util.logging.Logger;

/**
 * This class will store all information about current node. This includes key pairs, device id.
 * This class also contains helper functions to sign and verify signatures.
 */
public class Config {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * This is the id for current node and this id is different from public key for this node. This is a nick name for current node.
     * QUESTION: we are not sure whether we should decouple user with nodes. Probably, decouple these two
     * is reasonable, because we want to track per user not per devices probably in terms of tracking responsibilities.
     * However, we can actually use transactions to include user data and using node keypair to sign the block!
     */
    private String nodeId;

    /**
     * The pub/private key pair for the host node.
     */
    private KeyPair keyPair;

    /**
     * This is the signature of current node. We use this one to sign data. Note that we will use
     * different signature objects and public keys to verify others' signature.
     */
    private Signature signature;

    /**
     * The crypto id for current node. Now it just contains the public key of current node.
     */
    private com.isaacsheff.charlotte.proto.CryptoId cryptoId;

    /**
     * A class-wise logger, we use this to log exceptions.
     */
    private static final Logger logger = Logger.getLogger(Config.class.getName());


    /**
     * Constructor for the config object.
     * @param userId a nick name of current node.
     * @param keyPair a public/private keypair.
     */
    public Config(String userId, KeyPair keyPair) {
        this.nodeId = userId;
        this.keyPair = keyPair;
        try {
            signature = initSignature();
            signature.initSign(this.keyPair.getPrivate());
            cryptoId = com.isaacsheff.charlotte.proto.CryptoId.newBuilder().setPublicKey(
                    com.isaacsheff.charlotte.proto.PublicKey.newBuilder()
                            .setEllipticCurveP256(
                                    com.isaacsheff.charlotte.proto.PublicKey.EllipticCurveP256.newBuilder()
                                            .setByteString(ByteString.copyFrom(keyPair.getPublic().getEncoded()))
                                            .build()
                            )
                           .build()
            ).build();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }


    /**
     * Initialize a signature with SHA256withECDSA as algorithm.
     * @return a new signature instance.
     */
    protected static Signature initSignature() {
        try {
            return Signature.getInstance("SHA256withECDSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            logger.info("No Algorithm available\n"+e.getLocalizedMessage());
        } catch (NoSuchProviderException e) {
            logger.info("No BC provider available\n"+e.getLocalizedMessage());
        }
        return null;
    }


    /**
     * Sign given data with current keypair.
     * @param data
     * @return
     */
    public byte[] sign(byte[] data) {
        try {
            signature.update(data);
        } catch (SignatureException e) {
            logger.info("add data to signature failed\n"+e.getLocalizedMessage());
        }
        try {
            return signature.sign();
        } catch (SignatureException e) {
            logger.info("sign data failed\n"+e.getLocalizedMessage());
            return null;
        }
    }


    public byte[] sign(MessageLite message) {
        return sign(message.toByteArray());
    }


    /**
     * Sign the given message with current node's private key. This method returns a signature protobuf
     * object.
     * @param message
     * @return a signature protobuf object containing the signature signed by current node of given message.
     */
    public com.isaacsheff.charlotte.proto.Signature signProtoObject(MessageLite message) {
        return com.isaacsheff.charlotte.proto.Signature.newBuilder()
                .setCryptoId(cryptoId)
                .setSha256WithEcdsa(com.isaacsheff.charlotte.proto.Signature.SignatureAlgorithmSHA256WithECDSA.newBuilder()
                        .setByteString(ByteString.copyFrom(sign(message))).build())
                .build();
    }


    /**
     * Verify the given bytes is signed correct.
     * @param signatureBytes
     * @return true if signature match otherwise return false if anything goes wrong.
     */
    public static boolean checkSignature(byte[] signatureBytes, PublicKey publicKey) {
        try {
            Signature sig = initSignature();
            sig.initVerify(publicKey);
            return sig.verify(signatureBytes);
        } catch (InvalidKeyException e) {
            logger.info("Invalid Public Key\n"+e.getLocalizedMessage());
            return false;
        } catch (SignatureException e) {
            logger.info("Signature verify failed\n"+e.getLocalizedMessage());
            return false;
        }
    }


    /**
     * Calculate a sha3 256 hash of given data. Beside security concern,
     * Charlotte is using sha3 256, so in order to be compatible with charlotte, we use sha3 256 here.
     * @param data the byte arrays for hashing
     * @return a byte array of hash
     */
    public static byte[] sha3(byte[] data) {
        return new SHA3.Digest256().digest(data);
    }


    /**
     * @param message a protobuf object.
     * @return a protobuf Hash object containing SHA3 256 hash bytes corresponding to the given message.
     */
    public static com.isaacsheff.charlotte.proto.Hash sha3(MessageLite message) {
        return com.isaacsheff.charlotte.proto.Hash.newBuilder()
                .setSha3(ByteString.copyFrom(sha3(message.toByteArray())))
                .build();
    }


    /**
     * @return current node's nick name.
     */
    public String getNodeId() {
        return nodeId;
    }


    /**
     * @return current node's public key.
     */
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }


    /**
     * @return a byte array representation of current node's public key.
     */
    public byte[] getPublicKeyBytes() {
        return getPublicKey().getEncoded();
    }


    /**
     * @return a charlotte crypto id object. This ID identify this node.
     */
    public CryptoId getCryptoId() {
        return cryptoId;
    }
}
