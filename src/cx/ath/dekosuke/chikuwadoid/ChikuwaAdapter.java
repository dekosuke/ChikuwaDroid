package cx.ath.dekosuke.chikuwadoid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.text.Html;
import android.util.Log;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.ImageView;

//画面サイズ取得のため
import android.view.WindowManager;
import android.content.Context;
import android.view.Display;

public class ChikuwaAdapter extends ArrayAdapter {

	public ArrayList<LiveStream> items;
	private LayoutInflater inflater;
	private Context context;
	private TreeSet<Integer> checkedSet = new TreeSet<Integer>();

	// 画面サイズ
	private int width;
	private int height;

	public ChikuwaAdapter(Context context, int textViewResourceId,
			ArrayList items) {
		super(context, textViewResourceId, items);
		this.items = (ArrayList<LiveStream>) items;
		this.context = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ビューを受け取る
		View view = convertView;

		if (view == null) {
			// 受け取ったビューがnullなら新しくビューを生成
			view = inflater.inflate(R.layout.chikuwa_row, null);
			// 背景画像をセットする
			// view.setBackgroundResource(R.drawable.back);

		}

		try {
			// 表示すべきデータの取得
			final LiveStream item = (LiveStream) items.get(position);
			TextView text = (TextView) view.findViewById(R.id.bottomtext);
			TextView topText = (TextView) view.findViewById(R.id.bbsname);
			ImageView iv = (ImageView) view.findViewById(R.id.image);

			text.setText(item.title);
			topText.setText("co"+item.commnum+":"+item.commname);

			// 空画像挿入
			Bitmap bm = Bitmap.createBitmap(64, 64, Bitmap.Config.ALPHA_8);
			iv.setImageBitmap(bm);
			iv.setVisibility(View.VISIBLE);

			FLog.d("thumburl=" + item.thumbURL);
			// 画像をセット
			try {
				if (item.thumbURL != null) {
					iv.setTag(item.thumbURL);
					ImageGetTask task = new ImageGetTask(iv);
					task.execute(item.thumbURL);
				}
			} catch (Exception e) {
				FLog.d("message", e);
			}

			try {
				if (item.watchURL != null) {
					view.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							if (1 == 1) {
								Intent intent = new Intent(Intent.ACTION_VIEW,
										Uri.parse(item.watchURL));
								getContext().startActivity(intent);
							} else {
								Intent intent = new Intent(Intent.ACTION_SEND);
								intent.setType("text/plain");
								intent.putExtra(Intent.EXTRA_TEXT,
										item.watchURL);

							}
						}
					});
				}
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(getContext(), "ブラウザが見つかりませんでした",
						Toast.LENGTH_SHORT).show();
			}

			/*
			 * final String threadNum = "" + item.threadNum; TextView text =
			 * (TextView) view.findViewById(R.id.bottomtext); CheckBox checkbox
			 * = (CheckBox) view.findViewById(R.id.checkbox); TextView
			 * nonclickableblank = (TextView) view
			 * .findViewById(id.nonclickableblank); TextView resNum = (TextView)
			 * view.findViewById(R.id.resnum); TextView BBSName = (TextView)
			 * view.findViewById(R.id.bbsname); ImageView iv = (ImageView)
			 * view.findViewById(R.id.image); Bitmap bm =
			 * Bitmap.createBitmap(50, 50, Bitmap.Config.ALPHA_8);
			 * iv.setImageBitmap(bm); iv.setVisibility(View.VISIBLE); //
			 * メニュー用区切りのとき if (FutabaThreadContent.isMenu1(item)) {
			 * text.setTextSize(StateMan.getDescFontSize(getContext()));
			 * text.setTextColor(Color.parseColor("#FFFFFF"));
			 * text.setText("キーワードを含むスレッド↓");
			 * view.setBackgroundColor(Color.parseColor("#996666"));
			 * checkbox.setVisibility(View.GONE);
			 * nonclickableblank.setVisibility(View.GONE);
			 * resNum.setVisibility(View.GONE);
			 * BBSName.setVisibility(View.GONE); iv.setVisibility(View.GONE);
			 * view.setOnClickListener(new View.OnClickListener() { public void
			 * onClick(View v) { } }); return view; } else if
			 * (FutabaThreadContent.isMenu2(item)) {
			 * text.setTextSize(StateMan.getDescFontSize(getContext()));
			 * text.setTextColor(Color.parseColor("#FFFFFF"));
			 * text.setText("その他スレッド↓");
			 * view.setBackgroundColor(Color.parseColor("#996666"));
			 * checkbox.setVisibility(View.GONE);
			 * nonclickableblank.setVisibility(View.GONE);
			 * resNum.setVisibility(View.GONE);
			 * BBSName.setVisibility(View.GONE); iv.setVisibility(View.GONE);
			 * view.setOnClickListener(new View.OnClickListener() { public void
			 * onClick(View v) { } }); return view; }
			 * 
			 * text.setTextColor(Color.parseColor("#800000"));
			 * resNum.setVisibility(View.VISIBLE);
			 * 
			 * // カタログからスレッドをクリックしたときのリスナー if (true) {
			 * view.setOnClickListener(new View.OnClickListener() { public void
			 * onClick(View v) { FLog.d("intent calling thread activity");
			 * Intent intent = new Intent(); Catalog activity = (Catalog)
			 * getContext(); try { FutabaThreadContent thread = item; if
			 * (!activity.mode.equals("history")) { // 通常 thread.baseUrl =
			 * activity.baseUrl; } Calendar calendar = Calendar.getInstance();
			 * thread.lastAccessed = calendar.getTimeInMillis(); HistoryManager
			 * man = new HistoryManager(); man.Load(); int maxHistoryNum = 20;
			 * try { SharedPreferences preferences = PreferenceManager
			 * .getDefaultSharedPreferences(activity); maxHistoryNum =
			 * Integer.parseInt(preferences.getString(
			 * activity.getString(R.string.historynum), "20")); } catch
			 * (Exception e) { FLog.d("message", e); } FLog.d("maxhistorynum=" +
			 * maxHistoryNum); FLog.d("add thread " + thread.toString());
			 * 
			 * man.addThread(thread, maxHistoryNum); man.Save(); } catch
			 * (Exception e) { FLog.d("message", e); }
			 * 
			 * if (!activity.mode.equals("history")) { // 通常 String baseUrl =
			 * activity.baseUrl; intent.putExtra("baseUrl", baseUrl);
			 * intent.putExtra("BBSName", activity.BBSName);
			 * FLog.d("normal intent"); } else { String baseUrl = item.baseUrl;
			 * // 履歴モード intent.putExtra("baseUrl", baseUrl);
			 * intent.putExtra("BBSName", item.BBSName);
			 * FLog.d("history intent"); } intent.putExtra("threadNum",
			 * threadNum); intent.setClassName(activity.getPackageName(),
			 * activity .getClass().getPackage().getName() + ".FutabaThread");
			 * activity.startActivity(intent); } }); }
			 * 
			 * Catalog activity = (Catalog) getContext();
			 * 
			 * final int pos = position;
			 * 
			 * if (item != null) { // テキストをビューにセット
			 * text.setTextSize(StateMan.getMainFontSize(getContext())); String
			 * mainText = item.text; if (!activity.mode.equals("history")) { //
			 * 通常(not 履歴)モード mainText =
			 * StringUtil.highlightFocusWordMatched(mainText,
			 * activity.focusWords);
			 * 
			 * try { FutabaThreadContent prevThread = activity.man
			 * .get(item.threadNum); int diffResNum =
			 * Integer.parseInt(item.resNum) -
			 * Integer.parseInt(prevThread.resNum);
			 * resNum.setText(Html.fromHtml(item.resNum + "レス" +
			 * "<font color=\"red\">(+" + Math.max(0, diffResNum) +
			 * "レス)</font>")); } catch (Exception e) { //履歴にないよ
			 * //FLog.d("message", e);
			 * FLog.d("thread "+item.threadNum+" not found");
			 * resNum.setText(item.resNum + "レス"); } }else{
			 * resNum.setText(item.resNum + "レス"); } if (item.text != null) {
			 * CharSequence cs = Html.fromHtml(mainText); text.setText(cs); }
			 * resNum.setTextSize(StateMan.getDescFontSize(getContext()));
			 * BBSName.setTextSize(StateMan.getDescFontSize(getContext()));
			 * nonclickableblank.setTextSize(StateMan
			 * .getDescFontSize(getContext())); if
			 * (activity.mode.equals("history")) { // 履歴モード BBSName.setText("("
			 * + item.BBSName + ")");
			 * view.setBackgroundColor(Color.parseColor("#F0E0D6"));
			 * checkbox.setChecked(item.isChecked);
			 * checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener()
			 * { public void onCheckedChanged(CompoundButton buttonView, boolean
			 * isChecked) { FLog.d("" + buttonView.isShown());
			 * FLog.d("onCheckedChanged called at" + pos + " with" + isChecked);
			 * if (buttonView.isShown()) { // 画面から外れたときのfalse値回避
			 * items.get(pos).isChecked = isChecked; } }
			 * 
			 * }); } else { // 通常モード
			 * view.setBackgroundColor(Color.parseColor("#FFFFEE"));
			 * checkbox.setVisibility(View.GONE);
			 * nonclickableblank.setVisibility(View.GONE); }
			 * 
			 * // とりあえず空画像を作成 bm = Bitmap.createBitmap(50, 50,
			 * Bitmap.Config.ALPHA_8); iv.setImageBitmap(bm);
			 * 
			 * // 画像をセット try { if (item.imgURL != null) {
			 * iv.setTag(item.imgURL); ImageGetTask task = new ImageGetTask(iv);
			 * task.execute(item.imgURL); //
			 * FLog.d("image "+item.getImgURL()+"set" ); } else { // Bitmap bm =
			 * null; // ImageView iv = //
			 * (ImageView)view.findViewById(R.id.image); //
			 * iv.setImageBitmap(bm); } } catch (Exception e) {
			 * FLog.d("message", e); } }
			 */
		} catch (Exception e) {
			FLog.d("message", e);
		}
		return view;
	}

	// サムネ画像取得用スレッド
	class ImageGetTask extends AsyncTask<String, Void, Bitmap> {
		private ImageView image;
		private String tag;
		private int id;

		public ImageGetTask(ImageView _image) {
			image = _image;
			tag = _image.getTag().toString();
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			Bitmap bm = null;
			try {
				bm = ImageCache.getImage(urls[0]);
				if (bm == null) { // does not exist on cache
					ImageCache.setImage(urls[0]);
					bm = ImageCache.getImage(urls[0]);
				}
				bm = ImageResizer.ResizeWideToSquare(bm);
			} catch (Exception e) {
				FLog.d(e.toString());
			}
			return bm;
		}

		// メインスレッドで実行する処理
		@Override
		protected void onPostExecute(Bitmap result) {
			// FLog.d(,
			// "tag="+tag+" image.getTag="+image.getTag().toString() );
			// Tagが同じものが確認して、同じであれば画像を設定する
			if (result != null && tag.equals(image.getTag().toString())) {
				image.setImageBitmap(result);
			}
		}
	}

}
