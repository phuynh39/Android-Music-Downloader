package com.phuchaihuynh.simplenctdownloader;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class SongListArrayAdapter extends ArrayAdapter<SongModel> {
	
	private List<SongModel> list;
	private final Activity context;
	
	public SongListArrayAdapter(Activity context, List<SongModel> list) {
		super(context, R.layout.search_row_layout, list);
		this.context = context;
		this.list = list;
	}
	
	static class ViewHolder {
		protected TextView title;
		protected TextView subTitle;
		protected CheckBox checkbox;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.search_row_layout, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) view.findViewById(R.id.label);
			viewHolder.subTitle = (TextView) view.findViewById(R.id.sub);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
			viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					SongModel element = (SongModel) viewHolder.checkbox.getTag();
					element.setSelected(buttonView.isChecked());
				}
			});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		}
		else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.title.setText(list.get(position).getSong());
		holder.subTitle.setText(list.get(position).getSinger());
		holder.checkbox.setChecked(list.get(position).isSelected());
		return view;
	}
}
