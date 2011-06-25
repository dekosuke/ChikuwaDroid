package cx.ath.dekosuke.chikuwadoid;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*

 <tr class="main_tr comm_co1113065">
 <td class="rate" rowspan="2">2
 <span class="banimg" style="display:none;"><a href="javascript:void(0);" 
 onclick="AddBancomms('co1113065','co1113065');"><img src="http://www2.chikuwachan.com/img/cross.png" alt="×"></a></span></td>

 <td class="thumbnail" rowspan="2">
 <a href="http://com.nicovideo.jp/community/co1113065" target="_blank">
 <img alt="co1113065" src="http://icon.nimg.jp/community/s/co1113065.jpg?130764"></a></td>
 <td class="table_main" rowspan="2"><div class="table_main2">
 <div class="title">

 <img alt="顔出し" src="http://www2.chikuwachan.com/img/kao.gif"> 
 <a href="http://live.nicovideo.jp/watch/lv52817873" target="_blank">夜更かし出来る人！カモーンe&</a>
 </div>
 <div class="username"><a target="_blank" href="http://www.nicovideo.jp/user/21042365">a&ぽりんa&</a></div>
 <span class="date limit nicopo">60<span style="font-size:7pt;">分</span></span>
 <div class="content">*.☆ﾟ･*:.｡:*･゜☆ﾟ･*:..｡:*･゜☆ﾟ･*:..｡:*･゜☆.*皆さんいらっ</div>
 <div class="tags">
 <span class="c1">一般</span>
 <span class="tag">
 <a onclick="RankingOutside('','%E9%9B%91%E8%AB%87-%E5%A5%B3%E6%80%A7','','','','1','');
 " href="javascript:void(0);">雑談-女性</a></span><span class="tag">
 <a onclick="RankingOutside('','%E9%A1%94%E5%87%BA%E3%81%97','','','','1','');
 " href="javascript:void(0);">顔出し</a></span>
 </div>
 <span class="community"><a href="http://com.nicovideo.jp/community/co1113065" target="_blank">ぽりん！</a></span>
 <span onclick="admwin('co1113065');" class="community2">3484</span>
 </div></td>
 <td style="background-color:#FFFFFF;" class="active" colspan="2">
 <span style="color:#FF0000;">87</span>
 </td>
 </tr>

 */

public class ChikuwaParser {
	// パーザ
	static ArrayList<LiveStream> parse(String html) {
		// 番組情報全体
		Pattern streamPattern = Pattern
				.compile("<tr class=\"main_tr comm_co([^>]+)\">.+?</tr>",
						Pattern.DOTALL);
		Pattern titlePattern = Pattern.compile(
				"<a [^>]*?href=\"http://live.nicovideo.jp/watch/lv([0-9]+)\"[^>]*>"
						+ "([^<]+?)</a>", Pattern.DOTALL);
		// <img alt="co1113065"
		// src="http://icon.nimg.jp/community/s/co1113065.jpg?130764"></a></td>
		Pattern thumbPattern = Pattern.compile(
				"<img [^>]*src=\"(http://icon.nimg.jp.+?)\"", Pattern.DOTALL);
		Pattern watchLinkPattern = Pattern.compile(
				"<a[^>]*href=\"(http://live.nicovideo.jp/watch/(?:lv|co)[0-9]+)\"", Pattern.DOTALL);
		//<a target="_blank" href="http://com.nicovideo.jp/community/co1011582">じゅん☆じゅん　再出発</a>
		Pattern commLinkPattern = Pattern.compile(
				"<a[^>]*href=\"http://com.nicovideo.jp/community/co([0-9]+)\">([^<]+)</a>", Pattern.DOTALL);

		
		
		ArrayList<LiveStream> streams = new ArrayList<LiveStream>();

		Matcher mcStream = streamPattern.matcher(html);
		while (mcStream.find()) { // それぞれのStreamごとに
			String elem = mcStream.group(0);
			//FLog.d("elem=" + elem);
			LiveStream liveStream = new LiveStream();
			//liveStream.commnum = Integer.parseInt(mcStream.group(1));
			Matcher titleMc = titlePattern.matcher(elem);
			if (titleMc.find()) {
				liveStream.title = titleMc.group(2);
			}
			Matcher thumbMc = thumbPattern.matcher(elem);
			if (thumbMc.find()) {
				liveStream.thumbURL = thumbMc.group(1);
			}
			Matcher watchMc = watchLinkPattern.matcher(elem);
			if (watchMc.find()) {
				liveStream.watchURL = watchMc.group(1);
			}
			Matcher commMc = commLinkPattern.matcher(elem);
			if (commMc.find()) {
				liveStream.commnum = Integer.parseInt(commMc.group(1));
				liveStream.commname = commMc.group(2);
			}
			//FLog.d("watchURL="+liveStream.watchURL);
			
			if (liveStream.title != null) {
				streams.add(liveStream);
			}
		}

		return streams;
	}
}
