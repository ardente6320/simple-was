package com.nhn.was.servlets;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import com.nhn.was.models.HttpRequest;
import com.nhn.was.models.HttpResponse;

public class DateTime implements SimpleServlet{

    @Override
    public void service(HttpRequest req, HttpResponse res) throws IOException {
        Writer writer = res.getWriter();
        writer.write("DateTime, ");
        writer.write((new Date()).toString());
    }

}
