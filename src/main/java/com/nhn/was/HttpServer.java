package com.nhn.was;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhn.was.utils.LogUtils;

public final class HttpServer {
    private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);

    private static final int NUM_THREADS = 50;
    private final Map<String,Map<Integer,String>> errFiles = new HashMap<>();
    private final Map<String,File> rootDirectorys = new HashMap<>();
    private final int port;

    public HttpServer(JSONArray hosts, int port) throws IOException {
        this.port = port;

        for(Object obj : hosts){
            JSONObject host = (JSONObject) obj;

            String hostName = host.get("hostName").toString();
            File rootDirectory = new File(host.get("documentRoot").toString());

            if (!rootDirectory.isDirectory()) {
                throw new IOException(rootDirectory
                        + " does not exist as a directory");
            }

            this.rootDirectorys.put(hostName, rootDirectory);

            JSONArray errList = (JSONArray) host.get("errors");
            Map<Integer,String> errMap = new HashMap<>();

            for(Object err :errList.toArray()) {
                JSONObject errInfo = (JSONObject)err;
                errMap.put(Integer.parseInt(errInfo.get("code").toString()),errInfo.get("fileName").toString());
            }

            errFiles.put(hostName, errMap);
        }
    }

    public void start() throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        try (ServerSocket server = new ServerSocket(port)) {

            LOG.info("Accepting connections on port " + server.getLocalPort());
            while (true) {
                try {
                    Socket request = server.accept();
                    Runnable r = new RequestProcessor(rootDirectorys,errFiles, request);
                    pool.submit(r);
                } catch (IOException ex) {
                    LOG.error("Error accepting connection trace :: {}", LogUtils.getStackTrace(ex));
                }
            }
        }
    }
}
