package com.holalabs.holaio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

public class AsyncResponseHandler {
		
	final Handler mHandler = new Handler();
	
	/* @params
	 *  response: It's a JSONObject that the dev will parse
	 */
	public void onSuccess(JSONObject response) {}
	
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
	
	// Get the response from the AsyncHttpRequest and pass it to onSuccess()
	void sendResponseMessage(HttpResponse response) {
        String responseBody = null;
        try {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            responseBody = reader.readLine();
        } catch(IOException e) {
            e.printStackTrace();
        }

		try {
			JSONObject jsonResponse = new JSONObject(responseBody);
	        onSuccess(jsonResponse);
		} catch (JSONException e) {
			e.printStackTrace();
		};
    }
	
}