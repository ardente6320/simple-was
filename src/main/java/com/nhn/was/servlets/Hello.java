package com.nhn.was.servlets;

import java.io.IOException;

import com.nhn.was.models.HttpRequest;
import com.nhn.was.models.HttpResponse;

public class Hello implements SimpleServlet{

    @Override
    public void service(HttpRequest req, HttpResponse res) throws IOException{
        java.io.Writer writer = res.getWriter();
        writer.write("Hello, ");
        writer.write((String)req.getParameter("name"));
    }
    
}
