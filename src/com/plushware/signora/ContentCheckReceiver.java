package com.plushware.signora;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ContentCheckReceiver extends BroadcastReceiver {
	public static final String TAG = "ContentCheckReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		alarmOn(context);
	}

	private void alarmOn(Context context) {
		Log.d(TAG, "Time to check for new content, launching service.");

		context.startService(new Intent(context, ContentCheckService.class));
	}
}