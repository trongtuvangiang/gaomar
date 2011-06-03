package jp.gaomar.magicofgreeting;

import jp.co.nobot.libAdMaker.libAdMaker;

import com.admob.android.ads.AdListener;
import com.admob.android.ads.AdView;
import com.e3roid.E3Engine;
import com.e3roid.opengl.RenderSurfaceView;

public class LayoutActivity extends MainActivity{
	private final static int WIDTH  = 320;
	private final static int HEIGHT = 440;

    // AdmobÇÃçLçêópView
    protected AdView adView;
    // AdMakerÇ≥ÇÒÇ©ÇÁì`Ç¶ÇÁÇÍÇΩURL
    private static final String ADMAKER_URL = "http://images.ad-maker.info/apps/t9hrrx9sv97a.html";
    // AdMakerÇ©ÇÁì`Ç¶ÇÁÇÍÇΩsiteID
    private static final String ADMAKER_SITEID = "881";
    // AdMakerÇ©ÇÁì`Ç¶ÇÁÇÍÇΩsiteID
    private static final String ADMAKER_ZONEID = "1835";

	@Override
	protected void onSetContentView() {
		this.setContentView(R.layout.main);
		this.surfaceView = (RenderSurfaceView)this.findViewById(R.id.layout_po);
		this.surfaceView.setRenderer(this.engine);

//		setAdView();
	}

	@Override
	public E3Engine onLoadEngine() {
		E3Engine engine = new E3Engine(this, WIDTH, HEIGHT, E3Engine.RESOLUTION_FIXED_RATIO);
		engine.requestFullScreen();
		engine.requestPortrait();
		return engine;
	}

	protected void setAdView() {
//        adView = (AdView) findViewById(R.id.ad);
//
//        // AdMakerÇÃçLçêÇï\é¶Ç∑ÇÈÇÃÇ≈AdViewÇÃçXêVÇçsÇÌÇπÇ»Ç¢
//        adView.setRequestInterval(0);
//        adView.setVisibility(AdView.GONE);
        // AdMakerÇÃçLçêÇï\é¶Ç≥ÇπÇÈ
        libAdMaker ad = (libAdMaker)findViewById(R.id.admakerview);
        ad.setActivity(LayoutActivity.this);
        ad.siteId = ADMAKER_SITEID;
        ad.zoneId = ADMAKER_ZONEID;
        ad.setUrl(ADMAKER_URL);
        ad.setVisibility(libAdMaker.VISIBLE);
        ad.start();
//
//        adView.setAdListener(new AdListener() {
//
//            public void onReceiveRefreshedAd(AdView adView) {
//            }
//
//            public void onReceiveAd(AdView adView) {
//            }
//
//            public void onFailedToReceiveRefreshedAd(AdView adView) {
//            }
//
//            public void onFailedToReceiveAd(AdView adView) {
//                // AdMakerÇÃçLçêÇï\é¶Ç∑ÇÈÇÃÇ≈AdViewÇÃçXêVÇçsÇÌÇπÇ»Ç¢
//                adView.setRequestInterval(0);
//                adView.setVisibility(AdView.GONE);
//                // AdMakerÇÃçLçêÇï\é¶Ç≥ÇπÇÈ
//                libAdMaker ad = (libAdMaker)findViewById(R.id.admakerview);
//                ad.setActivity(LayoutActivity.this);
//                ad.siteId = ADMAKER_SITEID;
//                ad.zoneId = ADMAKER_ZONEID;
//                ad.setUrl(ADMAKER_URL);
//                ad.setVisibility(libAdMaker.VISIBLE);
//                ad.start();
//            }
//        });
    }

}
