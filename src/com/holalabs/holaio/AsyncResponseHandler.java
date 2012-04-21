package com.holalabs.holaio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

public class AsyncResponseHandler {
		
	final Handler mHandler = new Handler();
	
	/* @params
	 *  response: It's a JSONObject that the dev will parse
	 */
	public void onSuccess(JSONObject response) {}
	
	/*
	 * Runs if there's an error during the request
	 * @params
	 * error: the cause of the error
	 * response: the content of the response, if any
	 */
	public void onError(Throwable error, String response) {}
	
	// Runs after the request thread finishes
	public void onFinish() {}
	
	final Runnable mUpdateResults = new Runnable() {
        public void run() {
        	onFinish();
        }
    };
	
    // When receiving the finish message from AsyncHttpRequest, add the runnable to the UI thread's MessageQueue
	protected void sendFinishMessage() {
		mHandler.post(mUpdateResults);
	}
	
	// When there's an error during the AsyncHttpRequest
	protected void sendErrorMessage(Throwable e, String response) {
		onError(e, response);
	}
	
	// Get the response from the AsyncHttpRequest and pass it to onSuccess()
	void sendResponseMessage(HttpResponse response, String getURL, Boolean cache) {
		StatusLine status = null;
		if (response != null)
			status = response.getStatusLine();
        String responseBody = null;
        HolaIOSingleton singleton = null;
        if (cache)
        	singleton = HolaIOSingleton.getInstance();
        try {
        	if (response != null) { 
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	            responseBody = reader.readLine();
	            if (cache)
	            	singleton.setCachedValue(getURL, responseBody);
        	} else {
        		responseBody = singleton.getCachedValue(getURL);
        	}
            
            JSONObject jsonResponse = new JSONObject(responseBody);
            if (response != null) {
            	if (status.getStatusCode() >= 300) {
            		sendErrorMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
            	} else {
            		onSuccess(jsonResponse);
            	}
            } else {
            	onSuccess(jsonResponse);
            }
        } catch(IOException e) {
        	sendErrorMessage(e, null);
            e.printStackTrace();
        } catch (JSONException e2) {
        	sendErrorMessage(e2, null);
        	e2.printStackTrace();
        };
    }
}