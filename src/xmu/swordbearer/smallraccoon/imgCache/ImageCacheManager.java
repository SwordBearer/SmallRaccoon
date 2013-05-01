package xmu.swordbearer.smallraccoon.imgCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import xmu.swordbearer.smallraccoon.util.MD5Util;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageCacheManager {
	private static final String TAG = "ImageCacheManager";
	private Map<String, SoftReference<Bitmap>> imgMap;
	private Context mContext;

	public ImageCacheManager(Context context) {
		this.mContext = context;
		imgMap = new HashMap<String, SoftReference<Bitmap>>();
	}

	public Bitmap getFromCache(String url) {
		Log.e(TAG, "getFromCache");
		Bitmap bmp = null;
		if (imgMap.containsKey(url)) {
			bmp = getFromMapCache(url);
		}
		if (null == bmp) {
			bmp = getFromFileCache(url);
			if (null != bmp) {
				imgMap.put(url, new SoftReference<Bitmap>(bmp));
			}
		}
		return bmp;
	}

	/**
	 * 从内存中获取Bitmap
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getFromMapCache(String path) {
		Log.e(TAG, "getFromMapCache");
		Bitmap bmp = null;
		SoftReference<Bitmap> ref = null;
		synchronized (this) {
			ref = imgMap.get(path);
		}
		if (null != ref) {
			bmp = ref.get();
		}
		return bmp;

	}

	private Bitmap getFromFileCache(String url) {
		Bitmap bmp = null;
		String fileName = this.MD5Encode(url);
		Log.e(TAG, "getFromFileCache  " + fileName);
		FileInputStream fis = null;
		try {
			fis = mContext.openFileInput(fileName);
			bmp = BitmapFactory.decodeStream(fis);
			return bmp;
		} catch (FileNotFoundException e) {
			return null;
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public String writeToFile(String url, InputStream inputStream) {
		String fileName = this.MD5Encode(url);// 加密后的文件名
		Log.e(TAG, "writeToFile  " + fileName);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(inputStream);
			Log.e(TAG, "writeToFile inputStreamSize " + inputStream.available());
			bos = new BufferedOutputStream(mContext.openFileOutput(fileName,
					Context.MODE_PRIVATE));
			byte[] buffer = new byte[1024];
			int length;
			while ((length = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != bos) {
					bos.flush();
					bos.close();
				}
			} catch (IOException e) {
			}
		}
		return mContext.getFilesDir() + File.separator + fileName;
	}

	private String MD5Encode(String src) {
		return MD5Util.MD5Encode(src);
	}

}
