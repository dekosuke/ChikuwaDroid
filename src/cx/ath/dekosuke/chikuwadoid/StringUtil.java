package cx.ath.dekosuke.chikuwadoid;


import java.net.URLEncoder;
import java.util.ArrayList;

import android.webkit.MimeTypeMap;

public class StringUtil {
	static String safeCut(String str, int length) {
		if (str.length() > length) {
			return str.substring(0, length) + "...";
		}
		return str;
	}

	static String safeCutNoDot(String str, int length) {
		if (str.length() > length) {
			return str.substring(0, length);
		}
		return str;
	}

	// これ汎用じゃない・・
	static String[] nonBlankSplit(String str, String[] addition) {
		String[] elems = str.split("\n");
		ArrayList<String> filtered_elems = new ArrayList<String>();
		for (int i = 0; i < elems.length; ++i) {
			// if(elems[i])
			if (elems[i].length() > 0) {
				filtered_elems.add(elems[i]);
			}
		}
		if (elems.length > 1) {
			for (int i = 0; i < addition.length; ++i) {
				filtered_elems.add(addition[i]);
			}
		}
		// FLog.d("length="+filtered_elems.size());
		return (String[]) filtered_elems.toArray(new String[0]);
	}
	
	static String quote(String str){
		String ret="";
		String[] elems = str.split("\n");
		ArrayList<String> filtered_elems = new ArrayList<String>();
		for (int i = 0; i < elems.length; ++i) {
			// if(elems[i])
			if (elems[i].length() > 0) {
				ret+=">"+elems[i].trim()+"\n";
			}
		}
		return ret;
	}

	// 検索クエリを正規化
	// TODO:大文字小文字、全角半角、ひらがなかたかなの標準化
	static String[] queryNormalize(String str) {
		String temp = normalize(str);
		String[] splits = temp.split("[ 　]");
		ArrayList<String> splits_tmp = new ArrayList<String>();
		for (int i = 0; i < splits.length; ++i) {
			if (splits[i].length() > 0) {
				//自前HTMLエスケープ・・・
				String elem = splits[i].replaceAll("<", "&lt;");
				elem = elem.replaceAll(">", "&gt;");
				elem = elem.replaceAll("\"", "&quot;");
				splits_tmp.add(elem);
			}
		}
		return (String[]) splits_tmp.toArray(new String[0]);
	}
	
	//queryNormalizeと違って正規化しない
	static String[] querySplit(String str) {
		String[] splits = str.split("[ 　]");
		ArrayList<String> splits_tmp = new ArrayList<String>();
		for (int i = 0; i < splits.length; ++i) {
			if (splits[i].length() > 0) {
				//自前HTMLエスケープ・・・
				String elem = splits[i].replaceAll("<", "&lt;");
				elem = elem.replaceAll(">", "&gt;");
				elem = elem.replaceAll("\"", "&quot;");
				splits_tmp.add(elem);
			}
		}
		return (String[]) splits_tmp.toArray(new String[0]);
	}


	static boolean isQueryMatch(String str, String[] query) {
		String temp = normalize(str);
		for (int i = 0; i < query.length; ++i) {
			if (!temp.contains(query[i])) {
				return false;
			}
		}
		return true;
	}

	static boolean isQueryMatchOr(String str, String[] query, boolean doNormalize) {
		String temp = str;
		if(doNormalize){
			temp = normalize(temp);
		}
		for (int i = 0; i < query.length; ++i) {
			if (temp.contains(query[i])) {
				return true;
			}
		}
		return false;
	}
	
	// http://ameblo.jp/archive-redo-blog/entry-10376390355.html
	private static String zenkakuToHankaku(String value) {
		StringBuilder sb = new StringBuilder(value);
		for (int i = 0; i < sb.length(); i++) {
			int c = (int) sb.charAt(i);
			if ((c >= 0xFF10 && c <= 0xFF19) || (c >= 0xFF21 && c <= 0xFF3A)
					|| (c >= 0xFF41 && c <= 0xFF5A)) {
				sb.setCharAt(i, (char) (c - 0xFEE0));
			}
		}
		value = sb.toString();
		return value;
	}

	// http://www7a.biglobe.ne.jp/~java-master/samples/string/ZenkakuKatakanaToZenkakuHiragana.html
	public static String zenkakuHiraganaToZenkakuKatakana(String s) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c >= 'ァ' && c <= 'ン') {
				sb.setCharAt(i, (char) (c - 'ァ' + 'ぁ'));
			} else if (c == 'ヵ') {
				sb.setCharAt(i, 'か');
			} else if (c == 'ヶ') {
				sb.setCharAt(i, 'け');
			} else if (c == 'ヴ') {
				sb.setCharAt(i, 'う');
				sb.insert(i + 1, '゛');
				i++;
			}
		}
		return sb.toString();
	}

	// 正規化
	static String normalize(String str) {
		// 大文字ー＞小文字
		String temp = str.toLowerCase();
		// 全角ー＞半角
		temp = zenkakuToHankaku(temp);
		// かたかなー＞ひらがな
		temp = zenkakuHiraganaToZenkakuKatakana(temp);
		return temp;
	}

}
