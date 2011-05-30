package jp.gaomar.mytem;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryActivity extends Activity{
    
    // AdMakerさんから伝えられたURL
    private static final String ADMAKER_URL = "http://images.ad-maker.info/apps/3cxim1xrurk9.html";
    // AdMakerから伝えられたsiteID
    private static final String ADMAKER_SITEID = "881";
    // AdMakerから伝えられたsiteID
    private static final String ADMAKER_ZONEID = "3452";
    
	// 商品情報(MytemMaster)のキー
	private static final String KEY_MYTEM_MASTER = "MYTEM_MASTER";
	// 商品データ
	private MytemMaster _mytemMaster;
	// 呼び出し元インテント
	private int _requestCode;
	// 商品履歴データ
	private List<MytemHistory> _mytemHistory;
	// プログレスアイコン
	private ProgressDialog progressDialog = null;
	// レイアウト
	private LinearLayout layout = null;
	// JANコード
	private TextView janText = null;
	// タイトルテキスト
	private TextView titleText = null;
	// 商品名
	private TextView nameText = null;
	// リクエストコードの値（どこから呼び出されたか）
	private int requestCode = 0;
	// 商品情報
	private MytemMaster mytemMaster = null;

	// ReaderActivityの状態
	private static final int GET_HISTORY = 100; // みんなの履歴
	private static final int GET_MYHISTORY = 200; // 自分の履歴
	private static final int GET_FINISH = 300; // 次のインテントへ処理を移す
	private static final int GOTO_NEXT_INTENT = 400; // 次のインテントへ処理を移す

	/**
	 * メッセージハンドラーに対する処理
	 */
	private final Handler _handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_HISTORY:
				receiveMytemHistory(mytemMaster.getJanCode());
				break;
			case GET_MYHISTORY:
				receiveMyHistory();
				break;
			case GET_FINISH:
				// 取得データセット
				setHistoryData();
				break;
			case GOTO_NEXT_INTENT:
				// 次のアクティビティへ遷移する
				gotoNextActivity();
				break;

			default:
				// その他・・・
				// switch()内に処理が書かれていないステートが存在するため、
				// 例外を投げておく
				throw new ReaderStateException(msg.toString());
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		// リクエストコードを取得
		Intent intent = getIntent();
		requestCode = intent.getIntExtra(RequestCode.KEY_RESUEST_CODE, -1);
		// リクエストコードがセットされてない場合は終了
		if (requestCode == -1)
			finish();

		// ボタンとかエディットボックスとかのViewをメンバー変数に格納
		initUi();

		// 情報の取得
		mytemMaster = (MytemMaster) intent
				.getParcelableExtra(KEY_MYTEM_MASTER);
		
		// MytemMasterから情報を取り出す
		String nmJancode = null, nmName = null;
		Bitmap nmImage = null;
		if (mytemMaster != null) {
			nmJancode = mytemMaster.getJanCode();
			nmName = mytemMaster.getName();
			nmImage = mytemMaster.getImage();
		}
		
		// UIにMytemMasterの情報をセット
		if (nmJancode != null)
			janText.setText(nmJancode);
		if (nmName != null)
			nameText.setText(nmName);
		
		// 履歴を取得する
		if (requestCode != RequestCode.DASHBORAD2HISTORY.ordinal()) {
			_handler.sendEmptyMessage(GET_HISTORY);
		} else {
			_handler.sendEmptyMessage(GET_MYHISTORY);
		}
		
		if (requestCode == RequestCode.DASHBORAD2HISTORY.ordinal()) {
			titleText.setText(getString(R.string.mytem_all_history));
		} else if (requestCode == RequestCode.ITEM2SOCIAL.ordinal()) {
			titleText.setText(getString(R.string.social_history));
		} else {
			titleText.setText(getString(R.string.mytem_history));
		}
		
//        adView();

	}
    
//    public void adView() {
//        // AdMakerの広告を表示させる
//        libAdMaker ad = (libAdMaker)findViewById(R.id.admakerview);
//        ad.setActivity(HistoryActivity.this);
//        ad.siteId = ADMAKER_SITEID;
//        ad.zoneId = ADMAKER_ZONEID;
//        ad.setUrl(ADMAKER_URL);
//        ad.setVisibility(libAdMaker.VISIBLE);
//        ad.start();	
//    }

	/**
	 * UIを取ってくる
	 */
	private void initUi(){
		if (requestCode != RequestCode.DASHBORAD2HISTORY.ordinal()) {
			layout = (LinearLayout) findViewById(R.id.infomation);
			layout.setVisibility(View.VISIBLE);
		}
		titleText = (TextView) findViewById(R.id.txt_title);
		janText = (TextView) findViewById(R.id.JanText);
		nameText = (TextView) findViewById(R.id.NameText);
		
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.dialog_message));
        progressDialog.setCancelable(false);

	}

	/**
	 * logoボタンが押された時の動作
	 * 
	 * @param v
	 */
	public void onLogoClick(View v) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
	
	/**
	 * 読込ボタンが押されたとき
	 * 
	 * @param view
	 */
	public void onReaderButtonClick(View view) {

		int requestCode = RequestCode.DASHBORAD2READER.ordinal();
		gotoReaderActivity(requestCode);
	}

	/**
	 * リーダーの起動 
	 * 
	 * @param requestCode
	 */
	private void gotoReaderActivity(int requestCode) {
		Intent intent = new Intent(this, ReaderActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		startActivityForResult(intent, requestCode);
	}
	
	/**
	 * 履歴情報セット
	 */
	private void setHistoryData() {
		if (_mytemHistory.size() == 0) {
			Toast.makeText(this, "履歴はありません", Toast.LENGTH_SHORT).show();
		}
    	ListAdapter adapter = new ListAdapter(this);
        ListView list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
    }

	/**
	 * リストアダプター
	 * @author takauma
	 *
	 */
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater = null;
		private Context context;
		
		public ListAdapter (Context _context) {
			layoutInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			context = _context;
		}
		
		@Override
		public int getCount() {
			return _mytemHistory.size();
		}

		@Override
		public Object getItem(int pos) {
			return _mytemHistory.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(final int pos, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.action_history_item, null);				
			}
			
			LinearLayout itemArea = (LinearLayout) convertView.findViewById(R.id.itemArea);
			LinearLayout btnArea = (LinearLayout) convertView.findViewById(R.id.btnArea);
			TextView txt_ItemName = (TextView) convertView.findViewById(R.id.txt_itemname);
			TextView txt_ShopName = (TextView) convertView.findViewById(R.id.txt_shopname);
			TextView txt_Price    = (TextView) convertView.findViewById(R.id.txt_price);
			TextView txt_PostDate = (TextView) convertView.findViewById(R.id.txt_postdate);
			TextView txt_Note     = (TextView) convertView.findViewById(R.id.txt_note);
			Button btnItem = (Button) convertView.findViewById(R.id.itemButton);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			try {
				if (requestCode == RequestCode.DASHBORAD2HISTORY.ordinal()) {
					txt_ItemName.setText(_mytemHistory.get(pos).getItemName());
					itemArea.setVisibility(View.VISIBLE);
					btnArea.setVisibility(View.VISIBLE);
					btnItem.setOnClickListener(new OnClickListener() {						
						@Override
						public void onClick(View v) {
							progressDialog.show();
							receiveMytemData(_mytemHistory.get(pos).getJanCode());
						}
					});
				}				
				txt_ShopName.setText(_mytemHistory.get(pos).getShopName());
				txt_Price.setText(Integer.toString(_mytemHistory.get(pos).getPrice()) + "円");				
				txt_PostDate.setText(sdf.format(_mytemHistory.get(pos).getPostDate()));
				txt_Note.setText(_mytemHistory.get(pos).getNote());
								
			} catch (Exception e) {
			}
			
			return convertView;
		}
	
	}

	/**
	 * QRコードスキャナーが受信したJANコードを検索キーとして 履歴・GAEから商品情報を取得する
	 */
	private void receiveMytemData(String jancode) {
		//問い合わせ用AsyncTaskを起動する
		ReadMytemAsyncTask readAsyncTask = new ReadMytemAsyncTask();
		readAsyncTask.execute(jancode);
	}
	
	/**
	 * QRコードスキャナーが受信したJANコードを検索キーとして GAEから商品履歴情報を取得する
	 */
	private void receiveMytemHistory(String _janCode) {
		//問い合わせ用AsyncTaskを起動する
		ReadAsyncTask readAsyncTask = new ReadAsyncTask();
		readAsyncTask.execute(_janCode);
	}

	/**
	 * 今まで読み取った履歴を表示する
	 */
	private void receiveMyHistory() {
		//問い合わせ用AsyncTaskを起動する
		ReadAsyncTask readAsyncTask = new ReadAsyncTask();
		readAsyncTask.execute();
	}

	/**
	 * GAE問い合わせ用のAsyncTask
	 * 
	 * @author hide
	 * 
	 */
	private class ReadAsyncTask extends
			AsyncTask<String, List<MytemHistory>, List<MytemHistory>> {
		private MytemManager mytemManager;
		private int errorResId;

		/**
		 * コンストラクタ
		 */
		public ReadAsyncTask() {
			mytemManager = new MytemManager(HistoryActivity.this);
		}

		@Override
		protected List<MytemHistory> doInBackground(String... params) {
			List<MytemHistory> mytemHistory = null;
			// JANコードを検索キーにして、履歴またはGAEから
			// 商品を検索する
			try {
				if (requestCode == RequestCode.ITEM2SOCIAL.ordinal()) {
					mytemHistory = mytemManager.getSocialMytemHistories(params[0]);
				} else if (requestCode == RequestCode.ITEM2MYHISTORY.ordinal()){
					mytemHistory = mytemManager.getMytemHistories(params[0]);
				} else if (requestCode == RequestCode.DASHBORAD2HISTORY.ordinal()) {
					mytemHistory = mytemManager.getMytemHistories();
				}
				return mytemHistory;
			} catch (GaeException e) {
				Log.d("mytembug", ExceptionToStringConverter.convert(e));
				errorResId = R.string.sql_gae_error;
//				Toast.makeText(ReaderActivity.this,
//						"サーバー問い合わせでエラーが発生しました",
//						Toast.LENGTH_LONG).show();
				return null;
			} catch (SQLException e) {
				Log.d("mytembug", ExceptionToStringConverter.convert(e));
				errorResId = R.string.sql_local_error;
				return null;				
			} catch(Exception e){
				Log.d("mytembug", ExceptionToStringConverter.convert(e));
				errorResId = R.string.sql_unknown_error;
//				Toast.makeText(ReaderActivity.this,
//						"原因不明のエラーが発生しました",
//						Toast.LENGTH_LONG).show();
				return null;
			}

		}

		protected void onPostExecute(List<MytemHistory> mytemHistory) {
			_mytemHistory = mytemHistory;
			// 読み込み完了
			_handler.sendEmptyMessage(GET_FINISH);
		}

	}

	/**
	 * GAE問い合わせ用のAsyncTask
	 * 
	 * @author hide
	 * 
	 */
	private class ReadMytemAsyncTask extends
			AsyncTask<String, MytemMaster, MytemMaster> {
		private MytemManager mytemManager;
		private int errorResId;

		/**
		 * コンストラクタ
		 */
		public ReadMytemAsyncTask() {
			mytemManager = new MytemManager(HistoryActivity.this);
		}

		@Override
		protected MytemMaster doInBackground(String... params) {
			MytemMaster mytemMaster = null;
			// JANコードを検索キーにして、履歴またはGAEから
			// 商品を検索する
			try {
				mytemMaster = mytemManager.getMytemMaster(params[0]);
				// @hideponm
				if (mytemMaster == null) {
					// 該当商品がないのでJANコードだけ入れたMytemMasterを作ってあげる
					mytemMaster = new MytemMaster(params[0], "", "");
				}
				return mytemMaster;
			} catch (GaeException e) {
				Log.d("mytembug", ExceptionToStringConverter.convert(e));
				errorResId = R.string.sql_gae_error;
//				Toast.makeText(ReaderActivity.this,
//						"サーバー問い合わせでエラーが発生しました",
//						Toast.LENGTH_LONG).show();
				return null;
			} catch(Exception e){
				Log.d("mytembug", ExceptionToStringConverter.convert(e));
				errorResId = R.string.sql_unknown_error;
//				Toast.makeText(ReaderActivity.this,
//						"原因不明のエラーが発生しました",
//						Toast.LENGTH_LONG).show();
				return null;
			}

		}

		protected void onPostExecute(MytemMaster mytemMaster) {
			_mytemMaster = mytemMaster;
			// 次のインテントへ遷移する
			_handler.sendEmptyMessage(GOTO_NEXT_INTENT);
		}

	}

	/**
	 * 次の画面へ遷移する
	 */
	private void gotoNextActivity() {
		Intent intent = null;

		// 次の画面へ遷移する
		// (1)ダッシュボード→商品読込みの場合
		// →タイマー画面へ遷移する
		// (2)ダッシュボード→商品登録の場合
		// →登録画面へ遷移する

		// 商品情報オブジェクトが空の場合は、ダッシュボードに戻る
		// GAE問い合わせ、SQliteエラーの場合はnullが返ってきます @hideponm
		// ※通常はありえない
		if (null == _mytemMaster) {
			intent = getIntent();
			//処理失敗なのでRESULT_CANCELEDをダッシュボードに返す
			setResult(RESULT_CANCELED, intent);
			finish();
			return;
		}

		// (1)ダッシュボード→商品読込みの場合
		if (_mytemMaster.isCompleteData()) {
			// →購入履歴画面へ遷移する			
			intent = new Intent(this, ItemActivity.class);
			intent.putExtra(RequestCode.KEY_RESUEST_CODE,
					RequestCode.READER2HISTORY.ordinal());
			_requestCode = RequestCode.READER2HISTORY.ordinal();
		}

		// MytemMaster情報もインテントに情報を送る
		intent.putExtra(KEY_MYTEM_MASTER, _mytemMaster);
		// インテントを発行する
		startActivityForResult(intent,_requestCode);
		//お役ごめん @hideponm
		progressDialog.dismiss();
		//finish();	// 削除 by @leibun ここで終わられるとアクションバーのボタンの動作がダッシュボードに伝わらない onActivityForResultでfinish() 
	}

}
