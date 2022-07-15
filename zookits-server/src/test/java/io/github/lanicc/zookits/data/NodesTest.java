package io.github.lanicc.zookits.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
class NodesTest {

    @Test
    void of() {
        Node node = Nodes.of("/");
        assertNotNull(node);
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        Node a = Nodes.of("a", data);
        assertEquals("a", a.getName());
        assertEquals(data, a.getData());
    }

    @Test
    void ofRoot() {
        Node root = Nodes.ofRoot();
        assertNotNull(root);
        assertEquals("/", root.getName());
        assertNull(root.getData());
    }

    @Test
    void list() {
        Node root = Nodes.ofRoot();
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        Nodes.create(root, "/a/b", data);
        Nodes.create(root, "/b", data);
        Nodes.create(root, "/c", data);

        List<String> list = Nodes.list(root, "");
        assertNotNull(list);
        assertEquals(3, list.size());
    }

    @Test
    void create() {
        Node root = Nodes.ofRoot();
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        Nodes.create(root, "/a/b", data);
        assertNull(Nodes.getData(root, "/a"));
        assertNotNull(Nodes.getData(root, "/a/b"));


        Node p = Nodes.create(root, "/seq", null, new Attr(true, false, false));
        assertTrue(p.getAttr().isSequence());
        Node n1 = Nodes.create(root, "/seq/a", null, Attr.EMPTY);
        Node n2 = Nodes.create(root, "/seq/b", null, Attr.EMPTY);

    }

    @Test
    void getData() {
        Node root = Nodes.ofRoot();
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        Nodes.create(root, "/a/b", data);
        assertNull(Nodes.getData(root, "/a"));
        assertNotNull(Nodes.getData(root, "/a/b"));
        assertThrows(AssertionError.class, () -> Nodes.getData(root, "/c"));
    }

    @Test
    void delete() {
        Node root = Nodes.ofRoot();
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        Nodes.create(root, "/a/b", data);
        assertNotNull(Nodes.getData(root, "/a/b"));
        assertTrue(Nodes.delete(root, "/a/b"));
        assertThrows(AssertionError.class, () -> Nodes.getData(root, "/a/b"));
        assertFalse(Nodes.delete(root, "/a/b"));
    }

    @Test
    void getParentNode() {
        Node root = Nodes.ofRoot();
        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        Nodes.create(root, "/a/b", data);

        assertNotNull(Nodes.getData(root, "/a/b"));

        Node parentNode = Nodes.getParentNode(root, "/a/b", false);
        assertNotNull(parentNode);

        assertEquals("a",parentNode.getName());

        Node p = Nodes.getParentNode(root, "/a", false);
        assertNotNull(p);

        assertEquals(p, root);
    }

    @Test
    void getNode() {
    }

    @Test
    void getNodeName() {
        assertEquals("b", Nodes.getNodeName("/a/b"));
    }

    @Test
    void serial() {
        Node root = Nodes.ofRoot();
        Nodes.create(root, "/a/b", null);
        String s = JSON.toJSONString(root, SerializerFeature.PrettyFormat);
        Node node = JSON.parseObject(s, Node.class);
    }
}
