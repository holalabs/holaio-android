package com.holalabs.holaio;

import java.util.HashMap;
import java.util.Map;

public final class HolaIOSingleton {

	private static HolaIOSingleton instance = null;
	private Map<String,String> reqCache = new HashMap<String,String>();

	private HolaIOSingleton() {}
	
	public static synchronized HolaIOSingleton getInstance() {
		if (instance == null)
            instance = new HolaIOSingleton();
		return instance;
	}
	
	public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
      }
	
	public String getCachedValue(String key) {
		return reqCache.get(key);
	}
	
	public void setCachedValue(String key, String value) {
		reqCache.put(key, value);
	}
	
}
