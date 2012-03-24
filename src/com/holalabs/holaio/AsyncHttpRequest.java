package com.holalabs.holaio;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HttpContext;

public class AsyncHttpRequest implements Runnable {

	private final HttpClient client;
    private final HttpContext context;
    private final HttpGet req;
    private final AsyncResponseHandler resHandler;
    
    // Initialize the AsyncHttpRequest
    public AsyncHttpRequest(HttpClient client, HttpContext context, HttpGet req, AsyncResponseHandler resHandler) {
    	this.client = client;
    	this.context = context;
    	this.req = req;
    	this.resHandler = resHandler;
    }
    
    // The runnable's run() method
	@Override
	public void run() {
		try {
			// Do the request and get the response.
			if(!Thread.currentThread().isInterrupted()) {
	    		HttpResponse response = client.execute(req, context);
	    		if(!Thread.currentThread().isInterrupted()) {
	    			if(resHandler != null) {
	    				// Send the response to the AsyncResponseHandler
	    				resHandler.sendResponseMessage(response);
	    			}
	    		}
	    	}
			// When the thread ends, send a finish message to the AsyncResponseHandler
			if(resHandler != null) {
                resHandler.sendFinishMessage();
            }
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}