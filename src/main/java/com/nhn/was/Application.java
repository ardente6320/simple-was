package com.nhn.was;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {
    private static final Logger logger = Logger.getLogger(HttpServer.class.getCanonicalName());

    public static void main(String[] args) {
        try {  
            // get the Document root
            Reader reader = new FileReader(args[0]);

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            // set the port to listen on
            int port = (Integer)jsonObject.get("port");

            JSONArray hosts = (JSONArray) jsonObject.get("hosts");
            
            for(Object obj : hosts){
                JSONObject host = (JSONObject) obj;

                HttpServer webserver = new HttpServer(host, port);
                webserver.start();
            }
        } catch (ArrayIndexOutOfBoundsException | FileNotFoundException e) {
            System.out.println("Usage: java JHTTP docroot port");
            return;
        } catch (IOException | ParseException e) {
            logger.log(Level.SEVERE, "Server could not start", e);
        }
    }
}
