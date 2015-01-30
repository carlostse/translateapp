package com.iems5722.translateapp;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import android.util.Log;

public class Util {

	private static final String TAG = "Util";

	public static boolean isMissing(Object obj){
		if (obj == null) return true;
		if (obj instanceof String) return ((String) obj).trim().length() < 1;
		if (obj instanceof String[]) return ((String[]) obj).length < 1 || isMissing(((String[])obj)[0]);
		if (obj instanceof Object[]) return ((Object[]) obj).length < 1;
		if (obj instanceof List) return ((List<?>) obj).size() < 1;
		if (obj instanceof Map) return ((Map<?, ?>) obj).size() < 1;
		if (obj instanceof File) return !((File)obj).exists();
		return false;
	}

	public static void close(Closeable c){
		if (c != null){
			try {
				c.close();
			} catch (IOException e){
				Log.e(TAG, "close IOException: ", e);
			} finally {
				c = null;
			}
		}
	}

	public static void close(AutoCloseable c){
		if (c != null){
			try {
				c.close();
			} catch (Exception e){
				Log.e(TAG, "close Exception: ", e);
			} finally {
				c = null;
			}
		}
	}
}
