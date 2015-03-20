package co.hasBeen.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

/**
 * Created by 주현 on 2015-03-18.
 */
public abstract class HasBeenAsyncTask<T1,T2,T3> extends AsyncTask<T1,T2,T3> {
    public Handler mHandler;
    public HttpClient client;
    public HttpResponse response;
    public Uri uri;

    public HasBeenAsyncTask(Handler mHandler) {
        this.mHandler = mHandler;
//        client = new DefaultHttpClient();
        client = SFSSLSocketFactory.getHttpClient();
    }

    @Override
    protected void onPostExecute(T3 t3) {
        if(isCancelled()) return;
    }

    protected HasBeenAsyncTask() {
    }
}
