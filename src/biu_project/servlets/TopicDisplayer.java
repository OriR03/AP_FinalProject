package biu_project.servlets;

import biu_project.graph.Message;
import biu_project.graph.Topic;
import biu_project.server.RequestParser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import biu_project.graph.TopicManagerSingleton.*;
import biu_project.graph.TopicManagerSingleton;
import java.io.PrintWriter;


/**
 * The class is responsible for generating the table of agents+messages sent
 */
public class TopicDisplayer implements Servlet{
    public static TopicManager tm = TopicManagerSingleton.get();

    Map<String,String> topicMsgMap = new HashMap<>(); //map from topic to its message

    /**
     * this method generates the table of topics+messages
     * @param ri parsed info as a RequestParser.RequestInfo
     * @param toClient outputstream
     * @throws IOException
     */
    @Override
    public void handle(RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        Map<String, String> params = ri.getParameters(); //getting all the params from the ri
        for(String key : params.keySet()){
            System.out.println(key + ": " + params.get(key));
        }
        String topic="", msg="";
        for(String key : params.keySet()){
            if(key.startsWith("topic") || key.startsWith("Topic")){ //getting the topic from the params
                topic = params.get(key);
            } else if(key.startsWith("message") || key.startsWith("Message")){ //getting the message from the params
                msg = params.get(key);
            }
        }

        Topic t =  tm.getTopic(topic.toUpperCase());
        Message m = new Message(msg);
        t.publish(m); //publish the msg

        topicMsgMap.put(t.name.toUpperCase(),m.asText); //adding the topic and the msg to the map
        PrintWriter out = new PrintWriter(toClient, true); //writing to the client the table
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println();

        out.println("<html>");
        out.println("<head><title>Topic Published</title>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; display: flex; flex-direction: column; align-items: center; justify-content: flex-start; min-height: 100vh; }");
        out.println("h1 { font-size: 2rem; margin-bottom: 20px; text-align: center; font-weight: 400; color: #333; }");
        out.println("table { width: 100%; max-width: 800px; border-collapse: collapse; margin: 20px 0; border: 1px solid #ddd; border-radius: 8px; overflow: hidden; }");
        out.println("th, td { padding: 15px 20px; text-align: left; border-bottom: 1px solid #eee; }");
        out.println("th { background-color: #f4f4f4; color: #333; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; }");
        out.println("tr:hover td { background-color: #f9f9f9; }");
        out.println(".empty-message { text-align: center; color: #888; font-style: italic; padding: 20px 0; }");
        out.println("@media (max-width: 600px) { h1 { font-size: 1.5rem; } th, td { padding: 10px 12px; } }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Message Published</h1>");
        out.println("<table border='1'>");
        out.println("<tr><th>Topic Name</th><th>Last Message</th></tr>");

        for(Topic t1: tm.getTopics()){ //outputting the table
            if(t1.msg!=null)
                out.println("<tr><td>" + t1.name + "</td><td>" + t1.msg + "</td></tr>");
            else{
                out.println("<tr><td>" + t1.name + "</td><td> </td></tr>");
            }
        }

        out.println("</table>");
        out.println("</body>");
        out.println("</html>");

        out.close();
    }

    @Override
    public void close() throws IOException {

    }
}
