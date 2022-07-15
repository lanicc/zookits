package io.github.lanicc.zookits.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created on 2022/7/14.
 *
 * @author lan
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class LsRequest extends Request<List<String>> {

    private String path;

}
