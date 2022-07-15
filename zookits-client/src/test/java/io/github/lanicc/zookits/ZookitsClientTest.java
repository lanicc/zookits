package io.github.lanicc.zookits;

import io.github.lanicc.zookits.command.CreateRequest;
import io.github.lanicc.zookits.command.DeleteRequest;
import io.github.lanicc.zookits.command.WatchRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created on 2022/7/13.
 *
 * @author lan
 */
class ZookitsClientTest {
    static ZookitsClient client;


    @BeforeAll
    static void beforeAll() throws IOException {
        client = new ZookitsClient(Config.ofTest());
    }

    @AfterAll
    static void afterAll() throws IOException {
        client.close();
    }


    @Test
    void watchCreate() throws InterruptedException, IOException {
        String path = "/watch/1";
        WatchRequest watchRequest = new WatchRequest();
        watchRequest.setPath(path);
        watchRequest.setType(WatchRequest.Type.CREATE);
        watchRequest.setCallback(System.out::println);
        client.callRequest(watchRequest);

        TimeUnit.SECONDS.sleep(2);

        Boolean aBoolean =
                client.sendRequest(
                        new CreateRequest()
                                .setPath(path)
                );
        assertTrue(aBoolean);
    }

    @Test
    void watchDelete() throws InterruptedException, IOException {
        String path = "/watch/2";

        Boolean aBoolean =
                client.sendRequest(
                        new CreateRequest()
                                .setPath(path)
                );
        assertTrue(aBoolean);

        WatchRequest watchRequest = new WatchRequest();
        watchRequest.setPath(path);
        watchRequest.setType(WatchRequest.Type.DELETE);
        watchRequest.setCallback(System.out::println);
        client.callRequest(watchRequest);

        TimeUnit.SECONDS.sleep(2);

        Boolean b = client.sendRequest(
                new DeleteRequest()
                        .setPath(path)
        );
        assertTrue(b);

        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    void sendMessage() {
    }


}
