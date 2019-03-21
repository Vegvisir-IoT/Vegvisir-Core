package com.vegvisir.core.reconciliation;

import com.vegvisir.core.blockdag.BlockDAG;
import com.vegvisir.core.reconciliation.exceptions.VegvisirReconciliationException;

/**
 * Reconciliation protocol implementation version 1.
 * In this version, we send all blocks to the remote side and that is.
 */
public class ReconciliationV1 implements ReconciliationProtocol {

    @Override
    public void exchangeBlocks(BlockDAG myDAG, String remoteConnectionID) {

    }
}
