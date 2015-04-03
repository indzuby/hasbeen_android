package co.hasBeen.search;

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

import co.hasBeen.model.api.User;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-27.
 */
public class SearchPeopleAsyncTask extends HasBeenAsyncTask<Object,Void,List<User>> {
    final static String URL = Session.DOMAIN+"search/users";
    public SearchPeopleAsyncTask(Handler mHandler) {
        super(mHandler);
    }
    @Override
    protected List<User> doInBackground(Object... params) {
        try {
            String url =URL+"?keyword="+params[1];
            if(params.length>=3)
                url+="&page="+params[2];
            uri = Uri.parse(url);
            HttpGet get = new HttpGet(uri.toString());
            get.addHeader("User-Agent","Android");
            get.addHeader("Content-Type","application/json");
            get.addHeader("Authorization","Bearer " +params[0]);
            response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                //Read the server response and attempt to parse it as JSON
                Reader reader = new InputStreamReader(content);
                List<User> users = JsonConverter.convertJsonToUserList(reader);
                content.close();
                return users;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<User> users) {
        super.onPostExecute(users);
        Message msg = mHandler.obtainMessage();
        if(users !=null) {
            msg.obj = users;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);
    }
}
