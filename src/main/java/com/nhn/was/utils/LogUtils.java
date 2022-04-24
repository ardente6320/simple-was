package com.nhn.was.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtils {
    /**
     * get Stack Trace
     * @param e
     * @return
     */
    public static String getStackTrace(Exception e){
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }
}
