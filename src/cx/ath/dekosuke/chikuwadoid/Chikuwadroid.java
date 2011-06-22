package cx.ath.dekosuke.chikuwadoid;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Chikuwadroid extends Activity implements Runnable {
	private ProgressDialog waitDialog;
	private Thread thread;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setWait();
    }
    
	public void setWait() {
		if (waitDialog != null) {
			waitDialog.dismiss();
		}
		waitDialog = new ProgressDialog(this);
		waitDialog.setMessage(this.getString(R.string.loading));
		waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// waitDialog.setCancelable(true);
		waitDialog.show();

		/*
		try {
			sortType = StateMan.getSortParam(this);
		} catch (Exception e) {
			FLog.d("message", e);
		}
		*/

		thread = new Thread(this);
		thread.start();
	}
	
	public void run() {
		try { // 細かい時間を置いて、ダイアログを確実に表示させる
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// スレッドの割り込み処理を行った場合に発生、catchの実装は割愛
		}
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// HandlerクラスではActivityを継承してないため
			// 別の親クラスのメソッドにて処理を行うようにした。
			try {
				loading();

				// プログレスダイアログ終了
			} catch (Exception e) {
				FLog.d("message", e);
			}
		}
	};
	
	private void loading() {
		ChikuwaHTMLReader reader = new ChikuwaHTMLReader();
        String url = "http://www.chikuwachan.com/live/";
		String data = reader.read(url);
		ArrayList<LiveStream> streams = ChikuwaParser.parse(data);
	}
}