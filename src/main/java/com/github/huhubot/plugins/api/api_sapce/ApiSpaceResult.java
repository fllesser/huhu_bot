package com.github.huhubot.plugins.api.api_sapce;

import lombok.Data;

import java.util.Map;

/**
 * @author FLLess7
 * &#064;date  22/1/2024
 */
@Data
public class ApiSpaceResult {

    private String statusCode;

    private String desc;

    private Map<String, String>[] result;
}
