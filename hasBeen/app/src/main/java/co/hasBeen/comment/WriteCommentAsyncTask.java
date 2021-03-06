package co.hasBeen.comment;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import co.hasBeen.model.api.Comment;
import co.hasBeen.utils.HasBeenAsyncTask;
import co.hasBeen.utils.JsonConverter;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-02-06.
 */
public class WriteCommentAsyncTask extends HasBeenAsyncTask<Object,Void,Comment> {
    final static String URL = Session.DOMAIN;
    @Override
    protected Comment doInBackground(Object... params) {
        try {
            uri = Uri.parse(URL+params[1]+"/"+params[2]+"/comments");
            HttpPost post = new HttpPost(uri.toString());
            post.addHeader("User-Agent","Android");
            post.addHeader("Content-Type","application/json");
            post.addHeader("Authorization","Bearer " +params[0]);
            JSONObject param = new JSONObject();
            param.put("contents",params[3]);
            post.setEntity(new StringEntity(param.toString(), HTTP.UTF_8));
            response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 201) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                //Read the server response and attempt to parse it as JSON
                Reader reader = new InputStreamReader(content);

                Comment comment = JsonConverter.convertJsonToComment(reader);
                content.close();
                return comment;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Comment comment) {
        Message msg = mHandler.obtainMessage();
        if(comment !=null) {
            msg.obj = comment;
            msg.what = 0;
        }else {
            msg.what = -1;
        }
        mHandler.sendMessage(msg);

    }

    public WriteCommentAsyncTask(Handler mHandler) {
        super(mHandler);
    }
}
