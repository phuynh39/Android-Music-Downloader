package com.phuchaihuynh.simplenctdownloader;

public class DownloadModel {
	private String title;
	private String url;
	private String status;
	private int process;
	private boolean hasDownloadLink = false;

	public DownloadModel(String title, String url) {
		this.title = title;
		this.url = url;
		this.process = 0;
		if (url.contains(".mp3")) {
			this.hasDownloadLink = true;
		}
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getProcess() {
		return this.process;
	}

	public void setProcess(int process) {
		this.process = process;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return this.status;
	}

	public boolean hasDownloadLink() {
		return this.hasDownloadLink;
	}
}
