package com.phuchaihuynh.simplenctdownloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;

public class DownloadThread {
	private String song;
	private String url;
	private int count;
	
	public DownloadThread(String song, String url) {
		this.song = song;
		this.url = url;
		this.count = 0;
	}
	
	public int getCount() {
		return this.count;
	}
		
	public void run() {
		System.out.printf("[%s]: Start getting the download link for the song '%s'\n", Thread.currentThread().getName(), this.song);
		String xmlLink = null;
		HttpURLConnection connection = null;
		try {
			URL link = new URL(this.url);
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
			String content = new String(buffer.toByteArray());
			xmlLink = HtmlParser.parseToGetTheXMLLink(content);
			System.out.printf("[%s]: Achieved the xml link for the song '%s'(%s)\n", Thread.currentThread().getName(), this.song, xmlLink);
		} catch (MalformedURLException e) {
			System.out.println("[Getting xml link] ---> Error while connecting to network: " + xmlLink);
	        e.printStackTrace();
	    } catch (IOException e) {
	    	System.out.println("[Getting xml link] ---> Error while reading input stream");
	        e.printStackTrace();
	    } finally {
	        if(connection != null) { connection.disconnect(); }
	    }
		
		String downloadLink = null;
		if (xmlLink != null) {
			try {
				URL link = new URL(xmlLink);
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
				downloadLink = HtmlParser.parseToGetTheDownloadLink(xmlContent);
				System.out.printf("[%s]: Achieved the download link for the song '%s'(%s)\n", Thread.currentThread().getName(), this.song, downloadLink);
			} catch (MalformedURLException e) {
				System.out.println("[Getting download link] ---> Error while connecting to network: " + downloadLink);
		        e.printStackTrace();
		    } catch (IOException e) {
		    	System.out.println("[Getting download link] ---> Error while reading input stream");
		        e.printStackTrace();
		    } finally {
		        if(connection != null) { connection.disconnect(); }
		    }
		}
		
		//Downloading
		if (downloadLink != null) {
			String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
			File folder = new File(path, "/NCTDownloader");
			if (!folder.exists()) {
				if (folder.mkdir()) {
					System.out.printf("[Directory]: Create a folder named '%s'\n", folder.getAbsolutePath());
				}
			}
			String music = "/" + song + ".mp3";
			File musicFile = new File(folder.getAbsolutePath(), music);
			if (!musicFile.exists()) {
				try {
					musicFile.createNewFile();
					URL link = new URL(downloadLink);
					connection = (HttpURLConnection) link.openConnection();
					connection.connect();
					InputStream is = connection.getInputStream();
					FileOutputStream buffer = new FileOutputStream(musicFile);
					int nRead;
					byte[] data = new byte[16384];
					while ((nRead = is.read(data, 0, data.length)) != -1) {
						buffer.write(data, 0, nRead);
						this.count++;
					}
					buffer.flush();
					buffer.close();
					is.close();
					System.out.printf("[%s]: Achieved the song '%s'(%s)\n", Thread.currentThread().getName(), this.song);
				} catch (MalformedURLException e) {
					System.out.println("[Download Music] ---> Error while connecting to network");
			        e.printStackTrace();
			    } catch (IOException e) {
			    	System.out.println("[Download Music] ---> Error while reading and writting input stream");
			        e.printStackTrace();
			    } finally {
			        if(connection != null) { connection.disconnect(); }
			    }
			}	
		}
	}
}
