package io.github.lanicc.zookits.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
@Write
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class DeleteRequest extends Request<Boolean> {

    private String path;

}
