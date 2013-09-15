package com.phuchaihuynh.simplenctdownloader;

public class SongModel {
	private String song;
	private String singer;
	private boolean selected;
	
	public SongModel(String song, String singer) {
		this.song = song;
		this.singer = singer;
		selected = false;
	}
	
	public String getSong() {
		return this.song;
	}
	
	public String getSinger() {
		return this.singer;
	}
	
	public void setSong(String song) {
		this.song = song;
	}
	
	public void setSinger(String singer) {
		this.singer = singer;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
