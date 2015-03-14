package com.iems5722.translateapp.task;

import java.util.List;
import com.iems5722.translateapp.util.Util;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class LoadHistoryTask extends AsyncTask<Void, Void, List<String>> {

    private static final String TAG = "LoadHistoryTask";

    public interface LoadHistoryDelegate {
        public void onHistoryLoaded(List<String> list);
    }

    public LoadHistoryTask(Context context, LoadHistoryDelegate delegate) {
        super();
        this.context = context;
        this.delegate = delegate;
    }

    private Context context;
    private LoadHistoryDelegate delegate;

    @Override
    protected List<String> doInBackground(Void... params) {
        List<String> list = Util.loadHistory(context);
        Log.i(TAG, "history list size: " + (list == null? 0: list.size()));
        return list;
    }

    @Override
    protected void onPostExecute(List<String> result) {
        delegate.onHistoryLoaded(result);
    }
}
