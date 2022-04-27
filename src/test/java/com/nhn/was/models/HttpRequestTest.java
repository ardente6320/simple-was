package com.nhn.was.models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class HttpRequestTest {

    @Test
    public void setParamTest() throws IOException {
        //given
        HttpRequest req = new HttpRequest(mock(InputStreamReader.class));
        String givenParam = "name=tester&age=29";
        String expected = "tester";

        //when
        req.setParam(givenParam);

        //then
        assertEquals(expected,req.getParameter("name"));
    }
}
