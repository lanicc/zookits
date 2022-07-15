package io.github.lanicc.zookits.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.ratis.protocol.RaftClientRequest;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
public final class Commands {

    @SuppressWarnings("unchecked")
    public static <T> T of(ByteString byteString) {
        return (T) JSON.parse(byteString.toStringUtf8(), Feature.SupportAutoType);
    }

    public static ByteString toBs(Command command) {
        byte[] bytes = JSON.toJSONBytes(command, SerializerFeature.WriteClassName);
        return ByteString.copyFrom(bytes);
    }

    public static RaftClientRequest.Type typeOf(Command command) {
        if (command.getClass().isAnnotationPresent(Write.class)) {
            return RaftClientRequest.writeRequestType();
        }
        return RaftClientRequest.readRequestType();
    }

    private Commands() {
        throw new UnsupportedOperationException();
    }
}
