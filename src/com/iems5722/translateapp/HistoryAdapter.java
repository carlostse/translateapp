package com.iems5722.translateapp;

import com.iems5722.translateapp.HistoryDeleteListener.HistoryDeleteDelegate;
import com.iems5722.translateapp.object.History;
import com.iems5722.translateapp.object.History.Type;
import com.iems5722.translateapp.util.Database;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HistoryAdapter extends ArrayAdapter<History> implements HistoryDeleteDelegate {

    private static final String TAG = "HistoryAdapter";

    public HistoryAdapter(Context context, Database db) {
        super(context, 0);
        this.db = db;
        largeMargin = (int)(context.getResources().getDimension(R.dimen.message_margin_l) + 0.5f);
        normalMargin = (int)(context.getResources().getDimension(R.dimen.message_margin_n) + 0.5f);
    }

    private Database db;
    private int normalMargin, largeMargin;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        History obj = getItem(position);
        int rowId = obj.getRowId();

        // Check if an existing view is being reused, otherwise inflate the view
        TextView txt;
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_message, parent, false);

            // new TextView
            txt = (TextView) convertView.findViewById(R.id.txt_message);
            convertView.setTag(txt);

        } else {
            // re-use TextView
            txt = (TextView) convertView.getTag();
        }

        String msg = obj.getText();
        Log.v(TAG, String.format(
                "TextView [%d/%d]: %s (%d)",
                position, rowId, msg, obj.getType().ordinal()));

        // has to set the text even the text view is re-used
        // otherwise, it will be incorrect if one of the row is removed
        txt.setText(msg);
        txt.setOnLongClickListener(new HistoryDeleteListener(this, position, rowId));

        // customize the alignment, background, and margin for messages
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) txt.getLayoutParams();


        if (obj.getType() == Type.Send){
            txt.setGravity(Gravity.END);
            txt.setBackgroundResource(R.drawable.bkg_msg_sent);
            params.setMarginStart(largeMargin);
            params.setMarginEnd(normalMargin);

        } else if (obj.getType() == Type.Receive){
            txt.setGravity(Gravity.START);
            txt.setBackgroundResource(R.drawable.bkg_msg_recv);
            params.setMarginStart(normalMargin);
            params.setMarginEnd(largeMargin);
        }
        txt.setLayoutParams(params);

        return convertView;
    }


    @Override
    public void deleted(int position, int rowId) {
        Log.i(TAG, "remove [" + position + "/" + rowId + "]");

        // remove from list view
        remove(getItem(position));

        // remove from database
        db.deleteHistory(rowId);
    }
}
