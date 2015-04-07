package co.hasBeen.database.Photo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.util.List;

import co.hasBeen.database.DataBaseHelper;
import co.hasBeen.model.api.Photo;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-13.
 */
public class PhotoSyncThread extends Thread {

    Long dayid;
    DataBaseHelper database;
    List<Photo> mPhotoList;
    Handler handler;
    boolean isCancel = false;
    Context mContext;
    public PhotoSyncThread(Long dayid, Context context, Handler handler) {
        this.dayid = dayid;
        mContext = context;
        database = new DataBaseHelper(context);
        this.handler = handler;
    }
    public void cancel(){
        isCancel = true;
    }
    @Override
    public void run() {
        boolean flag;
        while(!isCancel) {
            try {
                flag = false;
//                mPhotoList = database.selectPhotosByDayId(dayid);
//                for (Photo photo : mPhotoList) {
//                    if (isDeleted(photo.getPhotoPath())) {
//                        if (isMainPhotoInDay(photo))
//                            setNewMainPhotoInDay(photo);
//                        if (isMainPhotoInPosition(photo))
//                            setNewMainPositionDay(photo);
//                        database.removePhoto(photo.getId());
//                        flag = true;
//                    }
//                    if (isEmptyPosition(photo.getPositionId()))
//                        database.removePosition(photo.getPositionId());
//                }
//                if (isEmptyDay(dayid))
//                    database.removeDay(dayid);
                boolean modfiy = Session.getBoolean(mContext, "modifyDay" + dayid, false);
                if(modfiy) {
                    Session.putBoolean(mContext, "modifyDay" + dayid, false);
                    flag = true;
                }
                if(flag) {
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
                Message msg = handler.obtainMessage();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        }
    }
    protected boolean isDeleted(String photoPath) {
        File file = new File(photoPath);
        return !file.exists();
    }
    protected boolean isMainPhotoInDay(Photo photo) throws Exception{
        Long id = database.selectDay(photo.getDayId()).getMainPhotoId();
        return (id.equals(photo.getId()));
    }
    protected boolean isMainPhotoInPosition(Photo photo) throws Exception {
        Long id = database.selectPosition(photo.getPositionId()).getMainPhotoId();
        return (id.equals(photo.getId()));
    }
    protected boolean isClearestPhoto(Photo photo){
        return (photo.getId().equals(photo.getClearestId()));
    }
    protected void setNewMainPhotoInDay(Photo photo) throws Exception{
        for(Photo  newPhoto : mPhotoList) {
            if(!photo.getId().equals(newPhoto.getId())) {
                database.updateDayMainPhotoId(dayid, newPhoto.getId());
                break;
            }
        }
    }
    protected void setNewMainPositionDay(Photo photo) throws Exception {
        List<Photo> photoList = database.selectPhotoByPositionId(photo.getPositionId());
        for(Photo newPhoto : photoList) {
            if(!newPhoto.getId().equals(photo.getId())) {
                database.updatePositionMainPhotoId(photo.getPositionId(), newPhoto.getId());
                break;
            }
        }
    }
    protected void setNewClearestPhoto(Photo photo) throws Exception {
        List<Photo> photoList = database.selectClearestPhoto(photo.getId());
        int max = 0;
        Long newCleaerestId = photo.getId();
        for(Photo  newPhoto : photoList){
            if(photo.getId().equals(newPhoto.getId())) continue;
            if(!isDeleted(newPhoto.getPhotoPath())) {
                if(max<newPhoto.getEdgeCount()) {
                    max = newPhoto.getEdgeCount();
                    newCleaerestId = newPhoto.getId();
                }
            }else
                database.removePhoto(newPhoto.getId());
        }
        database.updateClearestId(photo.getId(),newCleaerestId);
    }
    protected boolean isEmptyPosition(Long positionid) throws Exception{
        return database.isEmptyPosition(positionid);
    }
    protected boolean isEmptyDay(Long dayid) throws Exception{
        return database.isEmptyDay(dayid);
    }
}
