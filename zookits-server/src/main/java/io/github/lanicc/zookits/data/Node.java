package io.github.lanicc.zookits.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Node implements Serializable {

    private final static String PATH_SEP = "/";

    private String name;

    private byte[] data;

    private ConcurrentSkipListMap<String, Node> children;

    private Attr attr;

    private Node parent;

    private volatile boolean deleted;

    private volatile long lastAccess;

    public boolean removeChild(String name) {
        updateLastAccess();

        Node n = children.remove(name);
        if (Objects.nonNull(n)) {
            Nodes.delete(n);
            return true;
        }
        return false;
    }


    public void clear() {
        updateLastAccess();

        this.children.clear();
    }

    public Node createChild(String cName, byte[] data, Attr attr) {
        updateLastAccess();
        if (this.attr.isSequence()) {
            long idx = 0;
            if (!children.isEmpty()) {
                String key = children.lastKey();
                String idxStr = key.substring(name.length() + 1);
                idx = Long.parseLong(idxStr);
                idx++;
            }
            final NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumIntegerDigits(20);
            nf.setMaximumFractionDigits(0);
            nf.setGroupingUsed(false);
            cName = nf.format(idx);
        }
        Node c = Nodes.of(cName, data, attr);
        c.parent = this;
        children.put(cName, c);
        return c;
    }

    public boolean hasChild() {
        return MapUtils.isNotEmpty(children);
    }

    public void updateLastAccess() {
        lastAccess = System.currentTimeMillis();
    }

    public byte[] getData() {
        updateLastAccess();
        return data;
    }


    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", data=" + Arrays.toString(data) +
                ", children=" + children +
                ", attr=" + attr +
                ", deleted=" + deleted +
                ", lastAccess=" + lastAccess +
                '}';
    }
}
