package com.nhn.was.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

public class HttpUtils {

    public static String getHeader(final int httpStatus) {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ");
        sb.append(Integer.toString(httpStatus));
        return sb.toString();
    }

    public static void sendHeader(Writer out, String responseCode, String contentType, int length) throws IOException {
        out.write(responseCode + "\r\n");
        Date now = new Date();
        out.write("Date: " + now + "\r\n");
        out.write("Server: HTTP 1.1\r\n");
        out.write("Content-length: " + length + "\r\n");
        out.write("Content-type: " + contentType + "\r\n\r\n");
        out.flush();
    }

    public static String getDefault404Body() {
        String body = new StringBuilder("<HTML>\r\n")
                .append("<HEAD><TITLE>File Not Found</TITLE>\r\n")
                .append("</HEAD>\r\n")
                .append("<BODY>")
                .append("<H1>HTTP Error 404: File Not Found</H1>\r\n")
                .append("</BODY></HTML>\r\n")
                .toString();
        return body;
    }

    public static void response(Writer out, int responseCode, String version, String contentType, String body) throws IOException {

        if (version.startsWith("HTTP/")) { // send a MIME header
            HttpUtils.sendHeader(out, getHeader(responseCode), contentType, body.length());
        }

        out.write(body);
        out.flush();
    }
}