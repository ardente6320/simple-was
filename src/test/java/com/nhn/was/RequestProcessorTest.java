package com.nhn.was;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nhn.was.models.HttpRequest;
import com.nhn.was.models.HttpResponse;

public class RequestProcessorTest {

    static Map<String,Map<Integer,String>> errFiles = new HashMap<>();
    static Map<String,File> rootDirectorys = new HashMap<>();

    @BeforeClass
    public static void initialize() throws IOException, ParseException, URISyntaxException {
        // get the Document root
        ClassLoader loader = Application.class.getClassLoader();
        Path path = Paths.get(loader.getResource("setup.json").toURI());
        Reader reader = new FileReader(path.toString());

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);

        // set the port to listen on
        int port = Integer.parseInt(jsonObject.get("port").toString());

        JSONArray hosts = (JSONArray) jsonObject.get("hosts");

        for(Object obj : hosts){
            JSONObject host = (JSONObject) obj;

            String hostName = host.get("hostName").toString();
            File rootDirectory = new File(host.get("documentRoot").toString());

            if (!rootDirectory.isDirectory()) {
                throw new IOException(rootDirectory
                        + " does not exist as a directory");
            }

            rootDirectorys.put(hostName, rootDirectory);

            JSONArray errList = (JSONArray) host.get("errors");
            Map<Integer,String> errMap = new HashMap<>();

            for(Object err :errList.toArray()) {
                JSONObject errInfo = (JSONObject)err;
                errMap.put(Integer.parseInt(errInfo.get("code").toString()),errInfo.get("fileName").toString());
            }

            errFiles.put(hostName, errMap);
        }

    }

    @Test
    public void dateTimetest() throws InterruptedException, IOException {
        //given
        ServerSocket mockServer = mock(ServerSocket.class);
        Socket mockSocket = mock(Socket.class);

        when(mockServer.accept()).thenReturn(mockSocket);

        HttpRequest req = new HttpRequest(mock(InputStreamReader.class));
        HttpResponse res = new HttpResponse(mock(OutputStreamWriter.class));

        RequestProcessor processor = new RequestProcessor(rootDirectorys,errFiles,mockSocket);

        try {
            //when
            processor.response(req, res, "/DateTime");

            //then
            //pass
        }catch(Exception e) {
            fail("Should Not have thrown any Exception");
        }
    }


    @Test
    public void helloTest() throws InterruptedException, IOException {
        //given
        ServerSocket mockServer = mock(ServerSocket.class);
        Socket mockSocket = mock(Socket.class);

        when(mockServer.accept()).thenReturn(mockSocket);

        HttpRequest req = new HttpRequest(mock(InputStreamReader.class));
        HttpResponse res = new HttpResponse(mock(OutputStreamWriter.class));

        req.setParam("name=tester");

        RequestProcessor processor = new RequestProcessor(rootDirectorys,errFiles,mockSocket);

        try {
            //when
            processor.response(req, res, "/Hello");

            //then
            //pass
        }catch(Exception e) {
            fail("Should Not have thrown any Exception");
        }
    }


    @Test(expected=NullPointerException.class)
    public void helloEmptyParamTest() throws InterruptedException, IOException {
        //given
        ServerSocket mockServer = mock(ServerSocket.class);
        Socket mockSocket = mock(Socket.class);

        when(mockServer.accept()).thenReturn(mockSocket);

        HttpRequest req = new HttpRequest(mock(InputStreamReader.class));
        HttpResponse res = new HttpResponse(mock(OutputStreamWriter.class));


        RequestProcessor processor = new RequestProcessor(rootDirectorys,errFiles,mockSocket);

        //when
        processor.response(req, res, "/Hello");

        //then
        //Occurred NullPointer Exception
    }




    @Test
    public void serviceHelloTest() throws InterruptedException, IOException {
        //given
        ServerSocket mockServer = mock(ServerSocket.class);
        Socket mockSocket = mock(Socket.class);

        when(mockServer.accept()).thenReturn(mockSocket);

        HttpRequest req = new HttpRequest(mock(InputStreamReader.class));
        HttpResponse res = new HttpResponse(mock(OutputStreamWriter.class));

        req.setParam("name=tester");

        RequestProcessor processor = new RequestProcessor(rootDirectorys,errFiles,mockSocket);

        try {
            //when
            processor.response(req, res, "/service.Hello");

            //then
            //pass
        }catch(Exception e) {
            fail("Should Not have thrown any Exception");
        }

    }

    @Test(expected=NullPointerException.class)
    public void serviceHelloEmptyParamTest() throws InterruptedException, IOException {
        //given
        ServerSocket mockServer = mock(ServerSocket.class);
        Socket mockSocket = mock(Socket.class);

        when(mockServer.accept()).thenReturn(mockSocket);

        HttpRequest req = new HttpRequest(mock(InputStreamReader.class));
        HttpResponse res = new HttpResponse(mock(OutputStreamWriter.class));


        RequestProcessor processor = new RequestProcessor(rootDirectorys,errFiles,mockSocket);

        //when
        processor.response(req, res, "/service.Hello");

        //then
        //Occurred NullPointer Exception
    }
}
