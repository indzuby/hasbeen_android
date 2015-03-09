package co.hasBeen.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 주현 on 2015-03-09.
 */
public class GcmRegister {
    private String TAG = "GcmRegister";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String SENDER_ID = "996576834855";
    String regid;
    Context mContext;
    Handler mHandler;
    public GcmRegister(Context mContext) {
        this.mContext = mContext;
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity)mContext,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                ((Activity)mContext).finish();
            }
            return false;
        }
        return true;
    }

    public void registerGcm(Handler handler) {
        mHandler = handler;
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(mContext);
            regid = getRegistrationId();

            if (regid.isEmpty()) {
                registerInBackground();
            }else {
                Message message = mHandler.obtainMessage();
                message.obj = regid;
                message.what = 0;
                mHandler.sendMessage(message);
                Log.i(TAG, regid);
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }
    public String getRegistrationId() {
        String registrationId = co.hasBeen.utils.Session.getString(mContext,PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = co.hasBeen.utils.Session.getInt(mContext, PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(mContext);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    private void registerInBackground()
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                String msg = "";
                try
                {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(mContext);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(regid);
                }
                catch (IOException ex)
                {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg)
            {
                Log.i(TAG, regid);
                Message message = mHandler.obtainMessage();
                message.obj = msg;
                message.what = 0;
                mHandler.sendMessage(message);
            }
        }.execute(null, null, null);
    }
    private void storeRegistrationId(String regId) {
        int appVersion = getAppVersion(mContext);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        co.hasBeen.utils.Session.putString(mContext, PROPERTY_REG_ID, regId);
        co.hasBeen.utils.Session.putInt(mContext, PROPERTY_APP_VERSION, appVersion);
    }
}
