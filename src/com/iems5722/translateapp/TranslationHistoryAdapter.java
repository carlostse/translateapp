package com.iems5722.translateapp;

import java.util.ArrayList;
import com.iems5722.translateapp.util.Util;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TranslationHistoryAdapter extends ArrayAdapter<String>{

	public TranslationHistoryAdapter(Context context, ArrayList<String> history) {
		super(context, 0, history);
		sep = context.getText(R.string.sep_translation);
	}

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

			txt = (TextView) convertView.findViewById(R.id.tranlation);
			txt.setText(line);
			txt.setOnLongClickListener(new OnLongClickListener(){
				@Override
				public boolean onLongClick(View v) {

					return false;
				}
			});
			convertView.setTag(position);

		} else {
			txt = (TextView) convertView.getTag();
		}

		return convertView;
	}
}
