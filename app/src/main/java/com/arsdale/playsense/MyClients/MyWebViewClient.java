package com.arsdale.playsense.MyClients;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.arsdale.playsense.MainActivity;

import static com.arsdale.playsense.MainActivity.GOOGLE_PLAY_STORE_PREFIX;
import static com.arsdale.playsense.MainActivity.INTENT_PROTOCOL_START;

public class MyWebViewClient extends WebViewClient {

    private MainActivity mMainActivity;

    public MyWebViewClient(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        //super.onReceivedError(view, request, error);

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(INTENT_PROTOCOL_START)) {
            final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
            final int customUrlEndIndex = url.indexOf(mMainActivity.INTENT_PROTOCOL_INTENT);
            if (customUrlEndIndex < 0) {
                return false;
            } else {
                final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                try {
                    mMainActivity.getBaseContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } catch (ActivityNotFoundException e) {
                    final int packageStartIndex = customUrlEndIndex + mMainActivity.INTENT_PROTOCOL_INTENT.length();
                    final int packageEndIndex = url.indexOf(mMainActivity.INTENT_PROTOCOL_END);

                    final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                    mMainActivity.getBaseContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
                }
                return true;
            }

        } else {
            return false;
        }

    }

}
