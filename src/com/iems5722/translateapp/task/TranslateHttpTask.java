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
import android.os.AsyncTask;
import android.util.Log;

public class TranslateHttpTask extends AsyncTask<String, Void, String[]> {

	private static final String
	TRANSLATE_API = "http://iems5722v.ie.cuhk.edu.hk:8080/translate.php?",
	TRANSLATE_QUERY = "word=%s";

	private static final String TAG = "HttpTranslateTask";

	public TranslateHttpTask(TranslateAPICallback callback){
		super();
		delegate = callback;
	}

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
				return new String[]{input, EntityUtils.toString(response.getEntity(), HTTP.UTF_8)};
			}

			// HTTP status not success
			Log.e(TAG, "doInBackground, status != " + HttpStatus.SC_OK + ", " + respStatus);
			response.getEntity().consumeContent();

		} catch (ClientProtocolException e) {
			Log.e(TAG, "doInBackground, ClientProtocolException: ", e);
		} catch (IOException e) {
			Log.e(TAG, "doInBackground, IOException: ", e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(String[] result) {
		Log.i(TAG, "translated: " + result[1]);
		if (delegate != null){
			delegate.showLoading(false);
			delegate.translated(result);
		}
	}
}
