package com.iems5722.translateapp.task;

import java.io.IOException;
import java.net.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import com.iems5722.translateapp.R;
import com.iems5722.translateapp.util.Util;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class TranslateAPITask extends AsyncTask<String, Void, String[]> {

    private static final String
    TRANSLATE_API = "http://aws.aboutme.com.hk:3000/translate?",
    TRANSLATE_QUERY = "word=%s";

    private static final String TAG = "HttpTranslateTask";

    public TranslateAPITask(Context context, TranslateAPICallback callback){
        super();
        this.context = context;
        delegate = callback;
    }

    private Context context;
    private TranslateAPICallback delegate;

    @Override
    protected void onPreExecute() {
        if (delegate != null) delegate.showLoading(true);
    }

    @Override
    protected String[] doInBackground(String... params) {
        String input = params[0];
        HttpClient httpclient = new DefaultHttpClient();
        try {
            String q = String.format(TRANSLATE_QUERY, URLEncoder.encode(input, HTTP.UTF_8));
            String url = new StringBuffer(TRANSLATE_API).append(q).toString();
            Log.d(TAG, "url: " + url);

            HttpResponse response = httpclient.execute(new HttpGet(url));
            int respStatus = response.getStatusLine().getStatusCode();
            if (respStatus == HttpStatus.SC_OK) {
                String json = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                if (!Util.isMissing(json)){
                    JSONObject obj = new JSONObject(json);
                    return new String[]{input, obj.getString("output"), obj.getString("message")};
                }
            }

            // HTTP status not success
            Log.e(TAG, "doInBackground, status != " + HttpStatus.SC_OK + ", " + respStatus);
            response.getEntity().consumeContent();

        } catch (ClientProtocolException e) {
            Log.e(TAG, "doInBackground, ClientProtocolException: ", e);
            return new String[]{input, null, context.getText(R.string.err_network).toString()};
        } catch (IOException e) {
            Log.e(TAG, "doInBackground, IOException: ", e);
            return new String[]{input, null, context.getText(R.string.err_network).toString()};
        } catch (JSONException e) {
            Log.e(TAG, "doInBackground, JSONException: ", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if (result != null && result.length > 2)
            Log.i(TAG, "translated: " + result[1] + ", message: " + result[2]);

        if (delegate != null){
            delegate.showLoading(false);
            delegate.translated(result);
        }
    }
}
