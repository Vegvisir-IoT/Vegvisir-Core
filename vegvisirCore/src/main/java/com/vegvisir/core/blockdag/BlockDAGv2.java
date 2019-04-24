package com.vegvisir.core.blockdag;

import com.isaacsheff.charlotte.proto.Block;

import java.util.HashMap;

public class BlockDAGv2 extends BlockDAG {


    /**
     * A hash map mapping cryptoId -> blockchain.
     */
    HashMap<com.isaacsheff.charlotte.proto.CryptoId, Blockchain> blockchains;

    /**
     * Verify all transactions and signature for the block. If all checks are passed, then append this block to the block dag.
     * TODO: check block transactions
     *
     * @param block
     * @return
     */
    @Override
    public boolean verifyBlock(Block block) {
        return false;
    }
}
