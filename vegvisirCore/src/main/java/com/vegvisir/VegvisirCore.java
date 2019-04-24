package com.vegvisir;

import com.vegvisir.core.blockdag.BlockDAG;
import com.vegvisir.core.blockdag.BlockDAGv1;
import com.vegvisir.core.reconciliation.ReconciliationProtocol;
import com.vegvisir.core.reconciliation.ReconciliationV1;
import com.vegvisir.core.reconciliation.exceptions.VegvisirReconciliationException;
import com.vegvisir.gossip.*;
import com.vegvisir.gossip.adapter.NetworkAdapter;
import com.isaacsheff.charlotte.proto.Block;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Vegvisir reconciliation protocol version 0.1
 * We will use this class as a start point for vegivisir com.vegvisir.core.blockdag.
 */
public class VegvisirCore implements Runnable {


    /* Gossip layer for this Vegvisir block DAG instance */
    private Gossip gossipLayer;

    /* Block DAG containing real blocks */
    private final BlockDAG dag;

    /* Protocol that this instance will use for reconciliation with peers */
    private Class<? extends ReconciliationProtocol> protocol;

    private ExecutorService service;

    private static final Logger logger = Logger.getLogger(VegvisirCore.class.getName());


    /**
     * Constructor for a Core instance. Core contains a block dag for storing all blocks; a gossip layer to disseminate new blocks.
     * And a protocol blueprint for doing reconciliation.
     * @param adapter an adapter for network layer. This could be an adapter for TCP or Google Nearby.
     * @param protocol a reconciliation protocol class.
     * @param genesisBlock
     */
    public VegvisirCore(NetworkAdapter adapter, Class<? extends ReconciliationProtocol> protocol, Block genesisBlock) {
        gossipLayer = new Gossip(adapter);
        dag = new BlockDAGv1(genesisBlock);
        this.protocol = protocol;
        service = Executors.newCachedThreadPool();
    }

    public VegvisirCore(NetworkAdapter adapter, Class<ReconciliationProtocol> protocol) {
        this(adapter, protocol, null);
    }

    public VegvisirCore(NetworkAdapter adapter) {
        this(adapter, ReconciliationV1.class, null);
    }

    public void updateProtocol(Class<? extends ReconciliationProtocol> newProtocol)
    {
        /* Don't know whether this will cause a issue if a race condition happens */
        this.protocol = newProtocol;
    }

    /**
     * @return the block dag for this Core instance.
     */
    public BlockDAG getDag() {
        return dag;
    }

    @Override
    public void run() {

        /* Main loop for reconciliation */
        while (true) {
            String remoteId = waitingForNewConnection();
            if (remoteId != null) {
                /* A new instance of protocol is created for each new connection.
                 * As a result, reconciliation process is stateless after it finishes.
                 * This can make it easy to update protocol version */
                service.submit(() -> {
                    try {
                        gossipLayer.linkReconciliationInstanceWithConnection(remoteId, Thread.currentThread());
                        protocol.newInstance().setGossipLayer(gossipLayer).exchangeBlocks(dag, remoteId);
                    } catch (VegvisirReconciliationException ex) {
                        logger.info(ex.getLocalizedMessage());
                    } catch (InstantiationException ex) {
                    }
                    catch (IllegalAccessException ex) {
                    }
                });
            }
        }
    }

    private String waitingForNewConnection() {
        return gossipLayer.randomPickAPeer();
    }
}
