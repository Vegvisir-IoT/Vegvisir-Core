package com.vegvisir.core.reconciliation;

import com.vegvisir.common.datatype.proto.ControlSignal;
import com.vegvisir.core.blockdag.BlockDAG;
import com.vegvisir.network.datatype.proto.Payload;
import com.vegvisir.network.datatype.proto.VegvisirProtocolMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Reconciliation protocol implementation version 1.
 * In this version, we send all blocks to the remote side and that is.
 */
public class ReconciliationV1 extends ReconciliationProtocol
{

    private BlockingQueue<String> completionQueue;
    Thread dispatchThread;

    public ReconciliationV1() {
        super(1, 0, 0);
        completionQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void exchangeBlocks(BlockDAG myDAG, String remoteConnectionID)
    {
        this.dag = myDAG;
        this.remoteId = remoteConnectionID;

        dispatchThread = gossipLayer.setHandlerForPeerMessage(remoteId, this::dispatcherHandler);

        sendVersion();

        synchronized (lock) {
            if (this.runningVersion == null) {
                try {
                    /* We wait until received remote version number */
                    lock.wait();
                } catch (InterruptedException ex) {
                    return;
                }
            }
        }
        sendAllBlocks();

        try {
            /* Take for send complete */
            completionQueue.take();
            /* Take for receive complete */
            completionQueue.take();
        } catch (InterruptedException ex) {
            return;
        }

        dispatchThread.interrupt();
        gossipLayer.disconnect(remoteId);
    }

    protected Version checkVersion(Version remoteVersion) {
        if (remoteVersion.compareTo(getVersion()) >= 0) {
            return getVersion();
        } else {
            return remoteVersion;
        }
    }

    protected void sendVersion()
    {
        com.vegvisir.common.datatype.proto.ProtocolVersion version = com.vegvisir.common.datatype.proto.ProtocolVersion.newBuilder()
                .setMajor(getVersion().getMajor())
                .setMinor(getVersion().getMinor())
                .setPatch(getVersion().getPatch())
                .build();
        VegvisirProtocolMessage message = com.vegvisir.network.datatype.proto.VegvisirProtocolMessage.newBuilder()
                .setVersion(version)
                .setCmd(ControlSignal.VERSION)
                .build();
        Payload payload = Payload.newBuilder().setMessage(message).build();
        gossipLayer.sendToPeer(remoteId, payload);
    }

    /**
     * This is the handler function for upcoming payloads from remote sides.
     * @param payload
     */
    protected void dispatcherHandler(Payload payload)
    {
        com.vegvisir.common.datatype.proto.ProtocolVersion remoteV = payload.getMessage().getVersion();
        Version remoteVersion = new Version(remoteV.getMajor(), remoteV.getMinor(), remoteV.getPatch());

        switch (payload.getMessage().getCmd()) {
            case VERSION:
                synchronized (lock) {
                    this.runningVersion = checkVersion(remoteVersion);
                    lock.notifyAll();
                }
                break;
            case ADD_BLOCKS:
                synchronized (lock) {
                    if (this.runningVersion == null) {
                        try {
                            lock.wait();
                        } catch (InterruptedException ex) {

                        }
                    }
                }
                if(remoteVersion.compareTo(this.runningVersion) != 0)
                {
                    /* all operations other than sync version will be run with the same version between two nodes. This is because the first step for running reconciliation is syncing up versions */
                    return;
                }
                handleAddBlocks(payload.getMessage().getBlocksList());
            case UNRECOGNIZED:
        }

    }

    protected void handleAddBlocks(Iterable<com.isaacsheff.charlotte.proto.Block> blocks) {
        dag.addAllBlocks(blocks);
        completionQueue.add("Add");
    }

    /**
     * Send all blocks to remote side.
     */
    protected void sendAllBlocks() {
         VegvisirProtocolMessage message = VegvisirProtocolMessage.newBuilder()
                 .addAllBlocks(this.dag.getAllBlocks())
                 .setCmd(ControlSignal.ADD_BLOCKS)
                 .setVersion(this.runningVersion.toProtoVersion())
                 .build();
        Payload payload = Payload.newBuilder()
                .setMessage(message)
                .build();
        this.gossipLayer.sendToPeer(remoteId, payload);
        completionQueue.add("Send");
    }
}
