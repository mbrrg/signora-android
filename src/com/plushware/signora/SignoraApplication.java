package com.plushware.signora;

import com.plushware.signora.util.DeviceUuidFactory;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class SignoraApplication extends Application {
	private static String TAG = "SignoraApplication";	
	
	private static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		SignoraApplication.mContext = getApplicationContext();
	
		DeviceUuidFactory.initialize(mContext);
		setupContentCheckAlarm();		
	}
	
	private void setupContentCheckAlarm() {
		Log.d(TAG, "Setting up alarm for content check.");	
		
		AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, ContentCheckReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
	
		alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + 5000, 60 * 1000, alarmIntent);
	}
	
	public static Context getAppContext() {
		return mContext;
	}	
}
