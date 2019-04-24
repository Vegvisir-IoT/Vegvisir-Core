package com.vegvisir.core.reconciliation;

import com.vegvisir.core.blockdag.BlockDAG;

public class ReconciliationV2 extends ReconciliationV1 {

    @Override
    public void exchangeBlocks(BlockDAG myDAG, String remoteConnectionID) {
        if (this.runningVersion.compareTo(this.getVersion()) < 0) {
            /**
             * If current version is higher than running version, then we let parent class handle this.
             * This will eventually be handled because all nodes should be able to run version 1.
             */
            super.exchangeBlocks(myDAG, remoteConnectionID);
            return;
        }



    }
}
