package com.iems5722.translateapp;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TranslationHistoryAdapter extends ArrayAdapter<String>{

	public TranslationHistoryAdapter(Context context, ArrayList<String> history) {
		super(context, 0, history);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String line = getItem(position);

		// Check if an existing view is being reused, otherwise inflate the view
		TextView txt;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.item_translation_history, parent, false);

			txt = (TextView) convertView.findViewById(R.id.tranlation);
			convertView.setTag(txt);

		} else {
			txt = (TextView) convertView.getTag();
		}

		// Populate the data into the template view using the data object
		txt.setText(line);

		return convertView;
	}
}
