package biu_project.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import biu_project.server.MyHTTPServer;
import biu_project.server.RequestParser;
import biu_project.server.RequestParser.RequestInfo;
import biu_project.servlets.Servlet;


public class MainTrain5 { // RequestParser
    

    private static void testParseRequest() {
        // Test data
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
                            "Host: example.com\n" +
                            "Content-Length: 5\n"+
                            "\n" +
                            "filename=\"hello_world.txt\"\n"+
                            "\n" +
                            "hello world!\n"+
                            "\n" ;

        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);

            // Test HTTP command
            if (!requestInfo.getHttpCommand().equals("GET")) {
                System.out.println("HTTP command biu_project.test failed (-5)");
            }

            // Test URI
            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
                System.out.println("URI biu_project.test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"api", "resource"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments biu_project.test failed (-5)");
                for(String s : requestInfo.getUriSegments()){
                    System.out.println(s);
                }
            } 
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            expectedParams.put("filename","\"hello_world.txt\"");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters biu_project.test failed (-5)");
            }

            // Test content
            byte[] expectedContent = "hello world!\n".getBytes();
            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
                System.out.println("Content biu_project.test failed (-5)");
            } 
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }        
    }
	





    public static void testServer() throws Exception {
        // Create a calculator servlet
        Servlet calculatorServlet = new Servlet() {
            @Override
            public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
                Map<String, String> params = ri.getParameters();
                PrintWriter writer = new PrintWriter(toClient, true);
                
                // Get parameters
                String num1Str = params.get("num1");
                String num2Str = params.get("num2");
                String operation = params.get("op");
                
                try {
                    double num1 = Double.parseDouble(num1Str);
                    double num2 = Double.parseDouble(num2Str);
                    double result = 0;
                    
                    switch (operation) {
                        case "add": result = num1 + num2; break;
                        case "subtract": result = num1 - num2; break;
                        case "multiply": result = num1 * num2; break;
                        case "divide": result = num1 / num2; break;
                        default:
                            writer.println("HTTP/1.1 400 Bad Request\r\n\r\nInvalid operation");
                            return;
                    }
                    
                    writer.print("HTTP/1.1 200 OK\r\n");
                    writer.print("Content-Type: text/plain\r\n");
                    writer.print("\r\n");
                    writer.print("Result: " + result);
                    writer.flush();
                    
                } catch (NumberFormatException e) {
                    writer.println("HTTP/1.1 400 Bad Request\r\n\r\nInvalid numbers");
                } catch (Exception e) {
                    writer.println("HTTP/1.1 500 Internal Server Error\r\n\r\n" + e.getMessage());
                }
            }
            
            @Override
            public void close() throws IOException {}
        };
        
        // Start biu_project.server on port 8081 to avoid conflicts
        MyHTTPServer server = null;
        int port = 8083; // Changed from 8080
        
        try {
            server = new MyHTTPServer(port, 5);
            server.addServlet("GET", "/calculate", calculatorServlet);
            server.start();
            
            // Give biu_project.server time to start
            Thread.sleep(1000);
            System.out.println("Server started on port " + port);

            // Create client and send proper HTTP request
            try (Socket clientSocket = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                
                // Send complete HTTP request
                out.print("GET /calculate?num1=2&num2=3&op=add HTTP/1.1\r\n");
                out.print("Host: localhost\r\n");
                out.print("Connection: close\r\n");
                out.print("\r\n");
                out.flush();

                // Read response
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line).append("\n");
                }
                
                // Verify response
                if (!response.toString().contains("Result: 5.0")) {
                    System.out.println("Incorrect response:\n" + response);
                } else {
                    System.out.println("Server biu_project.test completed successfully!");
                }
            }
            
        } catch (Exception e) {
            System.out.println("Server biu_project.test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (server != null) {
                server.close();
            }
        }
    }
    public static void main(String[] args) {
        testParseRequest(); // 40 points
        try{
            testServer(); // 60
        }catch(Exception e){
            System.out.println("your biu_project.server throwed an exception (-60)");
        }
        System.out.println("done");
    }

}
