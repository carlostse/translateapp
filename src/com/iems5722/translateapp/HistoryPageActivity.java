package com.iems5722.translateapp;

import java.util.List;
import com.iems5722.translateapp.task.LoadHistoryTask;
import com.iems5722.translateapp.task.LoadHistoryTask.LoadHistoryDelegate;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

public class HistoryPageActivity extends Activity implements LoadHistoryDelegate {

    private static final String TAG = "HistoryPageActivity";

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_page);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.lv_history);
        Log.d(TAG, "loading history");
        new LoadHistoryTask(this, this).execute();
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

    @Override
    public void onHistoryLoaded(List<String> list) {
        listView.setAdapter(new HistoryAdapter(this, list));
    }
}
