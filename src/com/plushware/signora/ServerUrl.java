package com.plushware.signora;

import com.plushware.signora.util.DeviceUuidFactory;

public class ServerUrl {
	public static String getCommandChannelName() {	
		return String.format("signora-%s", DeviceUuidFactory.getDeviceUuid().toString());
	}
	
	public static String getRegisterDeviceUrl() {
//		return String.format("http://192.168.35.123:8000/signora/devices/%s", DeviceUuidFactory.getDeviceUuid().toString());
		return String.format("http://10.0.2.2:8080/signora/devices/%s", DeviceUuidFactory.getDeviceUuid().toString());
	}
}
