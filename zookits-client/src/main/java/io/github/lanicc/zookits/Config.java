package io.github.lanicc.zookits;

import org.apache.commons.lang3.StringUtils;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

/**
 * Created on 2022/7/13.
 *
 * @author lan
 */
public class Config {

    final static Logger logger = LoggerFactory.getLogger(Config.class);
    public final static String CONFIG_FILE = "zookits.config.file";

    final Properties properties;
    private final static String CLASSPATH = "classpath:";
    private final static String CONFIG_FILE_NAME = "zookits.properties";

    public Config(Properties properties) {
        this.properties = properties;
    }


    public int getInt(String key, int defaultVal) {
        return get(key, Integer::parseInt, defaultVal);
    }

    public ByteString getByteString(String key, ByteString defaultValue) {
        String s = get(key, null);
        if (StringUtils.isBlank(s)) {
            return defaultValue;
        }
        return ByteString.copyFromUtf8(s);
    }
    public String[] spilt(String key, String sep, String defaultValue) {
        String s = get(key, defaultValue);
        return StringUtils.split(s, sep);
    }

    public String get(String key, String defaultValue) {
        String property = properties.getProperty(key);
        if (StringUtils.isBlank(property)) {
            return defaultValue;
        }
        return property;
    }

    public <T> T get(String key, Function<String, T> mapper, T defaultValue) {
        String property = properties.getProperty(key);
        if (StringUtils.isBlank(property)) {
            return defaultValue;
        }
        return mapper.apply(property);
    }


    RaftProperties toRaft() {
        RaftProperties rp = new RaftProperties();
        properties.forEach((k, v) -> rp.set((String) k, (String) v));
        return rp;
    }

    public static Config ofTest() throws IOException {
        return new Config(loadConfig(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath() + "../../../home/" + CONFIG_FILE_NAME));
    }
    public static Config of() throws IOException {
        return new Config(loadConfig());
    }

    public static Config of(String path) throws IOException {
        return new Config(loadConfig(path));
    }

    private static Properties loadConfig(String path) throws IOException {
        logger.debug("load config from path: {}", path);
        if (path.startsWith(CLASSPATH)) {
            try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path.replace(CLASSPATH, ""))) {
                return read(in);
            }
        }
        try (InputStream in = Files.newInputStream(Paths.get(path), StandardOpenOption.READ)) {
            return read(in);
        }
    }

    private static Properties loadConfig() throws IOException {
        return loadConfig(System.getProperty(CONFIG_FILE, CLASSPATH + CONFIG_FILE_NAME));
    }

    private static Properties read(InputStream in) throws IOException {
        Properties properties = new Properties();
        properties.load(in);
        return properties;
    }
}
