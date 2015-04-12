package com.kumar;

import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class App 
{
    public static void main( String[] args )
    {
    	try
    	{
	        System.err.println( "Server is spinning up on port: " + Constants.SERVER_PORT);

	        HttpServer server = HttpServer.create(new InetSocketAddress(Constants.SERVER_PORT), 0);
	        server.createContext("/zip", new ZipHandler());
	        server.setExecutor(null);
	        server.start();
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace(System.err);
    		System.err.println(t.getMessage());
    	}
    }
}
