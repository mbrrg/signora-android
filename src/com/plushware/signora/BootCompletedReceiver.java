package com.plushware.signora;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	    Intent activityIntent = new Intent(context, BrowserActivity.class);
	    activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    context.startActivity(activityIntent);
	}
}
