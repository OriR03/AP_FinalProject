package biu_project.servlets;

import biu_project.server.RequestParser;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.*;

public class HtmlLoader implements Servlet{
    String dir_name;
    String file_name="";

    /**
     * HtmlLoader constructor
     * @param dir_name the directory name
     */
    public HtmlLoader(String dir_name){
        this.dir_name=dir_name;
    }

    /**
     * reads a file name from the request and outputs it to the client if exists
     * @param ri the request
     * @param toClient the client output stream
     * @throws IOException
     */
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        String[] uriS = ri.getUriSegments();

        PrintWriter out = new PrintWriter(toClient, true);
        for(String s: uriS){ //getting the filename from the uri
            System.out.println(s);
            if(s.endsWith(".html")){
                file_name=s;
            }
        }
        if(file_name.equals("")){ //if there's no file name in the uri print File Not Found
            out.println("HTTP/1.1 404 Not Found");
            out.println("Content-Type: text/html; charset=UTF-8");
            out.println();

            out.println("<html>");
            out.println("<head><title>File Not Found</title></head>");
            out.println("<body>");
            out.println("<h1>404 - Please enter file name</h1>");
            out.println("</body>");
            out.println("</html>");

            out.close();
            return;
        }
        File file = null;
        System.out.println("Loading "+file_name);
        if(file_name.equals("graph_gui.html")){ //if its the graph file
             file = new File("src/graph_display/"+file_name); //find it in the src/graph_display
        }else { //if its other file, find it in the dir_name
             file = new File("src/" + this.dir_name + "/" + file_name);
        }

//        File file = new File("../"+"../"+this.dir_name+"/"+file_name);
        System.out.println("L "+file.getAbsolutePath());
        if (!file.exists()) { //if file doesnt exist print a msg
            out.println("HTTP/1.1 404 Not Found");
            out.println("Content-Type: text/html; charset=UTF-8");
            out.println();

            out.println("<html>");
            out.println("<head><title>File Not Found</title></head>");
            out.println("<body>");
            out.println("<h1>404 - File Not Found</h1>");
            out.println("<p>The requested file '" + file_name + "' was not found on the server.</p>");
            out.println("</body>");
            out.println("</html>");

            out.close();
        }

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) { //output the given file 
            String line;
            while ((line = br.readLine()) != null) {
                out.println(line);
            }
        }
        //br.close();
        out.close();


    }


    @Override
    public void close() throws IOException {

    }
}
