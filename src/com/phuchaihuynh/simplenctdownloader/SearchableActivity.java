package com.phuchaihuynh.simplenctdownloader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class SearchableActivity extends Activity {
	
	private static final String TAG = "SearchableActivity";
	private Hashtable<String, String> songLinksTable = null;

	ListView listView;
	List<SongModel> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_results_listview);
		
		listView = (ListView) findViewById(R.id.search_results_list);

		// Get the intent, verify the action and get the query
		String query = null;
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra(SearchManager.QUERY);
			list = getModel(query, Search.SEARCH_BY_KEYWORD);
		}
		else {
			Bundle extras = intent.getExtras();
			query = extras.getString("playlist");
			if (query != null) {
				list = getModel(query, Search.SEARCH_BY_LINK);
			}
		}
		
		ArrayAdapter<SongModel> adapter = new SongListArrayAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Play the clicked song				
			}
		});

		Button download = (Button) findViewById(R.id.download_button);
		download.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle downloadBundle = new Bundle();
				for (SongModel songModel : list) {
					String song, singer, key, url;
					if (songModel.isSelected()) {
						song = songModel.getSong();
						singer = songModel.getSinger();
						key = song + " - " + singer;
						url = songLinksTable.get(key);
						downloadBundle.putString(key, url);
					}
				}
				if (!downloadBundle.isEmpty()) {
					Intent download = new Intent(v.getContext(), DownloadActivity.class);
					download.putExtra("downloadList", downloadBundle);
					startActivity(download);
				}
				else {
					Toast.makeText(getApplicationContext(), "There is no selected music", Toast.LENGTH_SHORT).show();
				}
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

	private List<SongModel> getModel(String keyword, int searchMode) {
		Search search = new Search(keyword, searchMode);
		if (songLinksTable == null) {
			songLinksTable = search.getSearchResults();
		}
		else {
		}
		Enumeration<String> keys = songLinksTable.keys();
		List<SongModel> list = new ArrayList<SongModel>();
		while (keys.hasMoreElements()) {
			String[] key = keys.nextElement().split(" - ");
			list.add(get(key[0], key[1]));
		}
		return list;
	}

	private SongModel get(String song, String singer) {
		return new SongModel(song, singer);
	}
} 

