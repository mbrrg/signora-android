package com.plushware.signora;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class ContentCheckService extends IntentService {
	private static String TAG = "ContentCheckService";

	public ContentCheckService() {
		super(TAG);
	}
		
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent");

		checkForNewContent();		
	}		
	
	private void checkForNewContent() {
		try {
			JSONObject json = new JSONObject(registerDevice());
			
			Log.d(TAG, "Received JSON: " + json.toString(2));

			if (json.has("action")) {
				if (json.getString("action").equals("sleep")) {
					Log.d(TAG, "Broadcasting intent to enable/disable screen.");
					
					Intent intent = new Intent(BrowserActivity.INTENT_SCREEN_ONOFF);
					intent.putExtra(BrowserActivity.EXTRA_SCREEN, false);						
					sendBroadcast(intent);						
				}
			}
			
			if (json.has("url")) {
				Log.d(TAG, "Broadcasting intent to switch URL.");
				
				Intent intent = new Intent(BrowserActivity.INTENT_CHANGE_URL);
				intent.putExtra(BrowserActivity.EXTRA_URL, json.getString("url"));			
				sendBroadcast(intent);						
			}			
		} catch (Exception e) {
			Log.e(TAG, "Failed to register device." + e.getMessage());
							
			sendBroadcast(new Intent(BrowserActivity.INTENT_SHOW_DISCO));			
		}
	}

	private String registerDevice() throws Exception {
		StringBuilder result = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		
		HttpPut request = new HttpPut(ServerUrl.getRegisterDeviceUrl());	
		
		request.addHeader("Content-Type", "application/json");
		request.setEntity(RegisterDeviceRequest.getBody());
		
		HttpResponse response = client.execute(request);			
		HttpEntity entity = response.getEntity();
		
		if (entity == null) {
			throw new Exception("Empty response.");
		}
		
		Header contentType = entity.getContentType();
		
		if (contentType == null || !contentType.getValue().equals("application/json")) {
			throw new Exception("Invalid content type.");
		}
		
		InputStream stream = null;				
		
		try {
			stream = entity.getContent();					
			result = new StringBuilder();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			String line;
			
			while ((line = reader.readLine()) != null) {
				result.append(line + "\n");
			}
		} finally {
			if (stream != null) {
				try {
					stream.close();
				}
				catch (Exception e) {
				}
			}
		}
		
		return result.toString();
	}
}
