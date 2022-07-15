package io.github.lanicc.zookits.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created on 2022/7/15.
 *
 * @author lan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WatchRequest extends CallbackRequest<WatchRequest> {

    private String path;

    private Type type;

    public enum Type {

        /**
         * 创建
         */
        CREATE,

        /**
         * 删除
         */
        DELETE;

    }
}
