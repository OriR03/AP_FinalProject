package biu_project.servlets;

import biu_project.configs.GenericConfig;
import biu_project.configs.Graph;
import biu_project.configs.Node;
import biu_project.server.RequestParser;
import biu_project.views.HtmlGraphWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;


public class ConfLoader implements Servlet , Observer {
    private OutputStream output = null;
    private volatile boolean htmlReady = false; // Use volatile for thread-safe visibility

    /**
     * reads a configuration from a request and builds a graph from it
     *
     * @param ri the request
     * @param toClient the output stream
     * @throws IOException
     */
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {

        while(ri.getParameters().get("filename")==null){//avoid race condition with the parser
            //sometimes the request parsing does not catch up and the object has no content fields
            //and the filename in question is null
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();//interrupt if there is an error
                break;
            }
        }
        output =toClient;
        String errorResponse = "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: 41\r\n" +
                "\r\n" +
                "Error - bad request, click \"deploy\" again";
        String file_name = ri.getParameters().get("filename").replace("\"","");;

        for(String p: ri.getParameters().keySet()){
            System.out.println(p+" param "+ri.getParameters().get(p));
        }

        String str = new String(ri.getContent(), StandardCharsets.UTF_8);//get the actual conf file contents

        System.out.println("file name "+file_name);

        if (file_name == null || file_name.trim().isEmpty()) {
            toClient.write(errorResponse.getBytes(StandardCharsets.UTF_8));
            return;
        }

        System.out.println();
        File file = new File("src/graph_display/"+file_name);//create a temp file with the contents of the chosen file
        //as the browser does not send the location of the file on the computer

        //as genericConfig needs to get a file.
        FileOutputStream fos = new FileOutputStream(file);//write the contents to the temp file
        fos.write(str.getBytes(StandardCharsets.UTF_8));

        biu_project.graph.TopicManagerSingleton.get().clear();//clear the TopicManagerSingleton -
        // without it we create dups each deploy press
        GenericConfig gc =  new GenericConfig();
        gc.setConfFile("src/graph_display/"+file_name);//create a generic config with the temp file
        gc.create();
        Graph g=new Graph();
        g.createFromTopics();//cast the file to a graph object
        if(g.hasCycles()){
            boolean deleted = file.delete();//delete temp file

            String body = "<p style='color:red; font-size:50px'>Error - The Chosen Graph Has Cycles</p>";
            String response =
                    "HTTP/1.1 400 Bad Request\r\n" +
                            "Content-Type: text/html\r\n" +
                            "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "\r\n" +
                            body;

            toClient.write(response.getBytes(StandardCharsets.UTF_8));
            fos.close();
            return;
        }
        HtmlGraphWriter gw = new HtmlGraphWriter();
        gw.addObserver(this);

        htmlReady = false;
        gw.writeGraph(g,ri.getParameters());//generate the graph on the view layer

        // wait until the html is ready from the view model
        while (!htmlReady) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();//interrupt if there is an error
                break;
            }
        }

       //html graph file is ready, send it to client
        String response = "HTTP/1.1 303 See Other\r\n" +
                "Location: /app/graph.html\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";
        toClient.write(response.getBytes(StandardCharsets.UTF_8));

        boolean deleted = file.delete();//delete temp file

        fos.close();//close filestreams
        //fos2.close();

    }

    @Override
    public void close() throws IOException {

    }

    /**
     * this method serves the graph after the view layer generates it
     * @param o     the observable object.
     * @param arg   the template file with the correct and current info to be served
     */
    @Override
    public void update(Observable o, Object arg) {
        try {
            Files.write(Paths.get("src/graph_display/graph_gui.html"), ((String) arg).getBytes());
        } catch (IOException e) {}

        finally {
            htmlReady = true;
        }
    }
}
