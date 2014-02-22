package com.plushware.signora;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class RegisterDeviceRequest {
	public static HttpEntity getBody() throws UnsupportedEncodingException {
		JSONObject body = new JSONObject();
		
		Context context = SignoraApplication.getAppContext();
		
		try {
			body.put("os", "Android");
			body.put("model", Build.MODEL);
			body.put("version", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
			
			WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			
			Point size = new Point();
			display.getSize(size);
			
			body.put("resolution", String.format("%dx%d", size.x, size.y));			
		} catch (Exception e) {
			
		}
		
		return new StringEntity(body.toString(), "UTF-8");
	}
}
