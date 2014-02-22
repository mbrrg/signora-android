package com.plushware.signora;

import android.content.Context;
import android.content.Intent;

public class WebViewInterface {
	Context mContext;
	
	WebViewInterface(Context context) {
		mContext = context;
	}
	
	public void showSettings() {
		mContext.startActivity(new Intent(mContext, SettingsActivity.class));
	}
	
}
