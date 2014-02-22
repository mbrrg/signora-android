package com.plushware.signora;

import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class CommandService extends Service {
	private static String TAG = "CommandService";
	
	private final Pusher mConnection = new Pusher(Secrets.PUSHER_APP_KEY);
	private Channel mChannel;
	
	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG,  "onStartCommand");

		createConnection();

		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		mConnection.disconnect();
	}
	
	private void delayedReconnect() {
		Log.d(TAG, "Attempting to re-establish connection in 10 s.");
		
		Handler handler = new Handler();					
		
		handler.postDelayed(new Runnable() { 
		    public void run() {
		    	createConnection();
		    }
		}, 10 * 1000);
	}	

	private void createConnection() {
		Log.d(TAG, "Attempting connection to Pusher.");
		
		mConnection.connect(new ConnectionEventListener() {
		    @Override
		    public void onConnectionStateChange(ConnectionStateChange change) {
		    	Log.d(TAG, "Pusher connection state changed from " + change.getPreviousState() + " to " + change.getCurrentState());
		    	
		    	if (change.getCurrentState() == ConnectionState.CONNECTED) {
		    		mChannel = mConnection.subscribe(ServerUrl.getCommandChannelName(), 
		    				new ChannelEventListener() {
		    		    @Override
		    		    public void onSubscriptionSucceeded(String channelName) {
		    		        Log.d(TAG, "Subscribed to channel " + channelName + ".");
		    		    }

		    		    @Override
		    		    public void onEvent(String channelName, String eventName, String data) {
		    		    	processCommand(eventName,  data);
		    		    }
		    		}, "restart", "screenon", "screenoff", "reload");		    		
		    	}
		    }

		    @Override
		    public void onError(String message, String code, Exception e) {
		    	Log.d(TAG, "Error connecting to Pusher.");
		    	delayedReconnect();
		    }
		});
	}
	
	public void processCommand(String command, String payload) {
		Log.d(TAG, "Received command " + command);
		
		if (command.equals("restart")) {
			PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
			pm.reboot("Request from Signora admin interface.");
		} else if (command.equals("screenon")) {
			Intent intent = new Intent(BrowserActivity.INTENT_SCREEN_ONOFF);
			intent.putExtra(BrowserActivity.EXTRA_SCREEN, true);						
			sendBroadcast(intent);						
		} else if (command.equals("screenoff")) {
			Intent intent = new Intent(BrowserActivity.INTENT_SCREEN_ONOFF);
			intent.putExtra(BrowserActivity.EXTRA_SCREEN, false);						
			sendBroadcast(intent);						
		} else if (command.equals("reload")) {
			Intent intent = new Intent(BrowserActivity.INTENT_CHANGE_URL);
			intent.putExtra(BrowserActivity.EXTRA_FORCE_RELOAD, true);
			sendBroadcast(intent);						
		}
	}	
}
