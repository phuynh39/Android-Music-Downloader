package com.phuchaihuynh.simplenctdownloader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.phuchaihuynh.customview.ExpandableHeightGridView;
import com.phuchaihuynh.customview.ExpandableHeightListView;

import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.util.Log;

public class HomeActivity extends Activity {

	private static final String TAG = "MainActivity";

	ExpandableHeightGridView playlistsView;
	ExpandableHeightListView topSongsListView;
	ExpandableHeightListView newSongsListView;
	
	ImageButton morePlaylists;
	ImageButton moreTopSongs;
	ImageButton moreNewSongs;
	
	List<PlaylistModel> playlistsList;
	List<SongModel> topSongsList = new ArrayList<SongModel>();
	List<SongModel> newSongsList;
	
	Hashtable<String, String> topSongsLinksTable = null;
	Hashtable<String, String> newSongsLinksTable = null;
	Hashtable<String, HtmlParser.PlaylistTuple> playlistLinksTable = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		setModels();
		
		playlistsView = (ExpandableHeightGridView) findViewById(R.id.playlist_gridview);
		PlaylistAdapter playlistAdapter = new PlaylistAdapter(this, playlistsList);
		playlistsView.setAdapter(playlistAdapter);
		playlistsView.setExpanded(true);
		
		playlistsView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Intent playlistIntent = new Intent(v.getContext(), SearchableActivity.class);
				String url = playlistsList.get(position).getPlaylistURL();
				playlistIntent.putExtra("playlist", url);
				startActivity(playlistIntent);
			}
		});
		
		topSongsListView = (ExpandableHeightListView) findViewById(R.id.top_songs_list);
		newSongsListView = (ExpandableHeightListView) findViewById(R.id.new_songs_list);
				
		SongListArrayAdapter topSongsAdapter = new SongListArrayAdapter(this, topSongsList);
		topSongsListView.setAdapter(topSongsAdapter);
		topSongsListView.setExpand(true);
		
		SongListArrayAdapter newSongsAdapter = new SongListArrayAdapter(this, newSongsList);
		newSongsListView.setAdapter(newSongsAdapter);
		newSongsListView.setExpand(true);	
		
		morePlaylists = (ImageButton) findViewById(R.id.more_playlist);
		moreTopSongs = (ImageButton) findViewById(R.id.more_top_songs);
		moreNewSongs = (ImageButton) findViewById(R.id.more_new_songs);
		
		morePlaylists.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent playlistsIntent = new Intent(v.getContext(), PlaylistActivity.class);
				startActivity(playlistsIntent);
			}
		});
		
		moreTopSongs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		moreNewSongs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		Log.d(TAG, "Options menu start");

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
		Log.d(TAG, "Searchable Info: " + searchInfo);
		searchView.setSearchableInfo(searchInfo);
		searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
		return true;
	}
	
	private void setModels() {
		Search searchHomePage = new Search();
		playlistLinksTable = searchHomePage.getPlaylists();
		newSongsLinksTable = searchHomePage.getNewSongs();
		topSongsLinksTable = searchHomePage.getTopSongs();
		
		playlistsList = new ArrayList<PlaylistModel>();
		Enumeration<String> playlistKeys = playlistLinksTable.keys();
		while(playlistKeys.hasMoreElements()) {
			String key = playlistKeys.nextElement();
			String[] splits = key.split(":"); 
			Log.d(TAG, "Playlist: " + key);
			String imageUrl = playlistLinksTable.get(key).getImageUrl();
			String playlistUrl = playlistLinksTable.get(key).getPlaylistUrl();
			Log.d(TAG, "Playlist image link: " + imageUrl);
			Log.d(TAG, "Playlist link: " + playlistUrl);
			Log.d(TAG, "Song: " + splits[0] + ", Artist: " + splits[1]);
			playlistsList.add(new PlaylistModel(imageUrl, playlistUrl, splits[0], splits[1]));
		}
		
		newSongsList = new ArrayList<SongModel>();
		Enumeration<String> newSongsKeys = newSongsLinksTable.keys();
		while(newSongsKeys.hasMoreElements()) {
			String key = newSongsKeys.nextElement();
			String[] splits = key.split(" - "); 
			newSongsList.add(new SongModel(splits[0], splits[1]));
		}
		
		topSongsList = new ArrayList<SongModel>();
		Enumeration<String> topSongsKeys = topSongsLinksTable.keys();
		while(topSongsKeys.hasMoreElements()) {
			String key = topSongsKeys.nextElement();
			String[] splits = key.split(":"); 
			topSongsList.add(new SongModel(splits[1], splits[2]));
		}
	}
}
