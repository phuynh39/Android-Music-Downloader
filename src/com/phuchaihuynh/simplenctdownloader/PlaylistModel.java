package com.phuchaihuynh.simplenctdownloader;

public class PlaylistModel {
	
	private String imageUrl;
	private String playlistUrl;
	private String title;
	private String author;
	
	public PlaylistModel(String imageUrl, String playlistUrl, String title, String author) {
		this.imageUrl = imageUrl;
		this.playlistUrl = playlistUrl;
		this.title = title;
		this.author = author;
	}

	public String getImageURL() {
		return this.imageUrl;
	}
	
	public void setImageURL(String url) {
		this.imageUrl = url;
	}
	
	public String getPlaylistURL() {
		return this.playlistUrl;
	}
	
	public void setPlaylistURL(String url) {
		this.playlistUrl = url;
	}

	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return this.author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}

}
