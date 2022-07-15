package io.github.lanicc.zookits;

import io.github.lanicc.zookits.command.CallbackRequest;
import io.github.lanicc.zookits.command.Commands;
import io.github.lanicc.zookits.command.Request;
import io.github.lanicc.zookits.command.Response;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.ratis.client.RaftClient;
import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.netty.NettyFactory;
import org.apache.ratis.netty.client.NettyClientRpc;
import org.apache.ratis.proto.RaftProtos;
import org.apache.ratis.protocol.ClientId;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftClientReply;
import org.apache.ratis.protocol.RaftClientRequest;
import org.apache.ratis.protocol.RaftPeerId;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 2022/7/13.
 *
 * @author lan
 */
public class ZookitsClient extends ZookitsPeer {

    final RaftClient raftClient;

    final ScheduledThreadPoolExecutor executor;

    public ZookitsClient(Config config) {
        super(config);
        this.raftClient = buildClient(config);
        AtomicInteger idx = new AtomicInteger();
        this.executor = new ScheduledThreadPoolExecutor(2, r -> new Thread(r, "WatchRequest-" + idx.getAndIncrement()));
    }

    public <T extends CallbackRequest<T>> void callRequest(T request) {
        schedule(new WatchTask<>(request));
    }

    private void schedule(Runnable r) {
        executor.schedule(r, 1, TimeUnit.SECONDS);
    }

    private class WatchTask<T extends CallbackRequest<T>> implements Runnable {

        private final T req;

        private WatchTask(T req) {
            this.req = req;
        }

        @Override
        public void run() {
            Boolean requested;
            try {
                requested = sendRequest(req);
            } catch (IOException e) {
                logger.error("watch task {} run error", req, e);
                return;
            }
            if (BooleanUtils.isTrue(requested)) {
                req.getCallback().accept(req);
            } else {
                schedule(this);
            }
        }
    }


    public <T> T sendRequest(Request<T> request) throws IOException {
        Message message = Message.valueOf(Commands.toBs(request));
        RaftClientRequest.Type type = Commands.typeOf(request);
        RaftClientReply reply;
        if (type.is(RaftProtos.RaftClientRequestProto.TypeCase.READ)) {
            reply = raftClient.io().sendReadOnly(message);
        } else {
            reply = raftClient.io().send(message);
        }
        Message replyMessage = reply.getMessage();
        Response<T> response = Commands.of(replyMessage.getContent());
        if (response.isError()) {
            throw new RuntimeException(response.getMsg());
        }
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMsg());
        }
        return response.getData();
    }

    private RaftClient buildClient(Config config) {
        int serverId = config.getInt(Const.SERVER_IDX.getLeft(), Const.SERVER_IDX.getRight());
        RaftProperties raftProperties = config.toRaft();
        NettyFactory nettyFactory = new NettyFactory(new Parameters());
        NettyClientRpc nettyClientRpc = nettyFactory.newRaftClientRpc(ClientId.randomId(), raftProperties);

        //RaftGroup.valueOf(RaftGroupId.valueOf(UUID.randomUUID()))
        RaftClient.Builder builder =
                RaftClient.newBuilder()
                        .setProperties(raftProperties)
                        .setRaftGroup(raftGroup)
                        .setLeaderId(RaftPeerId.getRaftPeerId(String.valueOf(serverId)))
                        .setClientRpc(nettyClientRpc);
        return builder.build();
    }

    @Override
    public void close() throws IOException {
        raftClient.close();
    }
}
