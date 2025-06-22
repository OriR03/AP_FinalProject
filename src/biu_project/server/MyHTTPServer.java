package biu_project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import biu_project.server.RequestParser.RequestInfo;
import biu_project.servlets.Servlet;


public class MyHTTPServer extends Thread implements HTTPServer{
	private int port;
	private int nThreads;
	private ExecutorService tPool;
	private ServerSocket socket;
	private final Map<String, Servlet> getServlet = new ConcurrentHashMap<>();
	private final Map<String, Servlet> postServlet = new ConcurrentHashMap<>();
	private final Map<String, Servlet> deleteServlet = new ConcurrentHashMap<>();	private volatile boolean stop = false;
	String error_msg = "HTTP/1.1 404 Not Found\n"+
			"Content-Type: text/plain\n"+
			"Content-Length: 13\n"+
			"\n"+
			"servlet error";
	String error_msg2 = "HTTP/1.1 404 Not Found\n"+
			"Content-Type: text/plain\n"+
			"Content-Length: 13\n"+
			"\n"+
			"servlet error2";

	/**
	 * MyHttpConstructor, creates a threadpool of desired number of threads
	 * @param port
	 * @param nThreads number of threads allocated to the server
	 */
	public MyHTTPServer(int port,int nThreads){
		this.port = port;
		this.nThreads = nThreads;
		this.tPool = Executors.newFixedThreadPool(nThreads);

	}

	/**
	 * This method put the uri and servlet to the correct httpcommand map
	 * @param httpCommand get/post/delete
	 * @param uri uri of the request
	 * @param s Servlet to put to the map
	 */
	public void addServlet(String httpCommand, String uri, Servlet s) {
		httpCommand = httpCommand.toLowerCase();
		switch (httpCommand) {
			case "get"://add the servlet to the correct map
				this.getServlet.put(uri, s);
				break;
			case "post":
				this.postServlet.put(uri, s);
				break;
			case "delete":
				this.deleteServlet.put(uri, s);
				break;
		}
	}

	/**
	 * this method removes the servlet and uri from the correct map
	 * @param httpCommand
	 * @param uri
	 */
	public void removeServlet(String httpCommand, String uri) {
		httpCommand = httpCommand.toLowerCase();
		switch (httpCommand) {//get the servlet from correct map
			case "get":
				this.getServlet.remove(uri);
				break;
			case "post":
				this.postServlet.remove(uri);
				break;
			case "delete":
				this.deleteServlet.remove(uri);
				break;
		}
	}

	/**
	 * The method accepts a new client, add it to the thread pool and executes the handle function
	 */
	public void run(){//open socket and set timeout to 5s
		try {
			this.socket = new ServerSocket(port);
			this.socket.setSoTimeout(5000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(!stop) {
			try {
				Socket client = socket.accept();
				tPool.execute(()->handle(client));//each connected client - run the handle function
			}
			catch (java.net.SocketTimeoutException e) {
				continue;
			}
			catch (java.net.SocketException e) {
				if(stop) {break;};
			}

			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

	}


	/**
	 * The method gets the input and output streams, parse the info from the request and returns the correct
	 * servlet based on the request
	 * @param c client socket
	 */
	private void handle(Socket c) {
		try {
			InputStream str = c.getInputStream();
			BufferedReader input = (new BufferedReader(new InputStreamReader(str)));
			OutputStream output = c.getOutputStream();//get input and save the outputstream

			//System.out.println(input);
			RequestInfo r = RequestParser.parseRequest(input);//parse the request
			System.out.println(r.getHttpCommand());
			if (r == null) {
				System.out.println("11111");
				output.write(error_msg.getBytes());
				return;
			}

			String uri = r.getUri().split("\\?")[0];//get the address first part of the uri
			String command = r.getHttpCommand().toLowerCase();
			if (uri.equals("/app")||uri.equals("/app/")) {//redirect to /app/index.html if user enters /app or /app/
				String redirectResponse = "HTTP/1.1 302 Found\r\nLocation: /app/index.html\r\n\r\n";
				output.write(redirectResponse.getBytes());
				return;
			}
			Servlet s = null;
			Map<String, Servlet> commandServlets=null;
			switch (command) {
				case "get":
					commandServlets=this.getServlet;
					break;
				case "post":
					commandServlets=this.postServlet;
					break;
				case "delete":
					commandServlets=this.deleteServlet;
					break;
			}
			if (commandServlets != null) {
				// find a servlet that matches the start of the path
				for (String servletUri : commandServlets.keySet()) {
					if (uri.startsWith(servletUri)) {
						s = commandServlets.get(servletUri);
						break;
					}
				}
			}

			if (s == null) {
				output.write("HTTP/1.1 404 Not Found\r\n\r\nServlet not found for this URI.".getBytes());
				return;
			}

			s.handle(r, output);


		} catch (IOException e) {
		} finally {
			try {
				c.close();
			} catch (IOException e) {
			}
		}
	}





	public void close(){
		stop = true;
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tPool.shutdown();

	}

}
