package com.arsdale.playsense.JSInterface;

public interface MainActivityInterface {

    void facebookShare(String url, String text);
    void bandShare(String url, String text);
    void facebookLogin();
    void kakaoLogin();
    void openBrowser(String url);

    String getFCMID();
    String getVersionInfo();

    void kakaoStoryShare(String url, String text);
    void kakaoTalkShare(String url, String text);

    boolean isKakaostoryInstalled();

}
