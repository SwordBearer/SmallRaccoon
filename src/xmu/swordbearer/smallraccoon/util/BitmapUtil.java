package xmu.swordbearer.smallraccoon.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;

public class BitmapUtil {
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 10;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 获取图片缩略图 最大宽度为72
	 * 
	 * @param originalImage
	 * @return
	 */
	public static Bitmap getImageThumbnail(Bitmap originalImage) {
		int w = originalImage.getWidth();
		int h = originalImage.getHeight();
		double time = w / 72;// 缩放倍数
		int ww = (int) (w / time);
		int hh = (int) (h / time);
		return Bitmap.createBitmap(originalImage, 0, 0, w, h);
	}

	@SuppressLint("NewApi")
	public static Bitmap getImageThumbnail(String imagePath) {
		// 获取缩略图
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		return ThumbnailUtils.extractThumbnail(bitmap, width, height);
	}
}
