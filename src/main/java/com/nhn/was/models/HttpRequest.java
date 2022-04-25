package com.nhn.was.models;

import java.util.Map;
import java.util.HashMap;

public class HttpRequest {
    private final Map<String,Object> param = new HashMap<>();

    public HttpRequest(String param){
        String[] args = param.split("&");

        for(String str : args){
            String[] val = str.split("=");
            this.param.put(val[0], val[1]);
        }
    }

    public Object getParameter(String key){
        return param.get(key);
    }
}
