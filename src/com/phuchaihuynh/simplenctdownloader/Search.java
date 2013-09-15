package com.phuchaihuynh.simplenctdownloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

public class Search {
	public static final int SEARCH_HOMEPAGE = 0;
	public static final int SEARCH_BY_KEYWORD = 1;
	public static final int SEARCH_BY_LINK = 2;
	public static final int SEARCH_PLAYLISTS = 3;

	private String NCT_HOME_URL = "http://www.nhaccuatui.com";
	private String NCT_SEARCHING_URL = "http://www.nhaccuatui.com/tim-kiem?q=";
	private String NCT_PLAYLIST_URL = "http://www.nhaccuatui.com/playlist/";
	
	private String key;
	
	private Hashtable<String, String> searchResults;
	private Hashtable<String, HtmlParser.PlaylistTuple> playlistsTable;
	private Hashtable<String, String> topSongsTable;
	private Hashtable<String, String> newSongsTable;


	public Search() {
		Thread searchThread = new Thread(new Runnable() {
			public void run() {
				search(Search.SEARCH_HOMEPAGE);
			}
		});
		searchThread.start();
		try {
			searchThread.join();
		} catch (InterruptedException e) {
			System.out.println("[Searching] ---> Error while returning results from home page searching");
			e.printStackTrace();
		}
	}

	public Search(String key, final int searchMode) {
		this.key = key;
		this.searchResults = new Hashtable<String, String>();
		Thread searchThread = new Thread(new Runnable() {
			public void run() {
				search(searchMode);
			}
		});
		searchThread.start();
		try {
			searchThread.join();
		} catch (InterruptedException e) {
			System.out.println("[Searching] ---> Error while returning results");
			e.printStackTrace();
		}
	}

	public Hashtable<String, String> getSearchResults() {
		return this.searchResults;
	}

	public Hashtable<String, String> getTopSongs() {
		return this.topSongsTable;
	}

	public Hashtable<String, String> getNewSongs() {
		return this.newSongsTable;
	}

	public Hashtable<String, HtmlParser.PlaylistTuple> getPlaylists() {
		return this.playlistsTable;
	}

	private void search(int searchMode) {
		String query = "";
		if (searchMode == Search.SEARCH_HOMEPAGE) {
			query = NCT_HOME_URL;
		}
		else if (searchMode == Search.SEARCH_BY_KEYWORD) {
			String encoded_keyword = this.key.replace(" ", "+");
			query = NCT_SEARCHING_URL + encoded_keyword;
		}
		else if (searchMode == Search.SEARCH_BY_LINK) {
			query = this.key;
		}
		else if (searchMode == Search.SEARCH_PLAYLISTS) {
			query = NCT_PLAYLIST_URL + "playlist-moi.html?page=1";
		}
		System.out.println("Query url: " + query);
		HttpURLConnection connection = null;
		try {
			long startTime = System.nanoTime();
			URL url = new URL(query);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			InputStream is = connection.getInputStream();
			// Get the content of the input stream
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[16384];
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			String content = new String(buffer.toByteArray());
			double runTime = (double) (System.nanoTime()-startTime)/1000000000.0;
			System.out.printf("Runnning time for getting the search page: %fs\n", runTime);
			// Parsing the content of the search page
			if (searchMode == Search.SEARCH_HOMEPAGE) {
				this.playlistsTable = HtmlParser.parsePlaylist(content);
				this.newSongsTable = HtmlParser.parseSongsResults(content);
				this.topSongsTable = HtmlParser.parseTopSongsResults(content);
			}
			else if (searchMode == Search.SEARCH_BY_KEYWORD) {
				this.searchResults = HtmlParser.parseSongsResults(content);
			}
			else if (searchMode == Search.SEARCH_BY_LINK) {
				this.searchResults = HtmlParser.parseSongsInPlaylist(content);
			}
			else if (searchMode == Search.SEARCH_PLAYLISTS) {
				this.playlistsTable = HtmlParser.parsePlaylist(content);
			}
		} catch (MalformedURLException e) {
			System.out.println("[Searching] ---> Error while connecting to the network");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[Searching] ---> Error while reading input stream");
			e.printStackTrace();
		} finally {
			if(connection != null) { connection.disconnect(); }
		}
	}
}
