package com.iems5722.translateapp.task;

import java.util.List;
import com.iems5722.translateapp.object.History;
import com.iems5722.translateapp.util.Database;
import android.os.AsyncTask;
import android.util.Log;

public class LoadHistoryTask extends AsyncTask<Void, Void, List<History>> {

    private static final String TAG = "LoadHistoryTask";

    public interface LoadHistoryDelegate {
        public void onHistoryLoaded(List<History> list);
    }

    public LoadHistoryTask(Database db, LoadHistoryDelegate delegate) {
        super();
        this.delegate = delegate;
        this.db = db;
    }

    private LoadHistoryDelegate delegate;
    private Database db;

    @Override
    protected List<History> doInBackground(Void... params) {
        List<History> list = History.getAll(db);
        Log.i(TAG, "history list size: " + (list == null? 0: list.size()));
        return list;
    }

    @Override
    protected void onPostExecute(List<History> result) {
        delegate.onHistoryLoaded(result);
    }
}
