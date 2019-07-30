package com.arsdale.playsense;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.arsdale.playsense.JSInterface.MainActivityInterface;
import com.arsdale.playsense.JSInterface.MyJSInterface;
import com.arsdale.playsense.MyClients.MyWebChromeClient;
import com.arsdale.playsense.MyClients.MyWebViewClient;
import com.arsdale.playsense.Share.KakaoTalkShare;
import com.arsdale.playsense.Utils.AppChecker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import static com.kakao.util.helper.Utility.getPackageInfo;


public class MainActivity extends AppCompatActivity implements MainActivityInterface {

    private static final String TAG = "TAGGG";
    private WebView mWebview;

    public static final String INTENT_PROTOCOL_START = "intent:";
    public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
    public static final String INTENT_PROTOCOL_END = ";end;";
    public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";

    private CallbackManager callbackManager;

    private SessionCallback callback;

    private String HOME_URL = "https://playsense.kr";
    //private String HOME_URL = "http://192.168.0.12:8888/playsense";
    private String FCM_ID = "";
    LoginButton loginButton;
    private BackPressCloseHandler backPressCloseHandler;

    private LinearLayout logoWrapper;

    private ProgressBar pBar;

    private HashMap<String, String> shareApps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        backPressCloseHandler = new BackPressCloseHandler(this);

        mWebview = (WebView)findViewById(R.id.webview);
        loginButton = (LoginButton) findViewById(R.id.kakao_login_btn);
        logoWrapper = (LinearLayout)findViewById(R.id.logo_layer);
        pBar = (ProgressBar)findViewById(R.id.progress_bar);

        shareApps = new HashMap<String, String>();

        shareApps.put("kakaotalk","com.kakao.talk");
        shareApps.put("kakaostory","com.kakao.story");
        shareApps.put("band","com.nhn.android.band");
        shareApps.put("facebook","com.facebook.katana");

        //FirebaseMessaging.getInstance().setAutoInitEnabled(true);


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(4000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("tag","extara: logo gone");

                            logoWrapper.setVisibility(View.GONE);

                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        setWebviewSetting();

        if (getIntent().getStringExtra("redirectUrl") != null) {
            HOME_URL += getIntent().getStringExtra("redirectUrl").toString();
        }

        mWebview.loadUrl(HOME_URL);

        FCM_ID = FirebaseInstanceId.getInstance().getToken();
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        //requestMe();

        FacebookSdk.setApplicationId( getResources().getString(R.string.facebook_appID));
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        facebookCallback();

       // Log.d("TG","HASH: "+getKeyHash(this));

/*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.arsdale.playsense",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        */
       // Log.d("tag",getKeyHash(this));
/*
        final String channelId = getString(R.string.project_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String messageTitle = "[플레이센스] 영화 \"왓칭\" 티져 예고편 캠페인 참여 안내";
        String messageBody = "[플레이센스] 영화 \"왓칭\" 티져 예고편 캠페인 참여 안내[플레이센스] 영화 \"왓칭\" 티져 예고편 캠페인 참여 안내";
        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.main_icon)
                        .setContentTitle(messageTitle)
                       .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(messageBody)
                                .setBigContentTitle(messageTitle)
                        );
        MyReceiver mr = new MyReceiver(this);
        mr.sendNotification(messageTitle,messageBody,"","https://playsense.kr/include/poster_upload/d5bad15b45c25a8f0cdbccec4415496d20515cb41560132362.jpg");
        */

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();

       // if (getIntent().getStringExtra("redirectUrl") != null) {
       //     Log.d("tag","redirect url: "+getIntent().getStringExtra("redirectUrl").toString());
       //     HOME_URL += getIntent().getStringExtra("redirectUrl").toString();
       // }
       // Log.d("tag","extara url: "+HOME_URL);
       // mWebview.loadUrl(HOME_URL);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d("tag","on back press");

        if (mWebview.getUrl().equals("https://playsense.kr")) {
            backPressCloseHandler.onBackPressed();
        }else {
            if (mWebview.canGoBack()) {
                mWebview.goBack();
            }else {
                backPressCloseHandler.onBackPressed();
            }
        }
    }



    private void setWebviewSetting() {



        WebSettings set = mWebview.getSettings();
        //set.setUserAgentString(set.getUserAgentString() + "/APP_SWC_Android/Version=데이터/UDID=데이터" );
        //안드로이드 UserAgent Setting

        set.setDatabaseEnabled(true);
        set.setDomStorageEnabled(true);
        set.setLoadsImagesAutomatically(true);


        set.setCacheMode(WebSettings.LOAD_NO_CACHE);
        set.setAllowContentAccess(true);
        set.setPluginState(WebSettings.PluginState.ON);

        set.setJavaScriptEnabled(true);
        set.setGeolocationEnabled(true);
        set.setUseWideViewPort(true);
        set.setSaveFormData(false);
        set.setJavaScriptCanOpenWindowsAutomatically(true);
        set.setSupportMultipleWindows(true);
        set.setDomStorageEnabled(true);
        set.setAppCacheEnabled(true);
        set.setMediaPlaybackRequiresUserGesture(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebview.setWebContentsDebuggingEnabled(true);
        }


        mWebview.setHorizontalScrollBarEnabled(false);
        mWebview.setVerticalScrollBarEnabled(false);
        mWebview.setInitialScale(100);
        mWebview.addJavascriptInterface(new MyJSInterface(this,this),"app");
        mWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);



        mWebview.setWebChromeClient(new MyWebChromeClient(this,this));
        mWebview.setWebViewClient(new MyWebViewClient(this));

        //mWebview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        //mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void facebookShare(String url,String text) {


        if (AppChecker.getInstance().isPackageInstalled(shareApps.get("facebook"), this.getPackageManager())) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(url))
                    .setContentDescription(text)
                    .build();
            ShareDialog shareDialog = new ShareDialog(this);
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        } else {
            AppChecker.getInstance().openInGooglePlay(this, shareApps.get("facebook"));
        }

    }

    @Override
    public void facebookLogin() {
        if (AppChecker.getInstance().isPackageInstalled(shareApps.get("facebook"), this.getPackageManager())) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }else {
            AppChecker.getInstance().openInGooglePlay(this, shareApps.get("facebook"));
        }
    }

    @Override
    public void bandShare(String url, String text) {

        try {
            PackageManager manager = getPackageManager();
            Intent i = manager.getLaunchIntentForPackage("com.nhn.android.band");

            String serviceDomain = url; //  연동 서비스 도메인
            String encodedText = text; // 글 본문 (utf-8 urlencoded)
            Uri uri = Uri.parse("bandapp://create/post?text=" + encodedText + "&route=" + serviceDomain);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        } catch (Exception e) {
            // 밴드앱 설치되지 않은 경우 구글 플레이 설치페이지로 이동
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhn.android.band"));
            startActivity(intent);
            return;
        }
    }

    @Override
    public void kakaoStoryShare(String url, String text) {

    }

    @Override
    public void kakaoTalkShare(String url, String text) {

        if (AppChecker.getInstance().isPackageInstalled(shareApps.get("kakaotalk"), this.getPackageManager())) {
            KakaoTalkShare kts = new KakaoTalkShare(this);
            kts.openKakaoShare(text);
        }else {
            AppChecker.getInstance().openInGooglePlay(this,shareApps.get("kakaotalk"));
        }
    }

    @Override
    public String getFCMID() {
        if (FCM_ID == null ) {
           FCM_ID = FirebaseInstanceId.getInstance().getToken();
        }else {
            if (FCM_ID.equals("")) {
                FCM_ID = FirebaseInstanceId.getInstance().getToken();
            }
        }

        return FCM_ID;
    }

    @Override
    public String getVersionInfo() {
        return getPackageInfo(this).versionName.toString();
    }

    public void facebookCallback() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        mWebview.loadUrl("javascript:sns_login_return(\""+loginResult.getAccessToken().getUserId()+"\",\"fb\");");
                    }

                    @Override
                    public void onCancel() {
                        // App code

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        exception.printStackTrace();
                    }
                });

    }


    private void redirectLoginActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void kakaoLogin() {

        Log.d("tag","kakao login");
        if (AppChecker.getInstance().isPackageInstalled(shareApps.get("kakaotalk"), this.getPackageManager()) ) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loginButton.performClick();
                }
            });
        }else {
            AppChecker.getInstance().openInGooglePlay(this, shareApps.get("kakaotalk"));
        }

    }

    @Override
    public void openBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( url ));
        startActivity(intent);
    }

    @Override
    public boolean isKakaostoryInstalled() {
        if (AppChecker.getInstance().isPackageInstalled(shareApps.get("kakaostory"), this.getPackageManager())) {
            return true;
        }else {
            AppChecker.getInstance().openInGooglePlay(this, shareApps.get("kakaostory"));
            return false;
        }
    }

    class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Log.e("UserProfile", "================================================================");
                    String message = "failed to get user info. msg=" + errorResult;

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        //에러로 인한 로그인 실패
                        // finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("UserProfile", "================================================================");
                    Log.e("UserProfile", "sesson closed");

                }

                @Override
                public void onNotSignedUp() {
                    Log.e("UserProfile", "================================================================");
                    Log.e("UserProfile", "sesson sing up");

                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.
                    Log.e("UserProfile", "================================================================");
                    mWebview.loadUrl("javascript:sns_login_return(\""+userProfile.getId()+"\",\"kk\");");

                }
            });

        }
        // 세션 실패시
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            exception.printStackTrace();
        }
    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w(TAG, "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }

    public void setpBar(ProgressBar pBar) {
        this.pBar = pBar;
    }

    public ProgressBar getpBar() {
        return pBar;
    }

}
