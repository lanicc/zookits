package io.github.lanicc.zookits.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Attr {

    public static final Attr EMPTY = new Attr(false, false, false);

    private boolean sequence;

    private boolean ephemerals;

    private boolean container;

}
