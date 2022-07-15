package io.github.lanicc.zookits;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2022/7/13.
 *
 * @author lan
 */
class ZookitsServerTest {

    @Test
    void start() throws IOException, InterruptedException {
        ZookitsServer server = new ZookitsServer(Config.ofTest());
        server.start();
        TimeUnit.HOURS.sleep(1);
        server.close();
    }
}
