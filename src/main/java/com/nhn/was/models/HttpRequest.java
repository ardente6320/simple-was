package com.nhn.was.models;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Map<String,Object> param = null;
    private final Reader reader;
    private String method;
    private String hostName;
    private String version ="";
    private String contentType;

    public HttpRequest(Socket connection) throws IOException {
        reader = new InputStreamReader(new BufferedInputStream(connection.getInputStream()), "UTF-8");
    }

    public void setParam(String param){
        this.param = new HashMap<>();

        String[] args = param.split("&");

        for(String str : args){
            String[] val = str.split("=");
            this.param.put(val[0], val[1]);
        }
    }

    public boolean containsKey(String key) {
        return param.containsKey(key);
    }

    public Map<String,Object> getParam(){
        return param;
    }

    public Object getParameter(String key){
        return param.get(key);
    }

    public Reader getReader() {
        return reader;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
