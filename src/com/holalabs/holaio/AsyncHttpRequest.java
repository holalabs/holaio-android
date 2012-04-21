package com.holalabs.holaio;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HttpContext;

public class AsyncHttpRequest implements Runnable {

	protected HolaIOSingleton singleton;
	
	private final HttpClient client;
    private final HttpContext context;
    private final HttpGet req;
    private final Boolean cache;
    private final String getURL;
    private final AsyncResponseHandler resHandler;
    
    // Initialize the AsyncHttpRequest
    public AsyncHttpRequest(HttpClient client, HttpContext context, HttpGet req, Boolean cache, String getURL, AsyncResponseHandler resHandler) {
    	this.client = client;
    	this.context = context;
    	this.req = req;
    	this.cache = cache;
    	this.getURL = getURL;
    	this.resHandler = resHandler;
    	if (cache)
    		singleton = HolaIOSingleton.getInstance();
    }
    
    // The runnable's run() method
	@Override
	public void run() {
		try {
			// Do the request and get the response.
			if(!Thread.currentThread().isInterrupted()) {
	    		HttpResponse response;
	    		if (!cache)	{
	    			response = client.execute(req, context);
	    		} else {
	    			if (singleton.getCachedValue(getURL) == null) {
	    				 response = client.execute(req, context);
	    			} else {
	    				response = null;
	    			}
	    		}
	    		if(!Thread.currentThread().isInterrupted()) {
	    			if(resHandler != null) {
	    				// Send the response to the AsyncResponseHandler
	    				resHandler.sendResponseMessage(response, getURL, cache);
	    			}
	    		}
	    	}
			// When the thread ends, send a finish message to the AsyncResponseHandler
			if(resHandler != null) {
                resHandler.sendFinishMessage();
            }
		} catch(IOException e) {
			if(resHandler != null) {
				resHandler.sendErrorMessage(e, null);
                resHandler.sendFinishMessage();
            }
			e.printStackTrace();
		}
	}
}