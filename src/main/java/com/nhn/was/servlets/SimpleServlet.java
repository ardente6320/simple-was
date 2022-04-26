package com.nhn.was.servlets;

import java.io.IOException;

import com.nhn.was.models.HttpRequest;
import com.nhn.was.models.HttpResponse;

public interface SimpleServlet{
    void service(HttpRequest req, HttpResponse res) throws IOException;
}
