package com.nhn.was;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhn.was.utils.LogUtils;

public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws URISyntaxException {
        try {
            // get the Document rootgetResource
            ClassLoader loader = Application.class.getClassLoader();
            InputStream inputStream = loader.getResourceAsStream("setup.json");
            String jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

            // set the port to listen on
            int port = Integer.parseInt(jsonObject.get("port").toString());

            JSONArray hosts = (JSONArray) jsonObject.get("hosts");

            HttpServer webserver = new HttpServer(hosts, port);
            webserver.start();
        } catch (ArrayIndexOutOfBoundsException | IOException | ParseException e) {
            LOG.error("Usage: java JHTTP docroot port trace :: {}", LogUtils.getStackTrace(e));
        }
    }
}
