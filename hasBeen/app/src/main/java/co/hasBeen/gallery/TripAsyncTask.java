package co.hasBeen.gallery;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import co.hasBeen.model.api.Trip;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-17.
 */
public class TripAsyncTask extends HasBeenAsyncTask<Object,Void,List<Trip>> {
    final static String URL= Session.DOMAIN+"/users/";
    @Override
    protected List<Trip> doInBackground(Object... params) {
        try {
            uri = Uri.parse(URL+params[1]+"/trips");
            HttpGet get = new HttpGet(uri.toString());
            get.addHeader("User-Agent","Android");
            get.addHeader("Content-Type","application/json");
            get.addHeader("Authorization","Bearer " +params[0]);
            response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                //Read the server response and attempt to parse it as JSON
                //Read the server response and attempt to parse it as JSON
                Reader reader = new InputStreamReader(content);

                List<Trip> trips = JsonConverter.convertJsonToTripList(reader);
                content.close();
                return trips;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Trip> trips) {
        super.onPostExecute(trips);
        Message msg = mHandler.obtainMessage();
        if(trips !=null) {
            msg.obj = trips;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }


    public TripAsyncTask(Handler mHandler) {
        super(mHandler);
    }
}
