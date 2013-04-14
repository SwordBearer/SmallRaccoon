package xmu.swordbearer.smallraccoon.imgCache;

import java.io.InputStream;
import java.lang.Thread.State;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import xmu.swordbearer.smallraccoon.http.HttpGetHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class LazyImageLoader {

	public static LazyImageLoader instance;

	public static LazyImageLoader newInstance(Context context) {
		if (instance == null) {
			instance = new LazyImageLoader(context);
		}
		return instance;
	}

	private Context mContext;
	private ImageCacheManager imgManager = null;
	private BlockingQueue<String> urlQueue = new ArrayBlockingQueue<String>(50);
	private DownloadImageThread downloadThread = new DownloadImageThread();
	private Map<String, ImageLoadListener> listeners = new HashMap<String, ImageLoadListener>();

	private LazyImageLoader(Context context) {
		this.mContext = context;
		imgManager = new ImageCacheManager(mContext);
	}

	private void putUrlToQueue(String url) {
		if (!urlQueue.contains(url)) {
			try {
				urlQueue.put(url);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadBitmap(String url, ImageLoadListener listener) {
		Bitmap bmp = null;
		bmp = imgManager.getFromCache(url);
		if (bmp != null) {
			listener.onLoaded(url, bmp);
		} else {
			startDownloadImage(url, listener);
		}
	}

	private void startDownloadImage(String url, ImageLoadListener listener) {
		putUrlToQueue(url);
		listeners.put(url, listener);
		State state = downloadThread.getState();
		if (state == State.NEW) {
			downloadThread.start();
		} else if (state == State.TERMINATED) {
			downloadThread = new DownloadImageThread();
			downloadThread.start();
		}
	}

	private class DownloadImageThread extends Thread {
		private boolean isRunning = false;

		@Override
		public void run() {
			while (isRunning) {
				String url = urlQueue.poll();
				if (url == null) {
					break;
				}
				Bitmap bmp = downloadImage(url);
				ImageLoadListener listener = listeners.get(url);
				// 回调
				listener.onLoaded(url, bmp);
				listeners.remove(url);
			}
			isRunning = false;
		}

		private Bitmap downloadImage(String url) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGetHelper helper = new HttpGetHelper();
			InputStream is = helper.httpGetStream(httpClient, url);
			if (is == null) {
				return null;
			}
			imgManager.writeToFile(url, is);
			return BitmapFactory.decodeStream(is);
		}
	}
}
