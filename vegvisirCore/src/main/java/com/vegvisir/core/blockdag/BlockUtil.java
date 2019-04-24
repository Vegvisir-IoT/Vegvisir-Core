package com.vegvisir.core.blockdag;

import com.google.protobuf.ByteString;
import com.isaacsheff.charlotte.proto.Reference;
import com.isaacsheff.charlotte.proto.Block;
import com.vegvisir.core.config.Config;

public class BlockUtil {

    /**
     * @param block
     * @return a Reference of given block.
     */
    public static Reference byRef(Block block) {
        return Reference.newBuilder().setHash(Config.sha3(block)).build();
    }


    /**
     * @param hash
     * @return a Reference wraps the given hash.
     */
    public static Reference byRef(com.isaacsheff.charlotte.proto.Hash hash) {
        return Reference.newBuilder().setHash(hash).build();
    }

}
