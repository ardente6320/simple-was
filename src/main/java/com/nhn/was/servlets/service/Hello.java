package com.nhn.was.servlets.service;

import java.io.IOException;

import com.nhn.was.models.*;
import com.nhn.was.servlets.SimpleServlet;

public class Hello implements SimpleServlet {

    @Override
    public void service(HttpRequest req, HttpResponse res) throws IOException {
        java.io.Writer writer = res.getWriter();
        writer.write("Service-Hello, ");
        writer.write((String)req.getParameter("name"));
    }
    
}
