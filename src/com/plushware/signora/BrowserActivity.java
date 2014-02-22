package com.plushware.signora;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BrowserActivity extends Activity {
	private static final String TAG = "BrowserActivity";
	
	public static final String INTENT_CHANGE_URL = "com.plushware.CHANGE_URL";
	public static final String INTENT_SHOW_DISCO = "com.plushware.SHOW_DISCO";
	public static final String INTENT_SCREEN_ONOFF = "com.plushware.SCREEN_POWER";
	public static final String EXTRA_URL = "url";
	public static final String EXTRA_FORCE_RELOAD = "force";
	public static final String EXTRA_SCREEN = "onoff";
	
	String mNavigatedUrl;
	WebView mWebView;
	WakeLock mWakeLock;
	
	private final BroadcastReceiver mBrowserUrlChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {			
			String action = intent.getAction();
			
			Log.d("BrowserUrlChangeReceiver", "Received intent action " + action);

			if (action.equals(INTENT_CHANGE_URL)) {
				String url =  intent.getStringExtra(EXTRA_URL);
				Boolean force = intent.getBooleanExtra(EXTRA_FORCE_RELOAD, false);
				
				if (url == null) {
					url = mNavigatedUrl;
				}
				
				if (showUrl(url, force)) {
					Log.d(TAG, "Loaded url " + url);
					acquireWakeLock();
				}
			} else if (action.equals(INTENT_SCREEN_ONOFF)) {				
				if (intent.getBooleanExtra(EXTRA_SCREEN, true)) {
					Log.d(TAG, "Received request to turn screen ON.");
					acquireWakeLock();						
				} else {
					Log.d(TAG, "Received request to turn screen OFF.");
					releaseWakeLock();
				}
			} else if (action.equals(INTENT_SHOW_DISCO)) {
				Log.d(TAG, "Showing disconnection page.");
				
				showDisconnectedPage();
				acquireWakeLock();
			}
		}
	};	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        
        setContentView(R.layout.activity_browser);
                
        initWebView();        
		initReceiver();
		createWakeLock();
		acquireWakeLock();
		
		startService(new Intent(this, CommandService.class));       		        
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	Log.d(TAG, "onDestroy");
    	
		releaseWakeLock();
		unregisterReceiver(mBrowserUrlChangeReceiver);
    }

	private void releaseWakeLock() {
    	if (mWakeLock.isHeld()) {
    		mWakeLock.release();
    		Log.d(TAG, "Wake lock released.");
    	} else {
    		Log.d(TAG, "Wake lock not held, nothing to do.");
    	}
	}
    
	private void createWakeLock() {
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);		
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Signora." + TAG);					
	}

	private void acquireWakeLock() {
		if (!mWakeLock.isHeld()) {
			mWakeLock.acquire();		
			Log.d(TAG, "Wake lock acquired.");
		} else {
			Log.d(TAG, "Wake lock already held, nothing to do.");
		}
	}

	private void initReceiver() {
		IntentFilter filter = new IntentFilter();		
		
		filter.addAction(INTENT_CHANGE_URL);
		filter.addAction(INTENT_SCREEN_ONOFF);
		filter.addAction(INTENT_SHOW_DISCO);
		
		registerReceiver(mBrowserUrlChangeReceiver, filter);
	}

	private void initWebView() {
		mWebView = (WebView)findViewById(R.id.fullscreen_browser);

        mWebView.setFocusable(false);        
        
        WebSettings settings = mWebView.getSettings();
        
        settings.setSupportZoom(false);
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportMultipleWindows(false);
        settings.setSavePassword(false);
        settings.setSupportZoom(false);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setAppCacheEnabled(false);
        settings.setGeolocationEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(false);
        settings.setLoadsImagesAutomatically(true);
        
        mWebView.setWebViewClient(new WebViewClient() {
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		return showUrl(url, false);
        	}
        });
        
        mWebView.addJavascriptInterface(new WebViewInterface(this), "Signora");
        
        showDisconnectedPage();
	}
	
	private Boolean showUrl(String url, Boolean force) {
		if (!url.equals(mNavigatedUrl) || force) {        		
			mWebView.loadUrl(mNavigatedUrl = url);
			
			return true;
		}
		
		return false;	
	}

	private void showDisconnectedPage() {
		mWebView.loadUrl(mNavigatedUrl = "file:///android_asset/disconnected.html");
	}
}
