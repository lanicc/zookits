package io.github.lanicc.zookits;

import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.netty.NettyConfigKeys;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.protocol.RaftPeerId;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.RaftServerConfigKeys;
import org.apache.ratis.util.NetUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * Created on 2022/7/13.
 *
 * @author lan
 */
public class ZookitsServer extends ZookitsPeer {


    private final RaftServer server;

    public ZookitsServer(Config conf) throws IOException {
        super(conf);
        int idx = conf.getInt(Const.SERVER_IDX.getLeft(), Const.SERVER_IDX.getRight());
        String storagePath = conf.get(Const.STORAGE_PATH.getLeft(), Const.STORAGE_PATH.getRight());

        File storageDir = new File(storagePath);
        String peerId = String.valueOf(idx);
        logger.info("peer id is {}", peerId);
        RaftPeer peer = raftGroup.getPeer(RaftPeerId.valueOf(peerId));

        //create a property object
        RaftProperties properties = conf.toRaft();

        //set the storage directory (different for each peer) in RaftProperty object
        RaftServerConfigKeys.setStorageDir(properties, Collections.singletonList(storageDir));

        //set the port which server listen to in RaftProperty object
        final int port = NetUtils.createSocketAddr(peer.getAddress()).getPort();
        NettyConfigKeys.Server.setPort(properties, port);

        //create the counter state machine which hold the counter value
        ZookitsStateMachine counterStateMachine = new ZookitsStateMachine();

        //create and start the Raft server
        this.server =
                RaftServer.newBuilder()
                        .setGroup(raftGroup)
                        .setProperties(properties)
                        .setServerId(peer.getId())
                        .setStateMachine(counterStateMachine)
                        .build();
    }

    public void start() throws IOException {
        server.start();
    }

    @Override
    public void close() throws IOException {
        server.close();
    }
}
