package io.github.lanicc.zookits;

import io.github.lanicc.zookits.command.Command;
import io.github.lanicc.zookits.command.Commands;
import io.github.lanicc.zookits.command.Request;
import io.github.lanicc.zookits.command.RequestDispatcher;
import io.github.lanicc.zookits.command.Response;
import io.github.lanicc.zookits.data.Node;
import io.github.lanicc.zookits.data.Nodes;
import org.apache.ratis.proto.RaftProtos;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.protocol.TermIndex;
import org.apache.ratis.server.storage.RaftStorage;
import org.apache.ratis.statemachine.TransactionContext;
import org.apache.ratis.statemachine.impl.BaseStateMachine;
import org.apache.ratis.statemachine.impl.SimpleStateMachineStorage;
import org.apache.ratis.statemachine.impl.SingleFileSnapshotInfo;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Created on 2022/7/13.
 *
 * @author lan
 */
public class ZookitsStateMachine extends BaseStateMachine {

    final static Logger logger = LoggerFactory.getLogger(ZookitsStateMachine.class);
    final RequestDispatcher requestDispatcher;

    private Node root;

    final SimpleStateMachineStorage stateMachineStorage;

    public ZookitsStateMachine() {
        root = Nodes.ofRoot();
        requestDispatcher = new RequestDispatcher(root);
        stateMachineStorage = new SimpleStateMachineStorage();
    }

    @Override
    public void initialize(RaftServer raftServer, RaftGroupId raftGroupId, RaftStorage storage) throws IOException {
        stateMachineStorage.init(storage);
        super.initialize(raftServer, raftGroupId, storage);
    }

    @Override
    public void reinitialize() throws IOException {
        root.clear();
        load(stateMachineStorage.getLatestSnapshot());
    }

    @Override
    public CompletableFuture<Message> applyTransaction(TransactionContext trx) {
        RaftProtos.LogEntryProto entry = Objects.requireNonNull(trx.getLogEntry());
        updateLastAppliedTermIndex(entry.getTerm(), entry.getIndex());
        ByteString logData = entry.getStateMachineLogEntry().getLogData();
        Command c = Commands.of(logData);
        return processCommand(c);
    }

    private void load(SingleFileSnapshotInfo snapshot) throws IOException {
        logger.info("load from: {}", snapshot.getFile().getPath());
        try (InputStream in = Files.newInputStream(snapshot.getFile().getPath(), StandardOpenOption.READ)) {
            root = Nodes.read(in);
        }
    }

    @Override
    public long takeSnapshot() throws IOException {
        TermIndex lastIdx = getLastAppliedTermIndex();
        File snapshotFile = stateMachineStorage.getSnapshotFile(lastIdx.getTerm(), lastIdx.getIndex());
        logger.info("take snapshot to file: {}", snapshotFile.getAbsolutePath());
        try (FileOutputStream out = new FileOutputStream(snapshotFile)) {
            Nodes.write(root, out);
        }
        return lastIdx.getIndex();
    }

    @Override
    public CompletableFuture<Message> query(Message msg) {
        Command c = Commands.of(msg.getContent());
        return processCommand(c);
    }

    private CompletableFuture<Message> processCommand(Command command) {
        if (command instanceof Request) {
            return processRequest((Request<?>) command);
        }

        throw new RuntimeException("unknown command: " + command);
    }

    private CompletableFuture<Message> processRequest(Request<?> request) {
        Response<?> response;
        Object resData;
        try {
            resData = requestDispatcher.p(request);
            response = Response.success(resData);
        } catch (Exception e) {
            logger.debug("p request error: {}", request, e);
            response = Response.error(e.getMessage());
        }

        Message reply = Message.valueOf(Commands.toBs(response));
        return CompletableFuture.completedFuture(reply);
    }
}
