package com.nhn.was.models;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

public class HttpResponse {
    private final Writer writer;

    public HttpResponse(Socket connection) throws IOException{
        this.writer = new OutputStreamWriter(new BufferedOutputStream(connection.getOutputStream()));
    }

    public Writer getWriter(){
        return writer;
    }
}
