package io.github.lanicc.zookits;

import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
public abstract class ZookitsPeer implements Closeable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final Config config;

    protected final RaftGroup raftGroup;

    protected ZookitsPeer(Config conf) {
        this.config = conf;

        String[] addresses = conf.spilt(Const.SERVER_ADDRESSES.getLeft(), Const.SERVER_ADDRESSES_SEP, Const.SERVER_ADDRESSES.getRight());
        RaftPeer[] peers = new RaftPeer[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            peers[i] =
                    RaftPeer.newBuilder()
                            .setAddress(addresses[i])
                            .setId(String.valueOf(i))
                            .build();
        }
        ByteString groupId = conf.getByteString(Const.RAFT_GROUP.getLeft(), Const.RAFT_GROUP.getValue());
        logger.info("group id is {}", groupId.toStringUtf8());
        this.raftGroup = RaftGroup.valueOf(RaftGroupId.valueOf(groupId), peers);
        //this.raftGroup = RaftGroup.valueOf(RaftGroupId.valueOf(UUID.randomUUID()), peers);
    }
}
