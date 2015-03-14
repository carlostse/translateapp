package com.iems5722.translateapp.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import com.iems5722.translateapp.R;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class Util {

    private static final String TAG = "Util";

    public static boolean isMissing(Object obj){
        if (obj == null) return true;
        if (obj instanceof String) return ((String) obj).trim().length() < 1;
        if (obj instanceof String[]) return ((String[]) obj).length < 1 || isMissing(((String[])obj)[0]);
        if (obj instanceof Object[]) return ((Object[]) obj).length < 1;
        if (obj instanceof Collection) return ((Collection<?>) obj).size() < 1;
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

    /**
     * Socket does not implement <code>Closeable</code> until API 19.<br>
     * Therefore, we need this specific function.
     * @param c
     */
    public static void close(Socket c){
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

    public static boolean isTranslationError(Context context, String result){
        return isMissing(result) || result.trim().equals(context.getString(R.string.err_translate));
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
