package biu_project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestParser {
    /**
     * gets a buffered reader and reads a request from it and analyses it
     * @param br the buffered reader
     * @return the request divided into command, uri, uriSegmant, parameters and content
     * @throws IOException
     */
    public static RequestInfo parseRequest(BufferedReader br) throws IOException {

        String httpCommand = null;
        String uri = null;
        String[] uriSegments = null;
        Map<String, String> parameters = new HashMap<>();
        byte[] content = null;
        String temp = "";
        String line;

        try {
            // read the first line to get the command
            String firstLine = br.readLine();
            System.out.println(firstLine +" first");
//            if(firstLine.startsWith("POST")){
//                while(br.ready()){
//                    System.out.println(br.readLine());
//                }
//            }

            if (firstLine == null) { //if there's no firstline return null
                return null;
            }

            String[] firstLineParts = firstLine.split(" "); //split the firstline to command,uri,params

            if (firstLineParts.length < 2) {
                return null;
            }

            httpCommand = firstLineParts[0]; //the command
            String fullUri = firstLineParts[1];
            int questionMarkIndex = fullUri.indexOf('?');

            if (questionMarkIndex != -1) { //if there's a ?
                String path = fullUri.substring(0, questionMarkIndex); //getting the path

                uri = fullUri;
                String queryString = fullUri.substring(questionMarkIndex + 1);  //get the querystr

                String[] queryParams = queryString.split("&"); //split the parameters


                for (String param : queryParams) { //get all the parameter into the map

                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        parameters.put(keyValue[0], keyValue[1]);
                    }
                }

                if (path.startsWith("/")) {
                    path = path.substring(1);
                }

                uriSegments = path.isEmpty() ? new String[0] : path.split("/");

            } else {
                uri = fullUri;
                String path = uri.startsWith("/") ? uri.substring(1) : uri;
                uriSegments = path.isEmpty() ? new String[0] : path.split("/");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        while ((line = br.readLine()) != null) {
            //ignoring unwanted lines in the header like host, content-length and so on..
            if (line.isEmpty()) break;
        }

        int webKit=0;
        boolean flag = false;
        boolean color_flag=true;
        while (br.ready() && (line = br.readLine()) != null) {
            System.out.println("data "+line);
            if(isColor(line)){ //if the line is a color
                if(color_flag) { //if its first time seeing a color its an agent color
                    parameters.put("agent_color", line);
                    color_flag=false;
                }
                else{ //topic color
                    parameters.put("topic_color", line);
                }

            }
            if(line.endsWith("\"displayMode\"")){ //if its a display mode add it to params
                line=br.readLine();
                line=br.readLine();
                parameters.put("displayMode", line);
            }
            if(line.endsWith("\"layoutGraph\"")){ //if its a display mode add it to params
                line=br.readLine();
                line=br.readLine();
                parameters.put("layoutName", line);
            }
            if (line.isEmpty()) {
                flag = !flag;
                continue;
            }
            String[] checkIfFileName = line.split(";");
            for (String par : checkIfFileName) { //add more params to the map
                if (par.contains("="))
                    parameters.put(par.split("=")[0].trim(), par.split("=")[1].trim());
                if (par.contains(":"))
                    parameters.put(par.split(":")[0].trim(), par.split(":")[1].trim());
            }


            if (!line.startsWith("------WebKit") && flag && webKit<2) { //saving the content between the WebKit
                temp += (line + "\n");
            } else if (line.startsWith("------WebKit")){
                webKit++;
            }
        }
        temp = temp.trim();
        content=temp.getBytes(); //saving the content as Bytes

        System.out.println("starttttt");
        if(content!=null) {
            content=temp.getBytes();
            System.out.println(temp);

        }else {
            System.out.println("is null");
        }
        System.out.println("end");

        return new RequestInfo(httpCommand, uri, uriSegments, parameters, content);
    }

    /**
     * checks if a string represents a color
     * @param line the str to check if it's a color
     * @return if the str is a color
     */
    public static boolean isColor(String line) {

        final Pattern HEXADECIMAL_PATTERN = Pattern.compile("\\p{XDigit}+");


        if(!line.startsWith("#") || line.length()>7){ //if its too long or not starting with # its not a color
            return false;
        }
        final Matcher matcher = HEXADECIMAL_PATTERN.matcher(line.substring(1)); //check if its hex
        return matcher.matches();
    }


    /***
     * request information
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
