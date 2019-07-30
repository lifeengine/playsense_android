package com.arsdale.playsense.JSInterface;

import android.app.Activity;
import android.webkit.JavascriptInterface;


public class MyJSInterface {

    Activity mActivity;
    MainActivityInterface mInterface;

    public MyJSInterface(Activity activity, MainActivityInterface mainActivityInterface) {
        this.mActivity = activity;
        this.mInterface = mainActivityInterface;
    }

    @JavascriptInterface
    public void facebook_share(String url, String text) {
        this.mInterface.facebookShare(url, text);
    }

    @JavascriptInterface
    public void band_share(String url, String text) {
        this.mInterface.bandShare(url, text);
    }

    @JavascriptInterface
    public void facebook_login() {
        this.mInterface.facebookLogin();
    }

    @JavascriptInterface
    public void kakao_login() {
        this.mInterface.kakaoLogin();
    }

    @JavascriptInterface
    public String request_fcm_id() {return this.mInterface.getFCMID();}

    @JavascriptInterface
    public String request_version_info() {
        return this.mInterface.getVersionInfo();
    }

    @JavascriptInterface
    public void open_browser(String url) {
        this.mInterface.openBrowser(url);
    }


    @JavascriptInterface
    public void kakaotalk_share(String url, String text) {
        this.mInterface.kakaoTalkShare(url,text);
    }

    @JavascriptInterface
    public boolean is_kakaostory_installed() {
        return this.mInterface.isKakaostoryInstalled();
    }
}

