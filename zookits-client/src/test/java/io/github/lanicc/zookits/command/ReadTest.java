package io.github.lanicc.zookits.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
class ReadTest {

    @Test
    void oper() {
        assertTrue(C1.class.isAnnotationPresent(Read.class));
        assertTrue(C2.class.isAnnotationPresent(Read.class));
    }

    @Read
    static class C1 extends Command {

    }

    static class C2 extends Command {

    }
}
