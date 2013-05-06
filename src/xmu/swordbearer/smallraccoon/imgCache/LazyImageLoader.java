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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LazyImageLoader {
	private static final String TAG = "LazyImageLoader";
	public static LazyImageLoader instance;

	public static LazyImageLoader newInstance(Context context) {
		if (instance == null) {
			instance = new LazyImageLoader(context);
		}
		return instance;
	}

	public interface ImageLoadListener {
		void onLoaded(String url, Bitmap bitmap);
	}

	private Context mContext;
	private ImageCacheManager imgManager = null;
	private BlockingQueue<String> urlQueue = new ArrayBlockingQueue<String>(50);
	private DownloadImageThread downloadThread = new DownloadImageThread();
	private Map<String, ImageLoadListener> listeners = new HashMap<String, ImageLoadListener>();

	private static final String MSG_IMG_URL = "img_url";
	private static final String MSG_IMG_BMP = "img_bmp";
	private static final int MSG_ID = 4321;

	private ImageHandler imageHandler = new ImageHandler();

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
		Log.d(TAG, "loadBitmap " + url);
		bmp = imgManager.getFromCache(url);
		if (bmp == null) {
			startDownloadImage(url, listener);
		} else {
			listener.onLoaded(url, bmp);
		}
	}

	private void startDownloadImage(String url, ImageLoadListener listener) {
		Log.d(TAG, "startDownloadImage");
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
		private boolean isRunning = true;

		@Override
		public void run() {
			Log.d(TAG, "DownloadImageThread---run");
			while (isRunning) {
				final String url = urlQueue.poll();
				if (url == null) {
					break;
				}
				final Bitmap bmp = downloadImage(url);
				/* 图片下载完成后，发送消息刷新界面 */
				ImageLoadListener listener = listeners.get(url);
				Message msg = imageHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString(MSG_IMG_URL, url);
				bundle.putParcelable(MSG_IMG_BMP, bmp);
				msg.setData(bundle);
				imageHandler.setListener(listener);
				imageHandler.sendMessage(msg);
				listeners.remove(url);
			}
		}

		private Bitmap downloadImage(String url) {
			Log.d(TAG, "DownloadImageThread---downloadImage");
			HttpClient httpClient = new DefaultHttpClient();
			HttpGetHelper helper = new HttpGetHelper();
			InputStream is = helper.httpGetStream(httpClient, url);
			if (is == null) {
				return null;
			}
			String filePath = imgManager.saveToFile(url, is);
			Bitmap bmp = BitmapFactory.decodeFile(filePath);
			return bmp;
		}
	}

	private class ImageHandler extends Handler {
		private ImageLoadListener mListener;

		public void setListener(ImageLoadListener listener) {
			this.mListener = listener;
		}

		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String url = bundle.getString(MSG_IMG_URL);
			Bitmap bmp = bundle.getParcelable(MSG_IMG_BMP);
			if (mListener != null) {
				this.mListener.onLoaded(url, bmp);
			}
		}
	}
}
