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
	public int comenum;
	public String category;
	ArrayList<String> livetags;
	public String comuname;
	public int comsize;
	public String thumbURL;

	public String toString() {
		return "url=" + url + " title=" + title + " summary=" + summary
				+ " rank=" + rank + " min=" + min + " activenum=" + activenum
				+ " allnum=" + allnum + " comenum=" + comenum + " category="
				+ category + " comuname=" + comuname + " comsize=" + comsize;
	}
}
