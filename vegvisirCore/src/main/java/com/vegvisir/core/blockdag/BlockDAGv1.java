package com.vegvisir.core.blockdag;

import com.isaacsheff.charlotte.proto.Block;
import com.isaacsheff.charlotte.proto.Reference;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BlockDAGv1 extends BlockDAG {


    public BlockDAGv1(Block genesisBlock) {
        super(genesisBlock, null);
    }

    public BlockDAGv1() {
        this(null);
    }

    /**
     * Verify all transactions and signature for the block. If all checks are passed, then append this block to the block dag.
     * TODO: check block transactions
     * @param block
     * @return
     */
    public boolean verifyBlock(Block block) {
        if (block.hasVegvisirBlock()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Get a particular block by its reference.
     * @param ref
     * @return
     */
    public Block getBlock(Reference ref) {
        return blockStorage.get(ref);
    }


    /**
     * Helper method for version 0.1
     * @param blocks a collection of blocks to be append to the block dag.
     */
    public void addAllBlocks(Iterable<Block> blocks) {
        blocks.forEach(b -> {
            addBlock(b);
        });
    }


    /**
     * For version 0.1, we want this method help us to get all blocks.
     * @return all blocks on this node.
     */
    public Collection<Block> getAllBlocks() {
        return blockStorage.values();
    }
}
