package cx.ath.dekosuke.chikuwadoid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;

import java.io.File;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.BitmapFactory;

//SDカードといいつつSDカードへの保存に加えてHTTPアクセスも扱っているクラス
public class SDCard {

	public static String cacheDir = null;
	public static String saveDir = null;

	public static boolean isSDCardMounted() {
		FLog.d("mount=" + Environment.getExternalStorageState());
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static boolean setCacheDir(Context context) {
		try {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			Boolean innerCache = true; //キャッシュ内部に限定
			if (innerCache) {
				cacheDir = context.getFilesDir().getPath();
			} else { // 外部メモリ
				cacheDir = null;
			}
			FLog.d("cacheDir=" + cacheDir);
		} catch (Exception e) {
			FLog.d("message", e);
			return false;
		}
		return true;
	}

	public static boolean setSaveDir(Context context) {
		try {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			saveDir = preferences.getString("saveDir", null);
		} catch (Exception e) {
			FLog.d("message", e);
			return false;
		}
		return true;
	}

	public static String getBaseDir() {
		String sdcard_dir = Environment.getExternalStorageDirectory().getPath();

		/*
		 * if(baseDir==null || baseDir==""){ return sdcard_dir; }else{
		 * 
		 * }
		 */

		return sdcard_dir;
	}

	public static boolean isUsableDirectory(File file) {
		return file.exists() && file.isDirectory() && file.canWrite();
	}

	public static String getCacheDir() {
		String base_dir = getBaseDir() + "/.ckwcache/";
		if (cacheDir != null) {
			// ユーザ指定キャッシュディレクトリ
			base_dir = cacheDir + "/";
		}
		FLog.d("cacheDir=" + cacheDir);
		String cacheDir = base_dir;
		File file = new File(cacheDir);
		file.mkdir(); // ディレクトリないときにつくる
		if (!isUsableDirectory(file)) {
			// ディレクトリが存在しないか書き込み権限がない
			return null;
		}
		return cacheDir;
	}

	public static String getSeriarizedDir() {
		String cacheDir = getCacheDir();
		String seriarizedDir = cacheDir + "bin/";
		File file = new File(seriarizedDir);
		file.mkdir(); // ディレクトリないときにつくる
		return seriarizedDir;

	}

	public static void saveBin(String name, byte[] bytes, boolean isCache) {
		String filename;
		filename = getCacheDir() + name;
		FLog.d("length=" + bytes.length);
		File file = new File(filename);
		FLog.d(filename);
		file.getParentFile().mkdir();
		try {
			BufferedOutputStream fos = new BufferedOutputStream(
					new FileOutputStream(file));
			fos.write(bytes);
		} catch (Exception e) {
			FLog.d("failed to write file" + name);
		}

		// Environment.getDataDirectory().getPath(); // /dataなど
		// Environment.getDownloadCacheDirectory().getPath(); // cacheなど
	}

	public static void saveFromURL(String name, URL url, boolean isCache)
			throws IOException {
		try {
			String filename;
			filename = getCacheDir() + name;

			// InputStream is = url.openStream();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			if (!String.valueOf(conn.getResponseCode()).startsWith("2")) {
				throw new IOException("Incorrect response code "
						+ conn.getResponseCode());
			}

			FLog.d("HTTP Response code=" + conn.getResponseCode());
			InputStream is = conn.getInputStream();

			// OutputStream os = new FileOutputStream(filename);
			File file = new File(filename);
			file.getParentFile().mkdir();
			OutputStream fos = new FileOutputStream(filename);

			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = is.read(buf)) > 0) {
				fos.write(buf, 0, len);
			}
			fos.flush();
			is.close();
			fos.close();
		} catch (IOException e) { // 2XX代以外のレスポンスコードとか
			throw new IOException(e.toString());
		} catch (Exception e) {
			FLog.d("failed to write file" + name);
		}
	}

	public static String loadTextCache(String name) throws IOException {
		String sdcard_dir = Environment.getExternalStorageDirectory().getPath();
		String filename = getCacheDir() + name;
		File file = new File(filename);
		return FileToString.fileToString(file, "UTF-8");
	}

	public static Bitmap loadBitmapCache(String name) {
		String sdcard_dir = Environment.getExternalStorageDirectory().getPath();
		String filename = getCacheDir() + name;
		File file = new File(filename);
		// 読み込み用のオプションオブジェクトを生成
		// BitmapFactory.Options options = new BitmapFactory.Options();
		return BitmapFactory.decodeFile(filename);
	}

	public static boolean cacheExist(String name) {
		String sdcard_dir = Environment.getExternalStorageDirectory().getPath();
		String filename = getCacheDir() + name;
		File file = new File(filename);
		return file.exists();
	}

	// ファイル古いものが先にくるようにソート
	// HTMLファイルは消えにくいように時間を一日伸ばしている
	static Comparator comparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			final long additional_days = 3 * 24 * 3600 * 1000;
			File f1 = (File) o1;
			File f2 = (File) o2;
			long f1_lastmodified = f1.lastModified();
			if (MyCrypt.isHTMLName(f1.toString())) {
				f1_lastmodified += additional_days;
			}
			long f2_lastmodified = f2.lastModified();
			if (MyCrypt.isHTMLName(f2.toString())) {
				f2_lastmodified += additional_days;
			}

			return (int) (f2_lastmodified - f1_lastmodified);
		}
	};

	// numMBになるまでキャッシュフォルダのファイルを（古い順に）削除
	// http://osima.jp/blog/howto_java_lastmodified/ 古い順にファイルソート
	//
	public static void limitCache(int num) {
		File cache_dir = new File(getCacheDir());
		File[] files = cache_dir.listFiles();
		ArrayList list = new ArrayList();
		for (int i = 0; i < files.length; i++) {
			list.add(files[i]);
		}

		Collections.sort(list, comparator);

		// 順番に新しいファイルから加える─＞既定サイズになったときにそれ以降のファイルをすべて削除
		// ディレクトリはすべて削除　で。
		int sizeSum = 0;
		for (int i = 0; i < list.size(); i++) {
			File f = (File) list.get(i);
			FLog.d(f.toString() + " lastmodified=" + f.lastModified());
			// FLog.d(f.getName() + "," + toCalendarString(f));
			if (f.isDirectory()) { // 強制ディレクトリ削除
				// deleteDir(f);
				// FLog.d("deleted directory "+f.getName());
			} else {
				// FLog.d("size="+f.length());
				sizeSum += f.length();
				if (sizeSum > num * 1000000) { // 強制ファイル削除
					f.delete();
					FLog.d("deleted file " + f.getName());
				}
			}
		}
	}

	static private void deleteDir(File f) {
		if (f.exists() == false) {
			return;
		}

		if (f.isFile()) {
			f.delete();
		}

		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteDir(files[i]);
			}
			f.delete();
		}
	}

	static private String toCalendarString(File f) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(f.lastModified());

		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		return String.valueOf(y) + "-" + String.valueOf(m + 1) + "-"
				+ String.valueOf(day);
	}

	public static boolean existSeriarized(String name) {
		String filename = getCacheDir() + "bin/" + name;
		File file = new File(filename);
		return file.exists();
	}

	static public ObjectInputStream getSerialized(String name)
			throws IOException {
		String filename = getSeriarizedDir() + name;
		File file = new File(filename);
		file.getParentFile().mkdir();
		FileInputStream inFile = new FileInputStream(filename);
		ObjectInputStream inObject = new ObjectInputStream(inFile);
		return inObject;
	}

	static public void setSerialized(String name, Object object)
			throws IOException {
		String filename = getSeriarizedDir() + name;
		FileOutputStream outFile = new FileOutputStream(filename);
		File file = new File(filename);
		file.getParentFile().mkdir();
		ObjectOutputStream outObject = new ObjectOutputStream(outFile);
		outObject.writeObject(object);
	}

	// Galaxy S以外だと使えるらしいSDカードマウントチェック
	// http://sakaneya.blogspot.com/2011/02/galaxy-ssd.html
	public static boolean isMountedExSD() {
		return Environment.MEDIA_MOUNTED.equals(Environment.MEDIA_MOUNTED);
	}

}
