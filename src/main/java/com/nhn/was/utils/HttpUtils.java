package com.nhn.was.utils;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {

    private static final Pattern CONTEXT_PATTERN = Pattern.compile("(\\.\\.\\/)|(\\.exe)");

    /**
     * URI 검증
     * @param contextPath
     * @throws AccessDeniedException
     */
    public static void checkPathValidation(String contextPath) throws AccessDeniedException {
        Matcher m = CONTEXT_PATTERN.matcher(contextPath);

        if(m.find()) {
            throw new AccessDeniedException(contextPath);
        }
    }

    public static String getHeader(final int httpStatus) {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ");
        sb.append(Integer.toString(httpStatus));
        return sb.toString();
    }

    public static void sendHeader(Writer out, String responseCode, String contentType) throws IOException {
        out.write(responseCode + "\r\n");
        Date now = new Date();
        out.write("Date: " + now + "\r\n");
        out.write("Server: HTTP 1.1\r\n");
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

    public static String getBodyString(File file) throws IOException {
        byte[] theData = Files.readAllBytes(file.toPath());
        return new String(theData,"UTF-8");
    }

    public static void response(Writer out, int responseCode, String version, String contentType, String body) throws IOException {

        if (version.startsWith("HTTP/")) { // send a MIME header
            HttpUtils.sendHeader(out, getHeader(responseCode), contentType);
        }

        out.write(body);
        out.flush();
    }
}