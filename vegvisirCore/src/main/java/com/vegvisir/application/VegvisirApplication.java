package com.vegvisir.application;

import com.vegvisir.core.datatype.proto.Block;


/**
 * Ideally, all applications should implement this interface.
 */
public interface VegvisirApplication {


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
