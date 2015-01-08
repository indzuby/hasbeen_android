package example.test.hasBeen;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.BaseAdapter;

import java.util.Hashtable;
import java.util.Stack;

/**
 * Created by zuby on 2015-01-05.
 */
public class ImageLoader {

    Hashtable<Integer, Bitmap> loadImages;
    Hashtable<Integer, String> positionRequested;
    BaseAdapter listener;
    int runningCount = 0;
    Stack<ItemPair> queue;
    ContentResolver resolver;
    final int imgSize = 128;
    public ImageLoader(ContentResolver r) {

        loadImages = new Hashtable<Integer, Bitmap>();
        positionRequested = new Hashtable<Integer, String>();
        queue = new Stack<ItemPair>();
        resolver = r;

    }

    public void setListener(BaseAdapter adapter) {

        listener = adapter;
        reset();
    }

    public void reset() {

        positionRequested.clear();
        runningCount = 0;
        queue.clear();
    }

    public Bitmap getImage(int uid, String path) {

        Bitmap image = loadImages.get(uid);
        if( image != null )
            return image;
        if( !positionRequested.containsKey(uid) ) {

            positionRequested.put(uid, path);
            if( runningCount >= 50 ) {

                queue.push(new ItemPair(uid, path));
            }
            else {

                runningCount++;
                new LoadImageAsyncTask().execute(uid, path);
            }
        }
        return null;
    }

    public void getNextImage() {

        if( !queue.isEmpty() ) {

            ItemPair item = queue.pop();
            new LoadImageAsyncTask().execute(item.uid, item.path);
        }
    }

    public class LoadImageAsyncTask extends AsyncTask<Object, Void, Bitmap> {

        Integer uid;

        @Override
        protected Bitmap doInBackground(Object... params) {

            this.uid = (Integer) params[0];
            String path = (String) params[1];
            String[] proj = { MediaStore.Images.Thumbnails.DATA };

            Bitmap micro = MediaStore.Images.Thumbnails.getThumbnail(resolver, uid, MediaStore.Images.Thumbnails.MICRO_KIND, null);
//            micro = null;
            if( micro != null ) {

                return micro;
            }
            else {
                Cursor mini = MediaStore.Images.Thumbnails.queryMiniThumbnail(resolver, uid, MediaStore.Images.Thumbnails.MINI_KIND, proj);
                if( mini != null && mini.moveToFirst() ) {

                    path = mini.getString(mini.getColumnIndex(proj[0]));
                }
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = 1;
            if( options.outWidth > imgSize ) {

                int ws = options.outWidth / imgSize + 1;
                if( ws > options.inSampleSize )
                    options.inSampleSize = ws;
            }
            if( options.outHeight > imgSize ) {

                int hs = options.outHeight / imgSize + 1;
                if( hs > options.inSampleSize )
                    options.inSampleSize = hs;
            }
            Bitmap src = BitmapFactory.decodeFile(path, options);
            int a=src.getHeight(),b=src.getWidth();
            if(src.getWidth()>=src.getHeight()) {
                a = src.getWidth();
                b = src.getHeight();
                return Bitmap.createBitmap(src,a/2-b/2,0,b,b);
            }
            return Bitmap.createBitmap(src,0,a/2-b/2,b,b);
            //return src.createScaledBitmap(src,src.getHeight(),src.getHeight(),true);
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            runningCount--;
            if( result != null ) {

                loadImages.put(uid, result);
                listener.notifyDataSetChanged();
                getNextImage();
            }
        }
    }

    public static class ItemPair {

        Integer uid;
        String path;

        public ItemPair(Integer uid, String path) {

            this.uid = uid;
            this.path = path;
        }
    }
}