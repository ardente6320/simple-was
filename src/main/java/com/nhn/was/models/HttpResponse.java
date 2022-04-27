package com.nhn.was.models;

import java.io.OutputStreamWriter;
import java.io.Writer;

public class HttpResponse {
    private final Writer writer;

    public HttpResponse(OutputStreamWriter writer){
        this.writer = writer;
    }

    public Writer getWriter(){
        return writer;
    }
}
