package com.phuchaihuynh.simplenctdownloader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

public class PlaylistActivity extends Activity {
	
	GridView playlistsView;
	List<PlaylistModel> playlistsList;
	
	Hashtable<String, HtmlParser.PlaylistTuple> playlistLinksTable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist_layout);
		
		playlistsList = getModel("", Search.SEARCH_PLAYLISTS);
		
		playlistsView = (GridView) findViewById(R.id.playlists);
		PlaylistAdapter playlistsAdapter = new PlaylistAdapter(this, playlistsList);
		playlistsView.setAdapter(playlistsAdapter);
	}
	
	private List<PlaylistModel> getModel(String searchKey, int searchMode) {
		List<PlaylistModel> list = new ArrayList<PlaylistModel>();
		Search searchPlaylist = new Search(searchKey, searchMode);
		playlistLinksTable = searchPlaylist.getPlaylists();
		Enumeration<String> keys = playlistLinksTable.keys();
		String key;
		String[] splits;
		String imageUrl, playlistUrl;
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			splits = key.split(":"); 
			imageUrl = playlistLinksTable.get(key).getImageUrl();
			playlistUrl = playlistLinksTable.get(key).getPlaylistUrl();
			list.add(new PlaylistModel(imageUrl, playlistUrl, splits[0], splits[1]));
		}
		return list;
	}
}
