package com.vegvisir.core.blockdag;

import com.isaacsheff.charlotte.proto.Hash;
import com.isaacsheff.charlotte.proto.Reference;
import com.vegvisir.core.datatype.proto.Block;
import java.util.List;

public abstract class Blockchain {
    /**
     * Store a list of block hashes, the concrete list type depends on implementation
     */
    List<Reference> _blocks;

    /**
     * The block dag owning this chain.
     */
    BlockDAG _dag;


    public Blockchain(BlockDAG dag) {
        _dag = dag;
    }

    /**
     * Create a new block by given transactions and parent blocks. This is different with appendBlocks in
     * 2 ways. First, this method should only be called by the host device of the chain. Other devices adding
     * blocks to the chain should call appendBlocks instead. Second, this call involves locks to serialize
     * the order of blocks.
     * If all transactions and parents are valid, this new block will be appended to the chain and saved in the
     * global block map.
     * @param transactions
     * @param parents
     * @return a hash of new created block.
     */
    public abstract Reference createBlock(Iterable<Block.Transaction> transactions, Iterable<Reference> parents);


    /**
     * Append all blocks in @blocks to the current chain. If all blocks are already available in the chain, then
     * return null. Otherwise, return the last hash of the chain.
     * @param blocks
     * @return null if all blocks in @blocks are duplicate. Otherwise, the hash of the last appended block.
     */
    public abstract Reference appendBlocks(Iterable<com.isaacsheff.charlotte.proto.Block> blocks);
}
