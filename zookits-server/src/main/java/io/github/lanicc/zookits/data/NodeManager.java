package io.github.lanicc.zookits.data;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
public class NodeManager {

    private final static Logger logger = LoggerFactory.getLogger(NodeManager.class);

    private final static long CONTAINER_NODE_KEEPALIVE_MILLION = 10000;
    private final static long EPHEMERALS_NODE_KEEPALIVE_MILLION = 5000;
    private final ScheduledThreadPoolExecutor executor;

    private final ArrayBlockingQueue<Node> containerNodes;

    private final ArrayBlockingQueue<Node> ephemeralsNodes;

    public NodeManager() {
        AtomicInteger idx = new AtomicInteger();
        executor = new ScheduledThreadPoolExecutor(2, r -> new Thread(r, "NodeWatcher-" + idx.getAndIncrement()));
        containerNodes = new ArrayBlockingQueue<>(1024);
        ephemeralsNodes = new ArrayBlockingQueue<>(1024);
    }

    public void start() {
        executor.scheduleAtFixedRate(() -> {
            try {
                monitor("containerNodeManager", CONTAINER_NODE_KEEPALIVE_MILLION, containerNodes);
            } catch (Exception e) {
                logger.error("containerNodeManager error", e);
            }
        }, 3, 1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(() -> {
            try {
                monitor("ephemeralsNodeManager", EPHEMERALS_NODE_KEEPALIVE_MILLION, ephemeralsNodes);
            } catch (Exception e) {
                logger.error("ephemeralsNodeManager error", e);
            }
        }, 3, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdown();
    }

    public void addWatch(Node node) {
        if (node.getAttr().isContainer()) {
            addContainerWatcher(node);
        }
        if (node.getAttr().isEphemerals()) {
            addEphemeralsWatcher(node);
        }
    }


    private void addContainerWatcher(Node node) {
        containerNodes.add(node);
    }

    private void addEphemeralsWatcher(Node node) {
        ephemeralsNodes.add(node);
    }

    public void monitor(String threadName, long timeout, ArrayBlockingQueue<Node> queue) {
        String originalName = Thread.currentThread().getName();
        Thread.currentThread().setName(threadName);
        try {
            keepaliveMonitor(timeout, queue);
        } finally {
            Thread.currentThread().setName(originalName);
        }
    }

    public void keepaliveMonitor(long timeout, ArrayBlockingQueue<Node> queue) {
        List<Node> nodes = new ArrayList<>();
        while (!queue.isEmpty()) {
            Node n = queue.poll();
            if (Objects.isNull(n)) {
                return;
            }
            if (n.isDeleted()) {
                logger.debug("node: {} has been deleted, remove from monitor queue", n.getName());
                continue;
            }
            long lastAccess = n.getLastAccess();
            if (!n.hasChild() && System.currentTimeMillis() - lastAccess > timeout) {
                logger.debug("node: {}, last access {} timeout exceeds than {}, delete", n.getName(), lastAccess, timeout);
                n.getParent().removeChild(n.getName());
                continue;
            }
            nodes.add(n);
        }
        if (CollectionUtils.isNotEmpty(nodes)) {
            queue.addAll(nodes);
        }
    }


}
