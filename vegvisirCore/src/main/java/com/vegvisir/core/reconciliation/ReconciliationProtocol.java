package com.vegvisir.core.reconciliation;

import com.vegvisir.core.blockdag.BlockDAG;
import com.vegvisir.core.reconciliation.exceptions.VegvisirReconciliationException;

/**
 * The interface for reconciliation protocol, we may need to add functions here for newer version.
 */
public interface ReconciliationProtocol {

    /**
     * Exchange blocks between my block and the other side. This is reconciliation protocol verison 1.0 which can only do exchange all blocks. The behavior is that, both nodes will send all block to the other side and end communication.
     * @param myDAG the dag that this nodes own
     * @param remoteConnectionID the connection we are going to use to send blocks.
     * @throws VegvisirReconciliationException
     */
    public void exchangeBlocks(BlockDAG myDAG, String remoteConnectionID) throws VegvisirReconciliationException;

}
