package jp.gaomar.magicofgreeting;


import jp.Adlantis.Android.AdlantisView;
import jp.co.imobile.android.AdRequestResult;
import jp.co.imobile.android.AdView;
import jp.co.imobile.android.AdViewRequestListener;
import net.nend.android.NendAdView;
import android.app.Activity;
import android.widget.LinearLayout;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;

public class Ads implements AdWhirlInterface{
	private Activity mActivity;  
	private LinearLayout mLayout;
	
	private AdWhirlLayout adWhirlLayout;
	private jp.co.imobile.android.AdView imobileAd;
	private AdlantisView adlantisAd;
	private NendAdView nendAd;
	
	private final int nendSpot_id = 24647;
	private final String nendApiKey = "1b6391c8f4f5bf2a6d35815b660667b94c7f3d2e";
	private final int iMobileMediaID = 38158;
	private final int iMobileSpotID = 70671;
	private final String adWhirlID = "4a543c4fd756488e9640f991cf7ad5cc";
	private final String adWhirlTestID = "b1c6d948d2894629b96b93a3dbddfc63";
    public Ads(Activity act, LinearLayout layout) {  
        mActivity = act;  
        mLayout = layout;
        adInit();
    }  

	/**
	 * 広告初期化
	 */
	private void adInit() {
		imobileAd = AdView.createForAdWhirl(mActivity, iMobileMediaID, iMobileSpotID);
	    nendAd = new NendAdView(mActivity, nendSpot_id, nendApiKey);	    
//	    adlantisAd = new AdlantisView(mActivity); //AdLantisのビューを作成
	    if (Util.isDebuggable(mActivity)) {
	    	adWhirlLayout = new AdWhirlLayout(mActivity, adWhirlTestID);
	    } else {
	    	adWhirlLayout = new AdWhirlLayout(mActivity, adWhirlID);
	    }
	    //AdWhirl管理画面のSDK Key:を入力する
	    adWhirlLayout.setAdWhirlInterface(this);
	    mLayout.addView(adWhirlLayout);
	    
	}
	
	public void nend() {
		adWhirlLayout.adWhirlManager.resetRollover();
		adWhirlLayout.removeAllViews();
		adWhirlLayout.addView(nendAd);
		adWhirlLayout.rotateThreadedDelayed();
	}

	public void imobile() {
		imobileAd.setOnRequestListener(new AdViewRequestListener() {
			@Override
			public void onCompleted(AdRequestResult result, AdView sender) {
				adWhirlLayout.adWhirlManager.resetRollover();
				adWhirlLayout.removeAllViews();
				adWhirlLayout.addView(sender);
				adWhirlLayout.rotateThreadedDelayed();
			}
			@Override
			public void onFailed(AdRequestResult result, AdView sender) {
				adWhirlLayout.rollover();
			}
		});
		// 広告取得開始
		imobileAd.start();
	}

	public void adLantis() {
		adlantisAd.showNextAd();
		adWhirlLayout.adWhirlManager.resetRollover();
		adWhirlLayout.removeAllViews();
		adWhirlLayout.addView(adlantisAd);
		adWhirlLayout.rotateThreadedDelayed();
	}

	@Override
	public void adWhirlGeneric() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

}
