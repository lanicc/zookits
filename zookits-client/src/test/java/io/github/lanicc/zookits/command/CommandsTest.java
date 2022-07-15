package io.github.lanicc.zookits.command;

import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
class CommandsTest {

    @Test
    void of() {
        HelloWorldRequest command = new HelloWorldRequest();
        command.setMessage("hello world");
        ByteString byteString = Commands.toBs(command);
        assertNotNull(byteString);
        Command c = Commands.of(byteString);
        assertNotNull(c);
        assertEquals(command, c);
    }

    @Test
    void toBs() {
    }
}
