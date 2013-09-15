package com.phuchaihuynh.simplenctdownloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {

	static class PlaylistTuple {
		String imageUrl;
		String playlistUrl;

		public PlaylistTuple(String imageUrl, String playlistUrl) {
			this.imageUrl = imageUrl;
			this.playlistUrl = playlistUrl;
		}
		
		public String getImageUrl() {
			return this.imageUrl;
		}
		
		public String getPlaylistUrl() {
			return this.playlistUrl;
		}
	}

	private final static String SONGS_REGEX = "<div class=\"song-name\">.*?" +
			"<a.*?></a>\\s*<a.*?href=\"(.*?)\"\\s*title=\"(.*?)\">.*?</a>.*?" +
			"<div class=\"singer\">.*?<a.*?>(.*?)</a>.*?</div>"; 

	private final static String SONG_HASH_STRING_REGEX = "<div class=\"player\" id=\"flashPlayer\">.*?" +
			"NCTNowPlaying.intFlashPlayer\\(.+?,.+?, \"(.*?)\",.+?,.+?\\);.*?</div>";

	private final static String DOWNLOAD_LINK_REGEX = "<location>\\s*<\\!\\[CDATA\\[(.*?)\\]\\]>\\s*</location>";

	private final static String PLAYLIST_REGEX = "<li.*?>\\s*<div class=\"img-110\".*?>.*?" +
			"<a\\s*href=\"(.*?)\"\\s*title=\"(.*?)\">\\s*<img\\s*src=\"(.*?)\".*?/></a></div>.*?"+
			"<p><a.*?>(.*?)</a></p>\\s*</li>";
	
	private final static String PLAYLIST_SONGS_REGEX = "<title>\\s*<\\!\\[CDATA\\[(.*?)\\]\\]>\\s*</title>\\s*"+
			"<creator>\\s*<\\!\\[CDATA\\[(.*?)\\]\\]>\\s*</creator>\\s*"+
			"<location>\\s*<\\!\\[CDATA\\[(.*?)\\]\\]>\\s*</location>";
	
	private final static String TOP_SONG_REGEX = "<div class=\"rank-iterm.*?\">(.*?)</div>.*?" +
			"<a.*?></a>\\s*<a.*?href=\"(.*?)\"\\s*title=\"(.*?)\">.*?</a>.*?" +
			"<div class=\"singer\">.*?<a.*?>(.*?)</a>.*?</div>";

	public static Hashtable<String, String> parseSongsResults(String html) {
		Hashtable<String, String> tb = new Hashtable<String, String>();
		Pattern pattern = Pattern.compile(SONGS_REGEX, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			String url = matcher.group(1);
			String title = matcher.group(2);
			String author = matcher.group(3);
			String key = title + " - " + author;
			tb.put(key, url);
		}
		return tb;
	}

	public static Hashtable<String, PlaylistTuple> parsePlaylist(String html) {
		Hashtable<String, PlaylistTuple> tb = new Hashtable<String, PlaylistTuple>();
		Pattern pattern = Pattern.compile(PLAYLIST_REGEX, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			String playlistUrl = matcher.group(1);
			String title = matcher.group(2);
			String imageUrl = matcher.group(3);
			String author = matcher.group(4);
			String key = title + ":" + author;
			PlaylistTuple tuple = new HtmlParser.PlaylistTuple(imageUrl, playlistUrl);
			tb.put(key, tuple);
		}
		return tb;
	}

	public static Hashtable<String, String> parseTopSongsResults(String html) {
		Hashtable<String, String> tb = new Hashtable<String, String>();
		Pattern pattern = Pattern.compile(TOP_SONG_REGEX, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			String rank = matcher.group(1);
			String url = matcher.group(2);
			String title = matcher.group(3);
			String author = matcher.group(4);
			String key = rank + ":" + title + ":" + author;
			tb.put(key, url);
		}
		return tb;
	}
	
	public static Hashtable<String, String> parseSongsInPlaylist(String html) {
		String playlistXmlLink = null;
		Hashtable<String, String> tb = new Hashtable<String, String>();
		Pattern pattern = Pattern.compile(SONG_HASH_STRING_REGEX, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
			playlistXmlLink = "http://www.nhaccuatui.com/flash/xml?key2=";
			String hashString = matcher.group(1);
			playlistXmlLink = playlistXmlLink + hashString;
		}
		HttpURLConnection connection = null;
		try {
			URL link = new URL(playlistXmlLink);
			connection = (HttpURLConnection) link.openConnection();
			connection.connect();
			InputStream is = connection.getInputStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[16384];
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			buffer.close();
			is.close();
			String xmlContent = new String(buffer.toByteArray());
			pattern = Pattern.compile(PLAYLIST_SONGS_REGEX, Pattern.DOTALL);
			matcher = pattern.matcher(xmlContent);
			while (matcher.find()) {
				String title = matcher.group(1);
				String author = matcher.group(2);
				String downloadLink = matcher.group(3);
				String key = title + " - " + author;
				tb.put(key, downloadLink);
			}
		} catch (MalformedURLException e) {
			System.out.println("[Getting download link in the playlist] ---> Error while connecting to network");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("[Getting download link in the playlist] ---> Error while reading input stream");
			e.printStackTrace();
		} finally {
			if(connection != null) { connection.disconnect();}
		}
		return tb;
	}

	public static String parseToGetTheXMLLink(String html) {
		String xmlLink = null;
		Pattern pattern = Pattern.compile(SONG_HASH_STRING_REGEX, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
			xmlLink = "http://www.nhaccuatui.com/flash/xml?key1=";
			String hashString = matcher.group(1);
			xmlLink = xmlLink + hashString;
		}
		return xmlLink;
	}

	public static String parseToGetTheDownloadLink(String xml) {
		String downloadLink = null;
		Pattern pattern = Pattern.compile(DOWNLOAD_LINK_REGEX, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(xml);
		if (matcher.find()) {
			downloadLink = matcher.group(1);
		}
		return downloadLink;
	}
}
