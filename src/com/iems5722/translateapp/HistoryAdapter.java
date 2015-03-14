package com.iems5722.translateapp;

import java.util.List;
import com.iems5722.translateapp.HistoryDeleteListener.HistoryDeleteDelegate;
import com.iems5722.translateapp.util.Util;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HistoryAdapter extends ArrayAdapter<String> implements HistoryDeleteDelegate {

    private static final String TAG = "HistoryAdapter";

    public HistoryAdapter(Context context, List<String> history) {
        super(context, 0, history);
        this.context = context;
        this.history = history;
        sep = context.getText(R.string.sep_translation);
    }

    private Context context;
    private List<String> history;
    private CharSequence sep;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String line = getItem(position);
        if (!Util.isMissing(line))
            line = line.replace("\t", sep);

        // Check if an existing view is being reused, otherwise inflate the view
        TextView txt;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_translation_history, parent, false);

            Log.d(TAG, "new TextView [" + position + "]");
            txt = (TextView) convertView.findViewById(R.id.tranlation);
            txt.setOnLongClickListener(new HistoryDeleteListener(this, position));
            convertView.setTag(txt);

        } else {
            txt = (TextView) convertView.getTag();
            Log.d(TAG, "re-use TextView [" + position + "]");
        }

        Log.i(TAG, "TextView [" + position + "]: " + line);
        // has to set the text even the text view is re-used
        // otherwise, it will be incorrect if one of the row is removed
        txt.setText(line);

        return convertView;
    }

    @Override
    public void deleted(int positon) {
        Log.d(TAG, "remove: " + positon);
        history.remove(positon);

        // update history file using background thread
        new Thread(new Runnable(){
            @Override
            public void run() {
                Util.updateHistory(context, history);
            }
        }).start();

        // refresh list view
        notifyDataSetChanged();
    }
}
