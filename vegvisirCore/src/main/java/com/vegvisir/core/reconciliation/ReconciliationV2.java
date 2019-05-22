package com.vegvisir.core.reconciliation;

import com.vegvisir.core.blockdag.BlockDAG;
import com.vegvisir.core.blockdag.BlockDAGv2;
import com.vegvisir.core.datatype.proto.Block;
import com.vegvisir.common.datatype.proto.ControlSignal;
import com.vegvisir.core.blockdag.BlockDAG;
import com.vegvisir.network.datatype.proto.Payload;
import com.vegvisir.network.datatype.proto.VegvisirProtocolMessage;

public class ReconciliationV2 extends ReconciliationV1 {

    /**
     * This is a pull based reconciliation algorithm.
     * @param myDAG
     * @param remoteConnectionID
     */
    @Override
    public void exchangeBlocks(BlockDAG myDAG, String remoteConnectionID) {
        /**
         * Send protocol version to the remote side and figure out a version that both can understand.
         * The final version should be the highest one that both can understand.
         */
        sendVersion();

        if (this.runningVersion.compareTo(this.getVersion()) < 0) {
            /*
             * If current version is higher than running version, then we let parent class handle this.
             * This will eventually be handled because all nodes should be able to run version 1.
             */
            super.exchangeBlocks(myDAG, remoteConnectionID);
            return;
        }

        this.dag = myDAG;
        this.remoteId = remoteConnectionID;


        /*
         * Compute frontier set. Now this is a vector clock.
         */
        Block.VectorClock clock = myDAG.computeFrontierSet();

        exchangeVectorClock(clock);

        /* Wait for remote vector clock */
    }


    /**
     *
     * @param clock
     */
    protected void exchangeVectorClock(Block.VectorClock clock) {

        VegvisirProtocolMessage message = VegvisirProtocolMessage.newBuilder()
                .setCmd(ControlSignal.VECTOR_CLOCK)
                .addBlocks(com.isaacsheff.charlotte.proto.Block.newBuilder()
                .setVegvisirBlock(Block.newBuilder().setVectorClock(clock).build()).build())
                .build();
        Payload payload = Payload.newBuilder().setMessage(message).build();
        this.gossipLayer.sendToPeer(this.remoteId, payload);
    }

}
