package com.holalabs.holaio.demo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.holalabs.holaio.AsyncResponseHandler;
import com.holalabs.holaio.HolaIO;

public class HolaIODemoActivity extends ListActivity {
	
	private ArrayList<String> menuItems = new ArrayList<String>();
	// Initialize HolaIO
	HolaIO io = new HolaIO("Your API Key");
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadList();
    }
	
	private void loadList() {
		// Do a request to google.com asking for the a span content. inner is true by default.
		// Pass an AsyncResponseHandler with onSuccess(), onError() and onFinish()
		io.get("google.com", "a span", true, new AsyncResponseHandler() {
			// Parse the content. It gets the array "a span" and each element is transformed to a string and put into an ArrayList
			@Override
			public void	onSuccess(JSONObject content) {
				try {
					JSONArray aspanJSONArray = content.getJSONArray("a span");
					for(int i = 0; i < aspanJSONArray.length(); i++) {
						String element = aspanJSONArray.optString(i);
						if (!element.equalsIgnoreCase("")) {
							menuItems.add(element);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			// If there's an error during the process
			@Override
			public void onError(Throwable e, String response) {
				menuItems.add("Error:" + e.toString());
				menuItems.add("Response:" + response);
			}
			// After the thread has finished, it updates the UI thread populating the listview
			@Override
			public void onFinish() {
				setListAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, menuItems));
			}
		});
	}

	// When an element of the list is clicked, show a Toast message with the content of that element
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ListView lv = getListView();
		ListAdapter list_adapter = lv.getAdapter();
		TextView item = (TextView) list_adapter.getView(position,null,null);
		Toast.makeText(getApplicationContext(), item.getText(), Toast.LENGTH_SHORT).show();
	}
}
