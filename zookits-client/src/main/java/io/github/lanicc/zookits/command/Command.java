package io.github.lanicc.zookits.command;

import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;

import java.io.Serializable;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
@Read
public abstract class Command implements Serializable {


    public ByteString toBs() {
        return Commands.toBs(this);
    }

}
