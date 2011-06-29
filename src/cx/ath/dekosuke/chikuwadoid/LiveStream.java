package cx.ath.dekosuke.chikuwadoid;

import java.util.ArrayList;

public class LiveStream {
	public String url;
	public String title;
	public String summary;
	public int rank;
	public int min;
	public int activenum;
	public int allnum;
	public int commnum;
	public String category;
	ArrayList<String> livetags;
	public String commname;
	public int comsize;
	public String thumbURL;
	public String watchURL;
	public int totalPeople;
	public int activePeople;
	public int totalComment;

	public String toString() {
		return "url=" + url + " title=" + title;
	}
}
