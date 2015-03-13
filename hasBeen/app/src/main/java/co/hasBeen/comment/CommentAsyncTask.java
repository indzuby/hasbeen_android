package co.hasBeen.comment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import co.hasBeen.model.api.Comment;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-06.
 */
public class CommentAsyncTask extends AsyncTask<Object,Void,List<Comment>> {
    final static String URL = Session.DOMAIN;
    @Override
    protected List<Comment> doInBackground(Object... params) {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        Uri uri;
        try {
            String url = URL+params[1]+"/"+params[2]+"/comments";
            if(params.length>=4) {
                url+="?lastCommentId="+params[3];
            }
            uri = Uri.parse(url);
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
                Reader reader = new InputStreamReader(content);

                List<Comment> comments = JsonConverter.convertJsonToCommentList(reader);
                content.close();
                return comments;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Comment> comments) {
        Message msg = mHandler.obtainMessage();
        if(comments !=null) {
            msg.obj = comments;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);

    }
    Handler mHandler;

    public CommentAsyncTask(Handler mHandler) {
        this.mHandler = mHandler;
    }
}
