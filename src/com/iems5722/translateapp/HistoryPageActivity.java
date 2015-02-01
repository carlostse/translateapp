package com.iems5722.translateapp;

import com.iems5722.translateapp.util.Util;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

public class HistoryPageActivity extends Activity {

	private static final String TAG = "TranslationRecordActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_page);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		ListView listView = (ListView) findViewById(R.id.lv_history);
		listView.setAdapter(new TranslationHistoryAdapter(this, Util.loadHistory(this)));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
