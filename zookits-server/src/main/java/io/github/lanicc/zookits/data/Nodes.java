package io.github.lanicc.zookits.data;

import com.alibaba.fastjson.JSON;
import io.github.lanicc.zookits.util.Predicates;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
public class Nodes {

    private final static String PATH_SEP = "/";

    public static Node of(String name, byte[] data) {
        return of(name, data, Attr.EMPTY);
    }

    public static Node of(String name, byte[] data, Attr attr) {
        return new Node(name, data, new ConcurrentSkipListMap<>(), attr, null, false, System.currentTimeMillis());
    }

    public static Node of(String name) {
        return of(name, null);
    }

    public static Node ofRoot() {
        return of("/", null);
    }

    public static List<String> list(Node node, String path) {
        Node n = getNode(node, path, true, false);
        assert n != null;
        return Collections.unmodifiableList(new ArrayList<>(n.getChildren().keySet()));
    }

    public static void create(Node node, String path, byte[] data) {
        create(node, path, data, Attr.EMPTY);
    }

    public static Node create(Node node, String path, byte[] data, Attr attr) {
        Node pNode = getParentNode(node, path, true);
        String nodeName = getNodeName(path);
        return pNode.createChild(nodeName, data, attr);
    }

    public static byte[] getData(Node node, String path) {
        Node n = getNode(node, path);
        assert n != null;
        return n.getData();
    }

    public static boolean delete(Node node, String path) {
        Node pNode = getParentNode(node, path, false);
        return pNode.removeChild(getNodeName(path));
    }

    public static Node getParentNode(Node root, String path, boolean autoCreate) {
        checkPath(path);
        String pPath = path.substring(0, path.lastIndexOf(PATH_SEP));
        return getNode(root, pPath, true, autoCreate);
    }

    public static Node getNode(Node root, String path) {
        checkPath(path);
        return getNode(root, path, true, false);
    }

    public static Node getNode(Node root, String path, boolean includeRoot, boolean autoCreate) {
        if (includeRoot) {
            if (StringUtils.isBlank(path) || StringUtils.equals(path, PATH_SEP)) {
                return root;
            }
        } else {
            checkPath(path);
        }
        String[] paths = StringUtils.split(path, PATH_SEP);

        Node node = root;
        if (autoCreate) {
            for (String p : paths) {
                Node n1 = node.getChildren().computeIfAbsent(p, Nodes::of);
                n1.setParent(node);
                node = n1;
            }
        } else {
            for (String p : paths) {
                node = node.getChildren().get(p);
                if (Objects.isNull(node)) {
                    return null;
                }
            }
        }
        return node;
    }

    public static boolean exists(Node root, String path) {
        return Objects.nonNull(getNode(root, path));
    }

    public static void delete(Node node) {
        if (Objects.nonNull(node)) {
            node.setDeleted(true);
            node.getChildren().values().forEach(Nodes::delete);
        }
    }

    public static Node read(InputStream in) throws IOException {
        return JSON.parseObject(in, Node.class);
    }

    public static void write(Node root, OutputStream out) throws IOException {
        out.write(JSON.toJSONBytes(root));
    }


    public static String getNodeName(String path) {
        checkPath(path);
        return path.substring(path.lastIndexOf(PATH_SEP) + 1);
    }

    private static void checkPath(String path) {
        checkNonBlankPath(path);
        assert !path.endsWith(PATH_SEP);
    }

    private static void checkNonBlankPath(String path) {
        Predicates.notBlank(path);
    }

}
