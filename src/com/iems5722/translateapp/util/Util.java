package com.iems5722.translateapp.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.http.protocol.HTTP;
import com.iems5722.translateapp.R;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class Util {

    private static final String TAG = "Util";

    private static final String HISTORY_FILE = "history";

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

    public static void saveHistory(Context c, String input, String output){
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(c.openFileOutput(
                    HISTORY_FILE, Context.MODE_PRIVATE | Context.MODE_APPEND));
            out.write(new StringBuffer(input).append("\t").append(output).append("\n")
                    .toString().getBytes(HTTP.UTF_8));
        } catch (IOException e){
            Log.e(TAG, "saveHistory IOException: ", e);
        } finally {
            close(out);
        }
    }

    public static List<String> loadHistory(Context c){
        List<String> list = new ArrayList<String>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(c.openFileInput(HISTORY_FILE)));

            String line;
            while ((line = in.readLine()) != null)
                list.add(line);

        } catch (IOException e){
            Log.e(TAG, "loadHistory IOException: ", e);
        } finally {
            close(in);
        }
        return list;
    }

    public static void updateHistory(Context c, List<String> list){
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(c.openFileOutput(HISTORY_FILE, Context.MODE_PRIVATE));

            for (String s: list){
                out.write(s.getBytes(HTTP.UTF_8));
                out.write(0x0a); // \n
            }

        } catch (IOException e){
            Log.e(TAG, "updateHistory IOException: ", e);
        } finally {
            close(out);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
