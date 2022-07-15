package io.github.lanicc.zookits.command;

import io.github.lanicc.zookits.data.Attr;
import io.github.lanicc.zookits.data.Node;
import io.github.lanicc.zookits.data.NodeManager;
import io.github.lanicc.zookits.data.Nodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
public class RequestDispatcher {

    final static Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);

    private final Node root;

    private final NodeManager manager;
    public RequestDispatcher(Node root) {
        this.root = root;
        this.manager = new NodeManager();
        manager.start();
    }


    public Object p(CreateRequest r) {
        Attr attr =
                new Attr(r.isSequence(), r.isEphemerals(), r.isContainer());
        Node node = Nodes.create(root, r.getPath(), r.getData(), attr);
        manager.addWatch(node);
        return true;
    }

    public Object p(LsRequest request) {
        return Nodes.list(root, request.getPath());
    }

    public Object p(GetRequest request) {
        return Nodes.getData(root, request.getPath());
    }

    public Object p(DeleteRequest request) {
        return Nodes.delete(root, request.getPath());
    }

    public boolean p(WatchRequest request) {
        boolean exists = Nodes.exists(root, request.getPath());
        boolean create = Objects.equals(request.getType(), WatchRequest.Type.CREATE);
        return create && exists || !create && !exists;
    }

    public Object p(HelloWorldRequest request) {
        logger.info("{}", request);
        return "welcome";
    }


    public Object p(Request<?> request) {
        try {
            Method p = this.getClass().getDeclaredMethod("p", request.getClass());
            return p.invoke(this, request);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
