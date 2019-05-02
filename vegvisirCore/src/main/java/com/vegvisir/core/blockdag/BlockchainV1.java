package com.vegvisir.core.blockdag;

import com.isaacsheff.charlotte.proto.Hash;
import com.vegvisir.core.config.Config;
import com.vegvisir.core.datatype.proto.Block;
import com.isaacsheff.charlotte.proto.Reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class BlockchainV1 extends Blockchain {

    /**
     * Constructor for a blockchain.
     * @param dag the DAG owns this blockchain.
     * @param cryptoId the id of the node owning this blockchain.
     */
    public BlockchainV1(BlockDAG dag, com.isaacsheff.charlotte.proto.CryptoId cryptoId) {
        super(dag, cryptoId);
        this._blocks = new ArrayList<>();
    }


    /**
     * Create a new block by given transactions and parent blocks. This is different with appendBlocks in
     * 2 ways. First, this method should only be called by the host device of the chain. Other devices adding
     * blocks to the chain should call appendBlocks instead. Second, this call involves locks to serialize
     * the order of blocks.
     * If all transactions and parents are valid, this new block will be appended to the chain and saved in the
     * global block map.
     *
     * PRECONDITION: All transactions are valid and parents are valid and no redundant dependency.
     *
     * @param transactions
     * @param parents
     * @return a hash of new created block.
     */
    @Override
    public synchronized Reference createBlock(Iterable<Block.Transaction> transactions, Iterable<Reference> parents) {
        Block.UserBlock content = Block.UserBlock.newBuilder().addAllParents(parents)
                .setUserid(_dag.getConfig().getNodeId())
                .setTimestamp(com.vegvisir.common.datatype.proto.Timestamp.newBuilder().setUtcTime(new Date().getTime()).build())
                .addAllTransactions(transactions)
                .build();
        com.isaacsheff.charlotte.proto.Block block = com.isaacsheff.charlotte.proto.Block.newBuilder()
                .setVegvisirBlock(
                        Block.newBuilder().setBlock(content)
                                .setSignature(_dag.getConfig().signProtoObject(content))
                                .build()
                ).build();
        return appendBlock(block);
    }


    /**
     * Append all blocks in @blocks to the current chain. If all blocks are already available in the chain, then
     * return null. Otherwise, return the last hash of the chain.
     *
     * @param blocks
     * @return null if all blocks in @blocks are duplicate. Otherwise, the hash of the last appended block.
     */
    @Override
    public Reference appendBlocks(Iterable<com.isaacsheff.charlotte.proto.Block> blocks) {
        Reference last = null;
        for (com.isaacsheff.charlotte.proto.Block b : blocks) {
            last = appendBlock(b);
        }
        return last;
    }


    /**
     * Append a block to the chain. This function assume the given block is valid.
     * @param block a valid block to be appended.
     * @return the hash of the given block.
     */
    @Override
    public Reference appendBlock(com.isaacsheff.charlotte.proto.Block block) {
        Reference ref = _dag.putBlock(block);
        if (ref != null)
            _blocks.add(ref);
        return ref;
    }
}
