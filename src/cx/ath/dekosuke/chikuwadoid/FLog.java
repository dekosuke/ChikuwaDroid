package cx.ath.dekosuke.chikuwadoid;

import android.util.Log;

public class FLog {
	static final String appname = "chikuwa";
	static final boolean use_log=false;
	static public int d(String msg){
		if(use_log){
			return Log.d(appname, msg);
		}else{
			return -1;
		}
	}
	static public int d(String msg, Throwable tr){
		if(use_log){
			return Log.d(appname, msg, tr);
		}else{
			return -1;
		}
	}
	static public int e(String msg){
		if(use_log){
			return Log.e(appname, msg);
		}else{
			return -1;
		}
	}
	static public int e(String msg, Throwable tr){
		if(use_log){
			return Log.e(appname, msg, tr);
		}else{
			return -1;
		}
	}
	static public int i(String msg){
		if(use_log){
			return Log.i(appname, msg);
		}else{
			return -1;
		}
	}
	static public int i(String msg, Throwable tr){
		if(use_log){
			return Log.i(appname, msg, tr);
		}else{
			return -1;
		}
	}
	static public int w(String msg){
		if(use_log){
			return Log.w(appname, msg);
		}else{
			return -1;
		}
	}
	static public int w(String msg, Throwable tr){
		if(use_log){
			return Log.w(appname, msg, tr);
		}else{
			return -1;
		}
	}
	static public int v(String msg){
		if(use_log){
			return Log.v(appname, msg);
		}else{
			return -1;
		}
	}
	static public int v(String msg, Throwable tr){
		if(use_log){
			return Log.v(appname, msg, tr);
		}else{
			return -1;
		}
	}
}
