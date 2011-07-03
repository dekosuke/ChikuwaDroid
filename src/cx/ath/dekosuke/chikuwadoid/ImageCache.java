package cx.ath.dekosuke.chikuwadoid;

import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import java.util.Iterator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

public class ImageCache {

	public static Bitmap getImage(String url) {
		String urlHash = MyCrypt.createDigest(url);
		// 本当はキーを画像名ではなくスレッド名含むURLにすべき
		try {
			if (SDCard.cacheExist(urlHash)) {
				return SDCard.loadBitmapCache(urlHash);
			}
		} catch (Exception e) {
			FLog.d("message", e);
		}
		return null;
	}

	// Bitmap→バイトデータ
	private static byte[] bmp2data(Bitmap src, Bitmap.CompressFormat format,
			int quality) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		src.compress(format, quality, os);
		return os.toByteArray();
	}

	public static boolean setImage(String url) {
		String urlHash = MyCrypt.createDigest(url);
		try {
			SDCard.saveFromURL(urlHash, new URL(url), true);
			return true;
		} catch (Exception e) {
			FLog.d("message", e);
		}
		return false;
	}

	public static boolean setImageFromBitmap(String url, Bitmap bmp) {
		String urlHash = MyCrypt.createDigest(url);
		byte[] bytes = bmp2data(bmp, Bitmap.CompressFormat.PNG, 100);
		try {
			// SDCard.saveFromURL(urlHash, new URL(url), true);
			SDCard.saveBin(urlHash, bytes, true);
			return true;
		} catch (Exception e) {
			FLog.d("message", e);
		}
		return false;
	}

	public static void GC() {
		// currently do nothing
	}
}
