package io.github.lanicc.zookits.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
class WriteTest {

    @Test
    void oper() {
        assertTrue(HelloWorldRequest.class.isAnnotationPresent(Write.class));
    }

}
