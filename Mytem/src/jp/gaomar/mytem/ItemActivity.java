package jp.gaomar.mytem;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemActivity extends Activity{

	// 商品情報(MytemMaster)のキー
	private static final String KEY_MYTEM_MASTER = "MYTEM_MASTER";

	// JANコード
	private TextView janText = null;
	// 商品名
	private TextView nameText = null;
	// 商品の画像
	private ImageView mytemImageView = null;
	// リクエストコードの値（どこから呼び出されたか）
	private int requestCode = 0;
	// 商品情報
	private MytemMaster mytemMaster = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
		
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
		String nmJancode = mytemMaster.getJanCode();
		String nmName = mytemMaster.getName();
		Bitmap nmImage = mytemMaster.getImage();

		// UIにMytemMasterの情報をセット
		if (nmJancode != null)
			janText.setText(nmJancode);
		if (nmName != null)
			nameText.setText(nmName);
		if (nmImage != null)
			mytemImageView.setImageBitmap(nmImage);
	}

	/**
	 * UIを取ってくる
	 */
	private void initUi(){
		janText = (TextView) findViewById(R.id.JanText);
		nameText = (TextView) findViewById(R.id.NameText);
		mytemImageView = (ImageView) findViewById(R.id.MytemImageView);
	}

	/**
	 * 履歴登録ボタンが押されたとき
	 * 
	 * @param view
	 */
	public void onCreateHistoryClick(View view) {

		int requestCode = RequestCode.ITEM2CREATE.ordinal();
		Intent intent = new Intent(this, CreateHistoryActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE,
				RequestCode.ITEM2CREATE.ordinal());
		// MytemMaster情報もインテントに情報を送る
		intent.putExtra(KEY_MYTEM_MASTER, mytemMaster);
		// インテントを発行する
		startActivityForResult(intent,requestCode);

	}

	/**
	 * 自分の履歴ボタンが押されたとき
	 * 
	 * @param view
	 */
	public void onHistoryClick(View view) {

		int requestCode = RequestCode.ITEM2MYHISTORY.ordinal();
		Intent intent = new Intent(this, HistoryActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE,
				RequestCode.ITEM2MYHISTORY.ordinal());
		// MytemMaster情報もインテントに情報を送る
		intent.putExtra(KEY_MYTEM_MASTER, mytemMaster);
		// インテントを発行する
		startActivityForResult(intent,requestCode);

	}

	/**
	 * logoボタンが押された時の動作
	 * 
	 * @param v
	 */
	public void onLogoClick(View v) {
		Intent intent = new Intent(this, MainActivity.class);
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
		finish();
	}
	
	/**
	 * みんなの履歴ボタンが押されたとき
	 * 
	 * @param view
	 */
	public void onSocialHistoryClick(View view) {

		int requestCode = RequestCode.ITEM2SOCIAL.ordinal();
		Intent intent = new Intent(this, HistoryActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE,
				RequestCode.ITEM2SOCIAL.ordinal());
		// MytemMaster情報もインテントに情報を送る
		intent.putExtra(KEY_MYTEM_MASTER, mytemMaster);
		// インテントを発行する
		startActivityForResult(intent,requestCode);

	}
	

}
