package com.arsdale.playsense.MyClients;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.arsdale.playsense.MainActivity;

public class MyWebChromeClient extends WebChromeClient {

    private Activity mActivity;
    private MainActivity mMainActivity;

    private View mCustomView;

    private int mOriginalOrientation;
    private FullscreenHolder mFullscreenContainer;
    private CustomViewCallback mCustomViewCollback;


    public MyWebChromeClient(Activity activity, MainActivity mainActivity) {
        this.mActivity = activity;
        this.mMainActivity = mainActivity;
    }


    @Override
    public void onProgressChanged(WebView view, final int newProgress) {
       // super.onProgressChanged(view, newProgress);
        Log.d("tag","progress: "+newProgress);
        //mActivity.setProgress(newProgress*100);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (newProgress < 100) {
                    mMainActivity.getpBar().setVisibility(View.VISIBLE);
                    mMainActivity.getpBar().setProgress(newProgress);
                }else {
                    mMainActivity.getpBar().setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {

        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }

        mOriginalOrientation = mActivity.getRequestedOrientation();
        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
        mFullscreenContainer = new FullscreenHolder(mActivity);
        mFullscreenContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT);
        decor.addView(mFullscreenContainer, ViewGroup.LayoutParams.MATCH_PARENT);

        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mCustomView = view;
        setFullscreen(true);
        mCustomViewCollback = callback;
       // mActivity.setRequestedOrientation(mOriginalOrientation);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {

        if (mCustomView == null) {
            return;
        }

        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
        setFullscreen(false);
        decor.removeView(mFullscreenContainer);
        mFullscreenContainer = null;
        mCustomView = null;
        mCustomViewCollback.onCustomViewHidden();
        //mActivity.setRequestedOrientation(mOriginalOrientation);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    private void setFullscreen(boolean enabled) {
        Window win = mActivity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (enabled) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
            if (mCustomView != null) {
                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        win.setAttributes(winParams);
    }

    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }


}
