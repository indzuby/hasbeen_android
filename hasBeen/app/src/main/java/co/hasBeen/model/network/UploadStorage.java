package co.hasBeen.model.network;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;

import co.hasBeen.model.database.Day;
import co.hasBeen.model.database.Photo;

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
        String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);

        new ImageUploadTask(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
                    mPhoto.setSmallUrl((String)msg.obj);
                    mPhotoCnt++;
                }
            }
        }).execute("small",photo,userId,photo.getPhotoId(),orientation);
        String url = getMiniThumbnail(photo.getPhotoId());
        new ImageUploadTask(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
                    mPhoto.setMediumUrl((String) msg.obj);
                    mPhotoCnt++;
                }
            }
        }).execute("medium", photo, userId, url,orientation);

        new ImageUploadTask(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
                    mPhoto.setLargeUrl((String) msg.obj);
                    mPhotoCnt++;
                }
            }
        }).execute("large", photo, userId, photo.getPhotoPath(),orientation);
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

    protected String getMiniThumbnail(long id) {
        Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                resolver, id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null);
        if( cursor != null && cursor.getCount() > 0 ) {
            cursor.moveToFirst();//**EDIT**
            String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            return uri;
        }
        return null;
    }

    public class ImageUploadTask extends AsyncTask<Object, Void, String> {
        Handler mHandler;
        int len;
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
        protected InputStream photoFile(String photoPath,String orientation) throws  Exception{
            ExifInterface exif = new ExifInterface(photoPath);

            exif.setAttribute(ExifInterface.TAG_ORIENTATION,orientation);
            exif.saveAttributes();
            File photoFile = new File(photoPath);

            len = (int) photoFile.length();
            return new FileInputStream(photoFile);
        }
        protected InputStream photoSmallFile(Long photoid,String orientation) throws Exception{
            Bitmap bitmap = getMicroThumbnail(photoid);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();
            File tempfile = File.createTempFile(photoid+"_small","jpg");
            FileOutputStream fos = new FileOutputStream(tempfile);
            fos.write(bitmapdata);
            ExifInterface exif = new ExifInterface(tempfile.getAbsolutePath());
            exif.setAttribute(ExifInterface.TAG_ORIENTATION,orientation);
            exif.saveAttributes();
            FileInputStream inputStream = new FileInputStream(tempfile);
            len = (int) tempfile.length();
            tempfile.delete();
            return inputStream;
        }
        @Override
        protected String doInBackground(Object... params) {
            try {
                String type = (String) params[0];
                Photo photo = (Photo) params[1];
                Long userId = (Long) params[2];
                InputStream inputStream;
                LocalDate date = new LocalDate(photo.getTakenTime());
                String dir = userId + "/" + date.toString("YYYYMMdd");
                if(type.equals("map")) {
                    inputStream = staticMap(photo.getLat(), photo.getLon());
                    dir += "/staticMap.jpg";
                }else if(!type.equals("small")){
                    String photoPath = (String) params[3];
                    String orientation = (String) params[4];
                    inputStream = photoFile(photoPath,orientation);
                    dir += "/"+photo.getTakenTime()+"_"+type+".jpg";
                }else {
                    Long id = (Long) params[3];
                    String orientation = (String) params[4];
                    dir += "/"+photo.getTakenTime()+"_"+type+".jpg";
                    inputStream = photoSmallFile(id,orientation);
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
                mHandler.sendMessage(msg);
            }else {
                msg.what=-1;
                mHandler.sendMessage(msg);
            }
        }
    }
}
