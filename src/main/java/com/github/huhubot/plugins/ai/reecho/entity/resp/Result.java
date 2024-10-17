package com.github.huhubot.plugins.ai.reecho.entity.resp;

import lombok.Data;

@Data
public class Result<T> {

    private int status;
    private String message;
    private T data;

}
