package biu_project.servlets;

import java.io.IOException;
import java.io.OutputStream;

import biu_project.server.RequestParser.RequestInfo;

public interface Servlet {
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;
    void close() throws IOException;
}
