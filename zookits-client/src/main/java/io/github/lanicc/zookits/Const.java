package io.github.lanicc.zookits;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;

/**
 * Created on 2022/7/13.
 *
 * @author lan
 */
class Const {

    public final static Pair<String, Integer> SERVER_IDX = new ImmutablePair<>("raft.server.this.idx", 0);
    public final static Pair<String, String> SERVER_ADDRESSES = new ImmutablePair<>("raft.server.address.list", "127.0.0.1:8091");

    public final static String SERVER_ADDRESSES_SEP = ",";

    public final static Pair<String, String> STORAGE_PATH = new ImmutablePair<>("raft.server.root.storage.path", "");

    public final static Pair<String, ByteString> RAFT_GROUP = new ImmutablePair<>("raft.server.group-id", ByteString.copyFromUtf8("0000000000000001"));
}
