package com.phuchaihuynh.simplenctdownloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadActivity extends ListActivity {

	private static final String TAG = "DownloadActivity";

	DownloadArrayAdapter adapter;
	List<DownloadModel> downloads;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Bundle extras = getIntent().getExtras();
		Bundle tb = new Bundle();
		if (extras != null) {
			tb = extras.getBundle("downloadList");
		}

		downloads = new ArrayList<DownloadModel>(); 
		DownloadModel model;
		for (String key : tb.keySet()) {
			model = new DownloadModel(key, (String)tb.get(key));
			downloads.add(model);
		}

		adapter = new DownloadArrayAdapter(DownloadActivity.this, downloads);
		setListAdapter(adapter);

		DownloadFilesTask executor = new DownloadFilesTask(); 
		for (DownloadModel d : downloads) {
			executor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, d);
		}
	}

	private class DownloadFilesTask extends AsyncTask<DownloadModel, Integer, Void> {
		DownloadModel myModel;
		private double fileLength;
		private String status;

		@Override
		protected Void doInBackground(DownloadModel... models) {
			myModel = models[0];
			int progress_status = myModel.getProcess();
			String downloadLink;
			Log.d(TAG, "Has Download Link: " + myModel.hasDownloadLink());  
			if (!myModel.hasDownloadLink()) {
				downloadLink = getDownloadLink(myModel.getUrl());
			}
			else {
				downloadLink = myModel.getUrl();
			}
			Log.d(TAG, "Download link: " + downloadLink);
			//Downloading
			if (downloadLink != null) {
				HttpURLConnection connection = null;
				String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
				File folder = new File(path, "/NCTDownloader");
				if (!folder.exists()) {
					if (folder.mkdir()) {
						System.out.printf("[Directory]: Create a folder named '%s'\n", folder.getAbsolutePath());
					}
				}
				String music = "/" + myModel.getTitle() + ".mp3";
				File musicFile = new File(folder.getAbsolutePath(), music);
				if (!musicFile.exists()) {
					try {
						musicFile.createNewFile();
						URL link = new URL(downloadLink);
						connection = (HttpURLConnection) link.openConnection();
						connection.connect();
						int fileLength = connection.getContentLength();
						this.fileLength = Math.round(fileLength/1000000.00);
						InputStream is = connection.getInputStream();
						FileOutputStream out = new FileOutputStream(musicFile);
						int nRead;
						long total = 0;
						byte[] data = new byte[16384];
						while ((nRead = is.read(data, 0, data.length)) != -1) {
							total += nRead;
							progress_status = (int) total*100/fileLength;
							System.out.println(progress_status);
							publishProgress(progress_status);
							out.write(data, 0, nRead);
						}
						out.flush();
						out.close();
						is.close();
						//System.out.printf("[%s]: Achieved the song '%s'(%s)\n", Thread.currentThread().getName(), myModel.getTitle());
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
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			status = fileLength + "MB  -  " + values[0] +"%";
			myModel.setProcess(values[0]);
			myModel.setStatus(status);
			adapter.notifyDataSetChanged();
		}

		private String getDownloadLink(String url) {
			System.out.printf("[%s]: Start getting the download link for the song '%s'\n", Thread.currentThread().getName(), myModel.getTitle());
			String xmlLink = null;
			HttpURLConnection connection = null;
			try {
				URL link = new URL(url);
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
				System.out.printf("[%s]: Achieved the xml link for the song '%s'(%s)\n", Thread.currentThread().getName(), myModel.getTitle(), xmlLink);
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
					System.out.printf("[%s]: Achieved the download link for the song '%s'(%s)\n", Thread.currentThread().getName(), myModel.getTitle(), downloadLink);
				} catch (MalformedURLException e) {
					System.out.println("[Getting download link] ---> Error while connecting to network: " + downloadLink);
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("[Getting download link] ---> Error while reading input stream");
					e.printStackTrace();
				} finally {
					if(connection != null) { connection.disconnect();}
				}
			}
			return downloadLink;
		}
	}



	private static class DownloadArrayAdapter extends ArrayAdapter<DownloadModel> {

		private final List<DownloadModel> list;
		private final Activity context;

		public DownloadArrayAdapter(Activity context, List<DownloadModel> list) {
			super(context, R.layout.download_row_layout, list);
			this.context = context;
			this.list = list;
		}

		private static class ViewHolder {
			protected TextView title;
			protected ProgressBar downloadProgress;
			protected TextView downloadStatus;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				LayoutInflater inflator = context.getLayoutInflater();
				view = inflator.inflate(R.layout.download_row_layout, null);
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.title = (TextView) view.findViewById(R.id.download_label);
				viewHolder.downloadProgress = (ProgressBar) view.findViewById(R.id.download_process_bar);
				viewHolder.downloadStatus = (TextView) view.findViewById(R.id.download_status);
				view.setTag(viewHolder);
			}
			else {
				view = convertView;
			}
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.title.setText(list.get(position).getTitle());
			holder.downloadProgress.setProgress(list.get(position).getProcess());
			holder.downloadStatus.setText(list.get(position).getStatus());
			return view;
		}
	}
}

