package com.vegvisir.core.blockdag;

import com.google.protobuf.ByteString;
import com.isaacsheff.charlotte.proto.Reference;
import com.isaacsheff.charlotte.proto.Block;

public class BlockUtil {

    public static Reference byRef(Block block) {
        return Reference.newBuilder().setHash(com.isaacsheff.charlotte.proto.Hash.newBuilder().setSha3(ByteString.copyFrom(block.toByteArray())).build()).build();
    }

}
