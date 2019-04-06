package com.vegvisir.core.reconciliation;

import com.vegvisir.core.blockdag.BlockDAG;
import com.vegvisir.core.reconciliation.exceptions.VegvisirReconciliationException;
import com.vegvisir.gossip.Gossip;

/**
 * The interface for reconciliation protocol, we may need to add functions here for newer version.
 */
public abstract class ReconciliationProtocol {


    /* Version of current protocol. Protocol should be backward compatible */
    private Version version;

    /* The version that runs in reconciliation. This is the highest common version between two nodes */
    protected Version runningVersion;

    protected final Object lock = new Object();

    /* remote side id */
    protected String remoteId;

    /* Gossip layer used to gossip messages */
    protected Gossip gossipLayer;

    /* The blockdag for the current node */
    protected BlockDAG dag;

    protected ReconciliationProtocol(int major, int minor, int patch) {
        this.setVersion(major, minor, patch);
    }

    public ReconciliationProtocol setGossipLayer(Gossip gossipLayer) {
        this.gossipLayer = gossipLayer;
        return this;
    }

    /**
     * Exchange blocks between my block and the other side. This is reconciliation protocol verison 1.0 which can only do exchange all blocks. The behavior is that, both nodes will send all block to the other side and end communication.
     * @param myDAG the dag that this nodes own
     * @param remoteConnectionID the connection we are going to use to send blocks.
     * @throws VegvisirReconciliationException
     */
    public abstract void exchangeBlocks(BlockDAG myDAG, String remoteConnectionID) throws VegvisirReconciliationException;


    /**
     * Init version and set version number to the given values.
     * @param major
     * @param minor
     * @param patch
     */
    private void setVersion(int major, int minor, int patch) {
        this.version = new Version(major, minor, patch);
    }

    public Version getVersion() {
        return version;
    }

    /**
     * Class for protocol version
     */
    class Version implements Comparable<Version> {
        int[] version;

        public Version(int major, int minor, int patch) {
            version = new int[3];
            this.version[0] = major;
            this.version[1] = minor;
            this.version[2] = patch;
        }

        public int getMajor() {
            return version[0];
        }

        public int getMinor() {
            return version[1];
        }

        public int getPatch() {
            return version[2];
        }

        public int[] getVersion() {
            return version;
        }

        @Override
        public int compareTo(Version version) {
            for (int i = 0; i < 3; i++) {
                if (this.version[i] > version.getVersion()[i])
                    return 1;
                else if (this.version[i] < version.getVersion()[i])
                    return -1;
            }
            return 0;
        }

        /**
         * Generate a protobuf ProtocolVersion Object for serialization.
         * @return a ProtocolVersion object with this version info.
         */
        public com.vegvisir.common.datatype.proto.ProtocolVersion toProtoVersion() {
            return com.vegvisir.common.datatype.proto.ProtocolVersion.newBuilder()
                    .setMajor(getMajor())
                    .setMinor(getMinor())
                    .setPatch(getPatch())
                    .build();
        }
    }
}
