package com.zelusik.eatery.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

    public static String getExceptionStackTrace(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return String.valueOf(stringWriter);
    }
}
