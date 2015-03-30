package co.hasBeen.database.Photo;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.util.List;

import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.model.api.Photo;

/**
 * Created by 주현 on 2015-03-13.
 */
public class DeleteAsyncTask extends AsyncTask<Object,Void,Void> {
    Long dayid;
    DatabaseHelper database;
    List<Photo> mPhotoList;
    Handler handler;

    public DeleteAsyncTask(Long dayid, DatabaseHelper database, Handler handler) {
        this.dayid = dayid;
        this.database = database;
        this.handler = handler;
    }

    @Override
    protected Void doInBackground(Object... params) {
        try {
            mPhotoList = database.selectClearestPhotosByDayId(dayid);
            for(Photo photo : mPhotoList) {
                if(isDeleted(photo.getPhotoPath())) {
                    if(isMainPhotoInDay(photo))
                        setNewMainPhotoInDay(photo);
                    if(isMainPhotoInPosition(photo))
                        setNewMainPositionDay(photo);
                    if(isClearestPhoto(photo))
                        setNewClearestPhoto(photo);
                    database.removePhoto(photo.getId());
                }
                if(isEmptyPosition(photo.getPositionId()))
                    database.removePosition(photo.getPositionId());
            }
            if(isEmptyDay(dayid))
                database.removeDay(dayid);
            Message msg = handler.obtainMessage();
            msg.what = 0 ;
            handler.sendMessage(msg);
        }catch (Exception e){
            e.printStackTrace();
            Message msg = handler.obtainMessage();
            msg.what = -1 ;
            handler.sendMessage(msg);
        }
        return null;
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
