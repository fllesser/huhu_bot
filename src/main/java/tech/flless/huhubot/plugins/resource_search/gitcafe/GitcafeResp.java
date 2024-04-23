/**
  * Copyright 2023 json.cn 
  */
package tech.flless.huhubot.plugins.resource_search.gitcafe;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Auto-generated: 2023-07-17 12:6:58
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Getter
@Setter
@ToString
public class GitcafeResp {

    private boolean success;
    private List<Data> data;
    private String error;

}