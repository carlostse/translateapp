package com.iems5722.translateapp.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import com.iems5722.translateapp.util.Util;
import android.os.AsyncTask;
import android.util.Log;

public class TcpTranslateTask extends AsyncTask<String, Void, String[]> {

	private static final String TAG = "TcpTranslateTask";

	private static final String TCP_SERVER = "iems5722v.ie.cuhk.edu.hk";
	private static final short TCP_SERVER_PORT = 3001;

	public TcpTranslateTask(TranslateAPICallback callback){
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
		Socket socket = null;
		try {
			socket = new Socket(TCP_SERVER, TCP_SERVER_PORT);
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			out.println(input);
			return new String[]{input, in.readLine()};

		} catch (IOException e) {
			Log.e(TAG, "doInBackground, IOException: ", e);
		} finally {
			Util.close(socket);
		}
		return null;
	}

	@Override
	protected void onPostExecute(String[] result) {
		Log.i(TAG, "translated: " + result);
		if (delegate != null){
			delegate.showLoading(false);
			delegate.translated(result);
		}
	}
}
