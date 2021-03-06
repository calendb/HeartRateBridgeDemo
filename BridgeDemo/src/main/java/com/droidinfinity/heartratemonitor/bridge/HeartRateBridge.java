package com.droidinfinity.heartratemonitor.bridge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Praveen Krishnan on 2/10/2018.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class HeartRateBridge {
    public final static String ACTION_MEASURE = "com.droidinfinity.heartratemonitor.MEASURE";
    public static final String KEY_CALLING_PACKAGE = "calling_package";

    private transient static final int REQUEST_MEASURE = 6437;
    private HeartRateListener heartRateListener;

    public interface HeartRateListener {
        void onHeartRateMeasured(int heartRate);
    }

    public void setHeartRateListener(HeartRateListener heartRateListener) {
        this.heartRateListener = heartRateListener;
    }

    public void openHeartRateBridge(Activity context) {
        final Intent intent = new Intent(ACTION_MEASURE);
        intent.putExtra(KEY_CALLING_PACKAGE, context.getApplicationContext().getPackageName());
        if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
            context.startActivityForResult(intent, REQUEST_MEASURE);
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEASURE) {
            int heartRate = -1;
            if (resultCode == Activity.RESULT_OK) {
                heartRate = extractHeartRateFromData(data);
            }

            if (heartRateListener != null) {
                heartRateListener.onHeartRateMeasured(heartRate);
                return true;
            }
        }

        return false;
    }

    /* Static Methods */
    public static int extractHeartRateFromData(Intent data) {
        if (data != null)
            return data.getIntExtra("heart_rate", -1);
        else
            return -1;
    }

    public static void startInstallIntent(Activity context) {
        final Uri uri = Uri.parse("market://details?id=com.droidinfinity.heartratemonitor&referrer=utm_source%3D" + context.getApplicationContext().getPackageName());
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static boolean isHeartRateMonitorInstalled(Context context) {
        final Intent intent = new Intent(ACTION_MEASURE);
        return context.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }
}
