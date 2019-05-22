package com.vegvisir.application;

import com.vegvisir.core.datatype.proto.Block;

public interface VegvisirInstance {


    /**
     * Register a delegator, which will handle new transactions for that application.
     * After the registration, new transactions will be forward to the delegator at most once.
     * If there already is delegator for that application, then this one replaces the old one.
     * However, transactions that already been sent to the old delegator will be processed by the
     * old one.
     * @param context a context object of the application.
     * @param delegator a delegator instance.
     * @return true if the @delegator is successfully registered.
     */
    public boolean registerApplicationDelegator(VegvisirApplicationContext context, VegvisirApplicationDelegator delegator);


    /**
     * Add a new transaction to the DAG. If the transaction is valid, then it will be added to the
     * block, either current one or next one depends on the transaction queue size.
     * @param context  a context object of the application.
     * @param tx a transaction to be added to the DAG.
     * @return true, if the transaction is valid.
     */
    public boolean addTransaction(VegvisirApplicationContext context, Block.Transaction tx);
}
