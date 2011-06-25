package cx.ath.dekosuke.chikuwadoid;

import java.util.ArrayList;

import cx.ath.dekosuke.chikuwadoid.R.id;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;

public class Chikuwadroid extends Activity implements Runnable {
	private ProgressDialog waitDialog;
	private Thread thread;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chikuwa);
        
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
		ListView listView = (ListView) findViewById(id.cataloglistview);
		ChikuwaAdapter adapter = new ChikuwaAdapter(this, R.layout.chikuwa_row,
				streams);
		listView.setAdapter(adapter);

		FLog.d("streams size="+streams.size());
		
		waitDialog.dismiss();
		adapter.notifyDataSetChanged();
		listView.invalidateViews();

	}
	
	public void onClickReloadBtn(View v) {
		FLog.d("onclick-reload");
		setWait();
	}

	private int sortType = 0;
	public void onClickSortBtn(View v) {
		// Toast.makeText(this, "ソート選択ボタンが押されました", Toast.LENGTH_SHORT).show();
		final String[] strs = { "カタログ", "新順", "古順", "多順", "少順" };
		AlertDialog.Builder dlg;
		//final Catalog catalog = this;
		dlg = new AlertDialog.Builder(this);
		dlg.setTitle("ソート方法の選択");
		// dlg.setMessage("クリップボードにコピーするテキストを選択してください");
		dlg.setCancelable(true);
		dlg.setSingleChoiceItems(strs, sortType,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// button.setText(String.format("%sが選択されました。",items[item]));
						sortType = item; // この実装こういうものなんですかね・・・
					}
				});
		dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				FLog.d("sortType=" + sortType);
				if (sortType >= 0 && sortType < strs.length) {
					//StateMan.setSortParam(catalog, sortType);
					//onClickReloadBtn(null);
				}
			}
		});
		dlg.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		dlg.show();

	}
	
	//"onClickCategoryBtn"
}