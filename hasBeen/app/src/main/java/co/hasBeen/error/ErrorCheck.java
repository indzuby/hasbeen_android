package co.hasBeen.error;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import co.hasBeen.R;

/**
 * Created by 주현 on 2015-03-10.
 */
public class ErrorCheck {
    public static boolean NetworkOnline(Context context) { // network 연결 상태 확인
        try {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState(); // wifi
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                return true;
            }

            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState(); // mobile ConnectivityManager.TYPE_MOBILE
            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                return true;
            }

        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }
    public static void errorToast(Context context){
        Toast.makeText(context, context.getString(R.string.common_error), Toast.LENGTH_LONG).show();
    }
}
