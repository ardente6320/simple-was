package com.nhn.was.models;

import java.io.Writer;

public class HttpResponse {
    private final Writer writer;

    public HttpResponse(Writer writer){
        this.writer = writer;
    }
    
    public Writer getWriter(){
        return writer;
    }
}
