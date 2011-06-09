package cx.ath.dekosuke.chikuwadoid;

import java.io.IOException;
import java.net.URL;

public class ChikuwaHTMLReader {
	Boolean network_ok = true;
	Boolean cache_ok = true;
	String read(String url){
		String allData = "";
		cache_ok = true;
		try {
			// byte[] data = HttpClient.getByteArrayFromURL(urlStr);
			// allData = new String(data, "Shift-JIS");
			SDCard.saveFromURL(MyCrypt.createDigest(url), new URL(
					url), true); // キャッシュに保存
			allData = SDCard
					.loadTextCache(MyCrypt.createDigest(url));
			network_ok = true;
		} catch (Exception e) { // ネットワークつながってないときとか
			FLog.d("failed to get catalog html");
			network_ok = false;
			if (SDCard.cacheExist(MyCrypt.createDigest(url))) {
				FLog.d("getting html from cache");
				try {
					allData = SDCard.loadTextCache(MyCrypt
							.createDigest(url));
				} catch (IOException e1) {
					FLog.d("message", e);
				}
			} else { // キャッシュもない
				cache_ok = false;
			}
		}
		return allData;
	}
}
