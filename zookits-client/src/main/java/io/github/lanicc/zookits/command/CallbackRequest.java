package io.github.lanicc.zookits.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.function.Consumer;

/**
 * Created on 2022/7/15.
 *
 * @author lan
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class CallbackRequest<T extends CallbackRequest<T>> extends Request<Boolean> {

    private Consumer<T> callback;

}
