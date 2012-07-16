package jp.gaomar.astronumbertouch;

import java.util.Locale;
import java.util.Random;

import jp.co.microad.smartphone.sdk.MicroAdLayout;

import mediba.ad.sdk.android.MasAdProxyListener;
import mediba.ad.sdk.android.MasAdView;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class Ads {
	private Activity mActivity;  
	private LinearLayout mLayout;
	
    public Ads(Activity act, LinearLayout layout) {  
        mActivity = act;  
        mLayout = layout;
    }  

    /** 
     * 指定した広告を出す 
     */  
    public void setAdsDisp(int id){  
        if (Locale.getDefault().equals(Locale.JAPAN)
        		|| Locale.getDefault().equals(Locale.JAPANESE)) {
	        switch(id){  
	            case 0:     // MicroAd  
	            	//setAdmob(0);
	            	setMicroAd();
	                break;  
	            case 1:     // Mediba  
	                setMediba();  
	                break;
	        }
        } else {
            setAdmob(id);          	
        }
    }  

    /** 
     * 広告をランダムに設定する 
     */  
    public void setAdsRandom(){  
        Random random = new Random();  
        int i = random.nextInt(100);
        if (Locale.getDefault().equals(Locale.JAPAN)
        		|| Locale.getDefault().equals(Locale.JAPANESE)) {
	        switch(i % 2){  
	            case 0:     // AdMob  
	                setMediba();  
	                break;  
	            case 1:     // Mediba  
	                setMediba();  
	                break;
	        }
        } else {
        	int id = random.nextInt(100);
            setAdmob(id);          	
        }
    }  
	      
    /** 
     * Admobを設定します。 
     */  
    public void setAdmob(final int id){  
        // xmlからAdmobViewを生成する  
        LayoutInflater inflater = mActivity.getLayoutInflater();  
        AdView av = (AdView)inflater.inflate(R.layout.ads_admob, null);  
          
        // AdmobはAdListenerをセットできる  
        av.setAdListener(new AdListener() {  
            @Override  
            public void onReceiveAd(Ad arg0) {  
            }  
            @Override  
            public void onPresentScreen(Ad arg0) {  
            }  
            @Override  
            public void onLeaveApplication(Ad arg0) {  
            }  
            @Override  
            public void onDismissScreen(Ad arg0) {  
            }
			@Override
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		        switch(id % 2){  
	            case 0:     // MicroAd  
	                setMicroAd();  
	                break;  
	            case 1:     // Mediba  
	                setMediba();  
	                break;
		        }
			}  
        });  
  
        // 広告レイアウトにAdmobをセットする
        mLayout.addView(av);  			
        av.loadAd(new AdRequest());  
    }  
	      
	      
    /**  
     * medibaを設定します。 
     */  
    public void setMediba(){  
        // medibaを生成する  
        MasAdView av = new MasAdView(mActivity);  
          
        // medibaはMasAdProxyListenerをセットする  
        av.setAdListener(new MasAdProxyListener(mActivity.getClass().getName()) {  
            @Override  
            public void onReceiveRefreshedAd(MasAdView arg0) {  
            }  
            @Override  
            public void onReceiveAd(MasAdView arg0) {  
            }  
            @Override  
            public void onFailedToReceiveRefreshedAd(MasAdView arg0) {  
            }  
            @Override  
            public void onFailedToReceiveAd(MasAdView arg0) {  
            }  
        });  
  
        // 広告レイアウトにmedibaをセットする  
        mLayout.addView(av);  			
        
    }  

    /**  
     * microadを設定します。 
     */  
    public void setMicroAd(){  
        // MicroAdを生成する  
    	MicroAdLayout av = new MicroAdLayout(mActivity);
    	av.init(mActivity);
    	
        // 広告レイアウトにMicroAdをセットする  
        mLayout.addView(av);  			
        
    }  

}
