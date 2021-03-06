package xmu.dayandnight.smallraccoon.cache.imgCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.State;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import xmu.dayandnight.smallraccoon.http.HttpHandler;

/**
 * 网络图片缓存工具类
 */
public class LazyImageLoader {
    private static final String TAG = "LazyImageLoader";
    public static LazyImageLoader instance;

    public static LazyImageLoader newInstance(Context context) {
        if (instance == null) {
            instance = new LazyImageLoader(context);
        }
        return instance;
    }

    /**
     * 加载图片的回调接口
     * 调用 loadBitmap加载网络图片时，需要自行实现此接口，完成回调
     */
    public interface ImageLoadListener {
        void onLoaded(String url, Bitmap bitmap);

        void onError(String url, String info);
    }

    private Context mContext;
    private ImageCacheManager imgManager = null;
    private BlockingQueue<String> urlQueue = new ArrayBlockingQueue<String>(40);
    private DownloadImageThread downloadThread = new DownloadImageThread();
    private Map<String, ImageLoadListener> listeners = new HashMap<String, ImageLoadListener>();

    private static final String MSG_IMG_URL = "img_url";
    private static final String MSG_IMG_BMP = "img_bmp";

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

    /**
     * 加载图片
     *
     * @param url      图片的url
     * @param listener 回调
     */
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
            try {
                InputStream is = HttpHandler.httpGetStream(url, null);
                if (is != null) {
                    String filePath = imgManager.saveToFile(url, is);
                    return BitmapFactory.decodeFile(filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return null;
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
