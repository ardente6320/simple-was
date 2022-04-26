package com.nhn.was;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.AccessDeniedException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhn.was.models.HttpRequest;
import com.nhn.was.models.HttpResponse;
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
            HttpRequest req = new HttpRequest(connection);
            HttpResponse res = new HttpResponse(connection);

            Reader in = req.getReader();
            Writer out = res.getWriter();

            StringBuilder requestLine = new StringBuilder();

            while (true) {
                int c = in.read();
                if (c == '\r' || c == '\n')
                    break;
                requestLine.append((char) c);
            }

            String get = requestLine.toString();
            req.setHostName(connection.getInetAddress().getHostName());
            req.setContentType("text/html; charset=utf-8");

            LOG.info("[{}] {}",connection.getRemoteSocketAddress(),get);

            if(!rootDirectorys.containsKey(req.getHostName())) {
                LOG.error("HostName :: {} trace :: {}",req.getHostName(),new SocketException());
                HttpUtils.response(out, HttpURLConnection.HTTP_NOT_FOUND, "HTTP/1.1", req.getContentType(), HttpUtils.getDefault404Body());
                return;
            }

            String[] tokens = get.split("\\s+");
            req.setMethod(tokens[0]);

            if ("GET".equals(req.getMethod())) {
                String[] contexts = tokens[1].split("\\?");

                String contextPath = contexts[0];

                if(contexts.length > 1) {
                    req.setParam(contexts[1]);
                }

                if(tokens.length > 2) {
                    req.setVersion(tokens[2]);
                }

                if (contextPath.endsWith("/")) {
                    // can't find the file
                    response(req,res,errFiles.get(req.getHostName()).get(HttpURLConnection.HTTP_NOT_FOUND));
                    return;
                }

                response(req,res,contextPath);

            } else {
                // method does not equal "GET"
                response(req,res,errFiles.get(req.getHostName()).get(HttpURLConnection.HTTP_INTERNAL_ERROR));
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
     * @throws
     * @throws IOException
     */
    private void response(HttpRequest req, HttpResponse res,String contextPath) throws IOException {
        String hostName = req.getHostName();
        String version = req.getVersion();
        String contentType = req.getContentType();
        File rootDirectory = rootDirectorys.get(hostName);

        int httpStatus = HttpURLConnection.HTTP_OK;

        try {
            //URL path 검증
            HttpUtils.checkPathValidation(contextPath);

            //리플렉션
            Class<?> clazz = Class.forName("com.nhn.was.servlets."+contextPath.substring(1));
            Method method = clazz.getDeclaredMethod("service", HttpRequest.class,HttpResponse.class);
            Constructor<?> constructor = clazz.getConstructor();

            HttpUtils.sendHeader(res.getWriter(), HttpUtils.getHeader(httpStatus), contentType);
            method.invoke(constructor.newInstance(), req,res);
            res.getWriter().flush();

        }catch(ClassNotFoundException e) {
            LOG.error("trace :: {}",LogUtils.getStackTrace(e));

            httpStatus = HttpURLConnection.HTTP_NOT_FOUND;

            String body = HttpUtils.getBodyString(new File(rootDirectory, errFiles.get(hostName).get(httpStatus)));
            HttpUtils.response(res.getWriter(), httpStatus, version, contentType, body);
        }catch(AccessDeniedException e) {
            LOG.error("trace :: {}",LogUtils.getStackTrace(e));

            httpStatus = HttpURLConnection.HTTP_FORBIDDEN;

            String body = HttpUtils.getBodyString(new File(rootDirectory, errFiles.get(hostName).get(httpStatus)));
            HttpUtils.response(res.getWriter(), httpStatus, version, contentType, body);
        }catch(IOException |
               InvocationTargetException |
               NullPointerException |
               NoSuchMethodException |
               IllegalAccessException |
               IllegalArgumentException |
               InstantiationException e) {
            LOG.error("trace :: {}",LogUtils.getStackTrace(e));

            httpStatus = HttpURLConnection.HTTP_INTERNAL_ERROR;

            String body = HttpUtils.getBodyString(new File(rootDirectory, errFiles.get(hostName).get(httpStatus)));
            HttpUtils.response(res.getWriter(), httpStatus, version, contentType, body);
        }finally {
            req.getReader().close();
            res.getWriter().close();
        }
    }
}