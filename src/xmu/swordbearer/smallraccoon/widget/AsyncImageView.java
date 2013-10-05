package xmu.swordbearer.smallraccoon.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;
import xmu.swordbearer.smallraccoon.util.NetUtil;

/**
 * deprecated:需要改进，加入缓存功能
 *
 * @author swordbearer
 */
public class AsyncImageView extends ImageView {
    // 防止重复下载
    private boolean isRunning = false;

    private Bitmap mBitmap;

    public AsyncImageView(Context context) {
        super(context);
    }

    public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void loadImage(String url) {
        if (!isRunning) {
            new AsyncImageLoader().execute(url);
        }
    }

    private class AsyncImageLoader extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return NetUtil.downloadImg(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            AsyncImageView.this.setImageBitmap(bitmap);
            mBitmap = bitmap;
            isRunning = false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onCancelled() {
            isRunning = false;
        }
    }
}
