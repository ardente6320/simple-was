package com.nhn.was;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhn.was.utils.HttpUtils;
import com.nhn.was.utils.LogUtils;

public class RequestProcessor implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(RequestProcessor.class);
    private Map<String,File> rootDirectorys;
    private Map<String,Map<Integer,String>> errFiles;
    private Socket connection;

    public RequestProcessor(Map<String,File> rootDirectorys,Map<String,Map<Integer,String>> errFiles, Socket connection) {
        this.rootDirectorys = rootDirectorys;
        this.errFiles = errFiles;
        this.connection = connection;
    }

    @Override
    public void run() {
        // for security checks
        try {
            OutputStream raw = new BufferedOutputStream(connection.getOutputStream());
            Writer out = new OutputStreamWriter(raw);
            Reader in = new InputStreamReader(new BufferedInputStream(connection.getInputStream()), "UTF-8");
            StringBuilder requestLine = new StringBuilder();

            while (true) {
                int c = in.read();
                if (c == '\r' || c == '\n')
                    break;
                requestLine.append((char) c);
            }

            String get = requestLine.toString();
            String hostName = connection.getInetAddress().getHostName();

            LOG.info("[{}] {}",connection.getRemoteSocketAddress(),get);

            if(!rootDirectorys.containsKey(hostName)) {
                HttpUtils.response(out, HttpURLConnection.HTTP_NOT_FOUND, "HTTP/1.1", "text/html; charset=utf-8", HttpUtils.getDefault404Body());
                return;
            }

            String[] tokens = get.split("\\s+");
            String method = tokens[0];
            String version = "";
            String param = null;
            if (method.equals("GET")) {
                String[] contexts = tokens[1].split("?");
                String contextPath = contexts[0];
                param = contexts[1];
                version = tokens.length > 2 ? tokens[2]:version;

                if (contextPath.endsWith("/")) {
                    // can't find the file
                    response(hostName,errFiles.get(hostName).get(HttpURLConnection.HTTP_FORBIDDEN),param,version,out);
                    return;
                }

                response(hostName,contextPath,param,version,out);

            } else {
                // method does not equal "GET"
                response(hostName,errFiles.get(hostName).get(HttpURLConnection.HTTP_INTERNAL_ERROR),param,version,out);
            }
        } catch (IOException ex) {
            LOG.error("Error talking to {} :: trace :: {}",connection.getRemoteSocketAddress(), LogUtils.getStackTrace(ex));
        } finally {
            try {
                connection.close();
            } catch (IOException ex) {}
        }
    }

    /**
     * Response To Client
     * @param hostName
     * @param contextPath
     * @param param
     * @param version
     * @param out
     * @throws IOException
     */
    private void response(String hostName, String contextPath, String param, String version,Writer out) throws IOException {
        File rootDirectory = rootDirectorys.get(hostName);

        String contentType = URLConnection.getFileNameMap().getContentTypeFor(contextPath);
        File theFile = new File(rootDirectory, contextPath);

        if (theFile.canRead()
                // Don't let clients outside the document root
                && theFile.getCanonicalPath().startsWith(rootDirectory.getPath())) {
            byte[] theData = Files.readAllBytes(theFile.toPath());

            HttpUtils.response(out, HttpURLConnection.HTTP_OK, version, contentType, new String(theData,"UTF-8"));

        } else {
            // can't find the file
            theFile = new File(rootDirectory, errFiles.get(hostName).get(HttpURLConnection.HTTP_NOT_FOUND));
            byte[] theData = Files.readAllBytes(theFile.toPath());
            HttpUtils.response(out, HttpURLConnection.HTTP_NOT_FOUND, version, "text/html; charset=utf-8", new String(theData,"UTF-8"));
        }
    }
}