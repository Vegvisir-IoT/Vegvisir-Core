package com.vegvisir.application;

import com.vegvisir.core.datatype.proto.Block;


/**
 * Ideally, all applications should implement this interface.
 */
public interface VegvisirApplicationDelegator {


    /**
     * Vegvisir will call this function to init and run application.
     * @param instance a underlying Vegvisir instance for application use.
     */
    public void init(VegvisirInstance instance);


    /**
     * An application implemented function. This function will get called whenever a new transaction
     * subscribed by this application arrives.
     * @param tx the new transaction that this application may interest.
     */
    public void applyTransaction(Block.Transaction tx);


    /**
     * Similar to above function except this function passes transactions in a batch for performance
     * optimization.
     * @param txs a list of transactions to be applied.
     */
    public void applyTransactions(Iterable<Block.Transaction> txs);
}
