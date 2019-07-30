package com.arsdale.playsense.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class AppChecker {
    private static final AppChecker ourInstance = new AppChecker();

    public static AppChecker getInstance() {
        return ourInstance;
    }

    private AppChecker() {
    }

    public boolean isPackageInstalled(String packageName, PackageManager packageManager) {

        boolean found = true;

        try {

            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {

            found = false;
        }

        return found;
    }

    public void openInGooglePlay(Activity activity, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id="+packageName));
        activity.startActivity(intent);
    }


}
