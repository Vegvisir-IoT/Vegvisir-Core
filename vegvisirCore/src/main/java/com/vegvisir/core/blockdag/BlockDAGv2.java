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


    /**
     * Add blocks to the dag. This will delegate to each blockchain to add blocks.
     * @param blocks a set of blocks to be appended.
     */
    public void addBlocks(Iterable<Block> blocks) {
        com.isaacsheff.charlotte.proto.CryptoId blockId;
        for (Block block : blocks) {
            blockId = block.getVegvisirBlock().getBlock().getCryptoID();
            if (blockchains.containsKey(blockId)) {
                blockchains.get(block.getVegvisirBlock().getBlock().getCryptoID()).appendBlock(block);
            } else {
                if (validatePeer(blockId)) {
                    addNewChain(blockId);
                    blockchains.get(block.getVegvisirBlock().getBlock().getCryptoID()).appendBlock(block);
                }
            }
        }
    }


    /**
     * Append blocks to chain with @cryptoId.
     * @param blocks a set of blocks to be appended.
     * @param cryptoId the id of the chain.
     */
    public void addBlocks(Iterable<Block> blocks, com.isaacsheff.charlotte.proto.CryptoId cryptoId) {
        if (!blockchains.containsKey(cryptoId)) {
            if (!validatePeer(cryptoId))
                return;
            addNewChain(cryptoId);
        }
        blockchains.get(cryptoId).appendBlocks(blocks);
    }


    /**
     * Add all bocks in @blocks to the dag. This is a alias of addBlocks in V2.
     * @param blocks a collection of blocks to be append to the block dag.
     */
    @Override
    public void addAllBlocks(Iterable<Block> blocks) {
        addBlocks(blocks);
    }


    /**
     * Put a new chain to the blockchain map.
     * @param id the id of the node. This is also the key to be used.
     */
    protected synchronized void addNewChain(com.isaacsheff.charlotte.proto.CryptoId id) {
        if (!blockchains.containsKey(id))
            blockchains.put(id, new BlockchainV1(this,  id));
    }


    /**
     * Check whether the given id is in the peer set. This call will delegate to a CRDT 2P set to
     * validate the given @id.
     * @param id the crypto id of peer node.
     * @return true if this is a valid peer, i.e. @id in the set of valid peers.
     */
    protected boolean validatePeer(com.isaacsheff.charlotte.proto.CryptoId id) {
        return true;
    }


    /**
     * A frontier set of each chain can be represented by a vector clock. Therefore, we just need
     * to return the vector clock of the last block of the blockchain for this node.
     * @return vector clock represented the frontier blocks of each blockchain.
     */
    @Override
    public com.vegvisir.core.datatype.proto.Block.VectorClock computeFrontierSet() {
        return blockchains.get(this.config.getCryptoId()).getLastVectorClock();
    }
}
