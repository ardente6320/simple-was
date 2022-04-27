package com.nhn.was.utils;

import java.nio.file.AccessDeniedException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HttpUtilsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void checkPathValidationTest() throws AccessDeniedException{
        //given
        String given = "/test/Hello";

        //when
        HttpUtils.checkPathValidation(given);

        //then
        //pass when success
    }

    @Test
    public void checkPathValidationFailTest() throws AccessDeniedException{
        //given
        String given = "/../../test/Hello";

        //Exception Then
        expectedException.expect(AccessDeniedException.class);

        //when
        HttpUtils.checkPathValidation(given);
    }

    @Test
    public void checkPathValidationExeFailTest() throws AccessDeniedException{
        //given
        String given = "/test/Hello.exe";

        //Exception Then
        expectedException.expect(AccessDeniedException.class);

        //when
        HttpUtils.checkPathValidation(given);
    }
}
