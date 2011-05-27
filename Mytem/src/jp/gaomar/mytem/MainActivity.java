package jp.gaomar.mytem;



import jp.co.nobot.libAdMaker.libAdMaker;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
	
    // AdMakerさんから伝えられたURL
    private static final String ADMAKER_URL = "http://images.ad-maker.info/apps/3cxim1xrurk9.html";
    // AdMakerから伝えられたsiteID
    private static final String ADMAKER_SITEID = "881";
    // AdMakerから伝えられたsiteID
    private static final String ADMAKER_ZONEID = "3452";
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        adView();
    }
    
    public void adView() {
        // AdMakerの広告を表示させる
        libAdMaker ad = (libAdMaker)findViewById(R.id.admakerview);
        ad.setActivity(MainActivity.this);
        ad.siteId = ADMAKER_SITEID;
        ad.zoneId = ADMAKER_ZONEID;
        ad.setUrl(ADMAKER_URL);
        ad.setVisibility(libAdMaker.VISIBLE);
        ad.start();	
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
	 * 履歴ボタンが押されたとき
	 * @param view
	 */
	public void onHistoryButtonClick(View view) {
		int requestCode = RequestCode.DASHBORAD2HISTORY.ordinal();
		gotoHistoryActivity(requestCode);
		
	}
	
	/**
	 * リーダーの起動 requestCodeでその後にTimerActivityかCreateActivityを選択
	 * 
	 * @param requestCode
	 */
	private void gotoReaderActivity(int requestCode) {
		Intent intent = new Intent(this, ReaderActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 履歴画面へ遷移
	 * 
	 * @param requestCode
	 */
	private void gotoHistoryActivity(int requestCode) {
		Intent intent = new Intent(this, HistoryActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		startActivityForResult(intent, requestCode);
	}

}