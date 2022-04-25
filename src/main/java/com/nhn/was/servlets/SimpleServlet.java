package com.nhn.was.servlets;

import java.io.IOException;

import com.nhn.was.models.*;

public interface SimpleServlet{
    void service(HttpRequest req, HttpResponse res) throws IOException;
}
