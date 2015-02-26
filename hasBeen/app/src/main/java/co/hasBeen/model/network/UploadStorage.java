package co.hasBeen.model.network;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.LocalDate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;

import co.hasBeen.model.database.Day;
import co.hasBeen.model.database.Photo;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-02-13.
 */
public class UploadStorage {
    final static String USERS = "users";
    final static String MAP_URL = "http://maps.googleapis.com/maps/api/staticmap?size=480x240&scale=2&zoom=14&markers=icon:http://image.hasbeen.co/common/marker.png%7C";
    private CloudStorageAccount storageAccount;
    private CloudBlobClient blobClient;
    private BlobContainerPermissions publicContainerPermission;
    private String storageConnectionString = "DefaultEndpointsProtocol=http;AccountName=hasbeen;AccountKey=FenPsmedm1j68IlYj2zW5bg2aGV2BpHoqFZB6sjD7oBWOzoH5smWJthrwCraPMetEVlKebiD2Ge/WghmFZ7DVQ==";
    private Day mDay;
    Photo mPhoto;
    Context mContext;
    ContentResolver resolver;
    public int mPhotoCnt;
    final int imgSize = 1080;
    public UploadStorage(Context context) {
        try {
            initStorageAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mContext = context;
        resolver = context.getContentResolver();
        mPhotoCnt = 0 ;
    }

    public void makeStaticMapUrl(Day day, Long userId) {
        mDay = day;
        new ImageUploadTask(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
                    mDay.setStaticMapUrl((String)msg.obj);
                }
            }
        }).execute("map", day.getMainPhoto(), userId);
    }
    public void makePhotoStorageUrl(Photo photo, Long userId) throws  Exception{
        mPhoto = photo;
        ExifInterface exif = new ExifInterface(photo.getPhotoPath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        new ImageUploadTask(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
//                    mPhoto.setSmallUrl((String)msg.obj);
                    mPhotoCnt++;
                }
            }
        }).execute("small",photo,userId,orientation);
        new ImageUploadTask(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
//                    mPhoto.setMediumUrl((String) msg.obj);
                    mPhotoCnt++;
                }
            }
        }).execute("medium", photo, userId,orientation);

        new ImageUploadTask(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
//                    mPhoto.setLargeUrl((String) msg.obj);
                    mPhotoCnt++;
                }
            }
        }).execute("large", photo, userId, orientation);
    }
    private void initStorageAccount() throws URISyntaxException, InvalidKeyException {
        storageAccount = CloudStorageAccount.parse(storageConnectionString);
        blobClient = storageAccount.createCloudBlobClient();

        publicContainerPermission = new BlobContainerPermissions();
        publicContainerPermission.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
    }

    protected Bitmap getMicroThumbnail(long id) {
        return MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
    }

    protected Bitmap getMediumThumbnail(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
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
        return BitmapFactory.decodeFile(path, options);
//        return MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
    }

    protected Bitmap getLargeThumbnail(String path) {
        return BitmapFactory.decodeFile(path);
    }
    public class ImageUploadTask extends AsyncTask<Object, Void, String> {
        Handler mHandler;
        int len;
        String mType;
        Photo mPhoto;
        public ImageUploadTask(Handler mHandler) {
            this.mHandler = mHandler;
        }
        protected InputStream staticMap(float lat, float lon) throws Exception{
            HttpClient client = new DefaultHttpClient();
            URL url = new URL(MAP_URL + lat + "," + lon);
            HttpGet request = new HttpGet(url.toString());
            InputStream in = client.execute(request).getEntity().getContent();
            Bitmap bmp =  BitmapFactory.decodeStream(in);
            in.close();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();
            ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
            len = bitmapdata.length;
            return bs;
        }
        protected InputStream photoStream(Bitmap bitmap, int orientation) throws  Exception{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();
            len = bitmapdata.length;
            ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
            return bs;
        }
        @Override
        protected String doInBackground(Object... params) {
            try {
                String type = (String) params[0];
                Photo photo = (Photo) params[1];
                Long userId = (Long) params[2];
                InputStream inputStream;
                LocalDate date = new LocalDate(photo.getTakenTime());
                mType = type;
                mPhoto = photo;
                String dir = userId + "/" + date.toString("YYYYMMdd");
                if(type.equals("map")) {
                    inputStream = staticMap(photo.getLat(), photo.getLon());
                    dir += "/staticMap.jpg";
                }else {
                    Long id = photo.getPhotoId();
                    int orientation = (int) params[3];
                    Bitmap bitmap = null;
                    if(type.equals("small"))
                        bitmap = getMicroThumbnail(id);
                    else if(type.equals("medium"))
                        bitmap = getMediumThumbnail(photo.getPhotoPath());
                    else if(type.equals("large"))
                        bitmap = getLargeThumbnail(photo.getPhotoPath());
                    dir += "/"+photo.getTakenTime()+"_"+type+".jpg";
                    bitmap = Util.rotateBitmap(bitmap,orientation);
                    if(type.equals("large")) {
                        photo.setWidth(bitmap.getWidth());
                        photo.setHeight(bitmap.getHeight());
                    }
                    inputStream = photoStream(bitmap, orientation);
                }
                return makeUrlToBlob(dir,inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected String makeUrlToBlob(String blockBlob,InputStream inputStream) throws Exception{
            CloudBlobContainer container = blobClient.getContainerReference(USERS);
            container.createIfNotExists();
            container.uploadPermissions(publicContainerPermission);
            CloudBlockBlob blob =  container.getBlockBlobReference(blockBlob);
            blob.getProperties().setContentType("image/jpeg");
            blob.upload(inputStream, len);
            return blob.getUri().toString();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Message msg = mHandler.obtainMessage();
            if(s!=null) {
                msg.what = 0;
                msg.obj = s;
                if(mType.equals("large"))
                    mPhoto.setLargeUrl(s);
                else if(mType.equals("medium"))
                    mPhoto.setMediumUrl(s);
                else if(mType.equals("small"))
                    mPhoto.setSmallUrl(s);

                mHandler.sendMessage(msg);
            }else {
                msg.what=-1;
                mHandler.sendMessage(msg);
            }
        }
    }
}
