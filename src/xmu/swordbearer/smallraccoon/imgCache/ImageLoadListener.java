package xmu.swordbearer.smallraccoon.imgCache;

import android.graphics.Bitmap;

public interface ImageLoadListener {
	void onLoaded(String url, Bitmap bitmap);
}
