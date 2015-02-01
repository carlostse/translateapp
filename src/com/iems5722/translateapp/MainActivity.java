package com.iems5722.translateapp;

import java.util.Locale;
import com.iems5722.translateapp.task.HttpTranslateTask;
import com.iems5722.translateapp.task.TcpTranslateTask;
import com.iems5722.translateapp.task.TranslateAPICallback;
import com.iems5722.translateapp.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity implements TranslateAPICallback {

	private static final String TAG = "MainActivity";

	private enum TranlateMethod {TCP, HTTP}

	private EditText txtIn;
	private TextView txtOut;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// get references to layout objects
		txtIn = (EditText) findViewById(R.id.txt_input);
		txtOut = (TextView) findViewById(R.id.txt_output);

		// add click listener to buttons to call translateText()
		((Button) findViewById(R.id.btn_tcp_submit))
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					submitButtonClicked(TranlateMethod.TCP);
				}
			});

		((Button) findViewById(R.id.btn_http_submit))
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitButtonClicked(TranlateMethod.HTTP);
			}
		});

		((Button) findViewById(R.id.btn_history))
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openTranslationRecord();
			}
		});

		// translate the text when user click done on the keyboard
		txtIn.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
					(actionId == EditorInfo.IME_ACTION_DONE)) {
					translateText(TranlateMethod.HTTP);
				}
				return false;
			}
		});
	}

	private void submitButtonClicked(TranlateMethod method){
		// hide the keyboard
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(txtIn.getWindowToken(), 0);
		// translate
		translateText(method);
	}

	/**
	 * translate look up
	 */
	private void translateText(TranlateMethod method) {
		// get user input
		String input = txtIn.getText().toString();
		Log.i(TAG, "input: " + input);

		if (Util.isMissing(input)) {
			toastMissingText();
			return;
		}

		// trim and to lower case
		input = input.trim().toLowerCase(Locale.ENGLISH);

		// try get word from API
		if (method == TranlateMethod.TCP)
			new TcpTranslateTask(this).execute(input);
		else if (method == TranlateMethod.HTTP)
			new HttpTranslateTask(this).execute(input);
	}

	private void toastMissingText(){
		Toast.makeText(this, getText(R.string.msg_without_input), Toast.LENGTH_SHORT).show();
	}

	/**
	 * Share the translated text to other applications
	 */
	private void openShare() {
		String out = txtOut.getText().toString();
		if (Util.isMissing(out)) {
			toastMissingText();
			return;
		}
		Intent i = new Intent();
		i.setAction(Intent.ACTION_SEND);
		i.putExtra(Intent.EXTRA_TEXT, out);
		i.setType("text/plain");
		startActivity(Intent.createChooser(i, getText(R.string.title_share_to)));
	}

	/**
	 * Show translation record view
	 */
	private void openTranslationRecord(){
		startActivity(new Intent(this, HistoryPageActivity.class));
	}

	/*
	 * show share icon in action bar
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * respond to action buttons
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_share:
				openShare();
				return true;
			case R.id.action_history:
				openTranslationRecord();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void showLoading(boolean show){
		if (dialog != null){
			dialog.dismiss();
			dialog = null;
		}
		if (show){
			dialog = ProgressDialog.show(this, null, getText(R.string.msg_loading));
			dialog.show();
		}
	}

	@Override
	public void translated(String[] result) {
		if (Util.isMissing(result) || result.length != 2 ||
			Util.isMissing(result[0]) || Util.isMissing(result[1])){
			Log.e(TAG, "missing result");
			new AlertDialog.Builder(this)
				.setTitle(R.string.msg_error)
				.setMessage(R.string.msg_not_in_dict)
				.setPositiveButton(R.string.btn_ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
				.create().show();
			return;
		}
		Log.d(TAG, "result: " + result[0] + " -> " + result[1]);
		// display result
		txtOut.setText(result[1]);
		// save result
		Util.saveHistory(this, result[0], result[1]);
	}
}
