package com.vegvisir.core.blockdag;

import com.google.protobuf.ByteString;
import com.google.protobuf.Value;
import com.isaacsheff.charlotte.proto.Reference;
import com.isaacsheff.charlotte.proto.Block;
import com.vegvisir.common.datatype.proto.ControlSignal;
import com.vegvisir.core.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    /**
     * Increment given vector clock by 1 on given crypto id's chain.
     * [Now using map instead of list]
     * Currently this is not efficient because vector clocks are stored in a list.
     * However, since protobuf does not support customized data type as map key, and
     * using strings are not match our other code. Therefore, now this function just
     * does a linear search for vector clock of @my_id.
     * Probably we eventually will go to use string...
     * @param my_id
     * @param clock
     * @return
     */
    public static com.vegvisir.core.datatype.proto.Block.VectorClock
    incrementClock(com.isaacsheff.charlotte.proto.CryptoId my_id,
                   com.vegvisir.core.datatype.proto.Block.VectorClock clock) {
//        List<com.vegvisir.core.datatype.proto.Block.VectorClock.Value> value =
//                com.vegvisir.core.datatype.proto.Block.VectorClock.newBuilder(clock)
//                .getValuesList();
//        List<com.vegvisir.core.datatype.proto.Block.VectorClock.Value> _values = new ArrayList<>();
//        value.forEach( v -> {
//            if (v.getCryptoId().equals(my_id)) {
//               _values.add(com.vegvisir.core.datatype.proto.Block.VectorClock.Value.newBuilder(v)
//                        .setIndex(v.getIndex() + 1).build());
//            } else {
//                _values.add(v);
//            }
//        });
//        com.vegvisir.core.datatype.proto.Block.VectorClock.Builder builder =
//                com.vegvisir.core.datatype.proto.Block.VectorClock.newBuilder(clock);
//        builder.getValuesList().clear();
//        builder.addAllValues(_values);
        Map<String, com.vegvisir.core.datatype.proto.Block.VectorClock.Value> valueMap = clock.getValuesMap();
        com.vegvisir.core.datatype.proto.Block.VectorClock.Value _value = valueMap.get(cryptoId2Str(my_id));
        _value = com.vegvisir.core.datatype.proto.Block.VectorClock.Value.newBuilder(_value)
                .setIndex(_value.getIndex() + 1)
                .build();
        valueMap.put(cryptoId2Str(my_id),  _value);
        return com.vegvisir.core.datatype.proto.Block.VectorClock.newBuilder(clock)
                .clearValues().putAllValues(valueMap).build();
    }


    /**
     * get a string representation of given crypto id.
     * Currently this function return a UTF8 string from sha3 hash.
     * @param id
     * @return a string format of given id.
     */
    public static String cryptoId2Str(com.isaacsheff.charlotte.proto.CryptoId id) {
        return id.getHash().getSha3().toStringUtf8();
    }

}
