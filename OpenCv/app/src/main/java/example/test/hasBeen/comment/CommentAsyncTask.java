package example.test.hasBeen.comment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import example.test.hasBeen.model.api.Comment;
import example.test.hasBeen.model.api.PhotoApi;
import example.test.hasBeen.utils.Session;

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


                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        if(f.getDeclaredClass() == PhotoApi.class){
                            if(f.getName().equals("day") || f.getName().equals("place"))
                                return true;
                        }
                        if(f.getName().equals("coverPhoto"))
                            return true;
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                }).create();
                Type listType = new TypeToken<List<Comment>>(){}.getType();
                List<Comment> comments = gson.fromJson(reader, listType);
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
