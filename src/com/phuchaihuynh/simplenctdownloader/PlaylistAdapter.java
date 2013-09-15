package com.phuchaihuynh.simplenctdownloader;

import java.util.Collections;
import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaylistAdapter extends BaseAdapter {

	static class ViewHolder {
		protected ImageView playlistImage;
		protected TextView title;
		protected TextView author;
	}

	private final Context mContext;
	private List<PlaylistModel> list = Collections.emptyList();

	public PlaylistAdapter(Context context, List<PlaylistModel> list) {
		this.mContext = context;
		this.list = list;
	}

	public void update(List<PlaylistModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public PlaylistModel getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.playlist_gridview_layout, parent, false);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.playlistImage = (ImageView) convertView.findViewById(R.id.playlist_image);
			viewHolder.title = (TextView) convertView.findViewById(R.id.playlist_title);
			viewHolder.author = (TextView) convertView.findViewById(R.id.playlist_author);
			convertView.setTag(viewHolder);
		}
		else {	
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		UrlImageViewHelper.setUrlDrawable(holder.playlistImage, getItem(position).getImageURL()); 
		holder.title.setText(getItem(position).getTitle());
		holder.author.setText(getItem(position).getAuthor());
		return convertView;
	}


}
