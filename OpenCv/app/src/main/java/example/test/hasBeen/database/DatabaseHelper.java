package example.test.hasBeen.database;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Iterator;

import example.test.hasBeen.model.HasBeenDay;
import example.test.hasBeen.model.HasBeenPhoto;
import example.test.hasBeen.model.HasBeenPlace;
import example.test.hasBeen.model.HasBeenPosition;

/**
 * Created by zuby on 2015-01-13.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "hasBeen";
    private static final int DATABASE_VERSION = 1;

    private Dao<HasBeenPhoto,Long> photos;
    private Dao<HasBeenPlace,Long> place;
    private Dao<HasBeenDay,Long> day;
    private Dao<HasBeenPosition,Long> position;
    private Context mContext;
    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME, null  , DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            TableUtils.createTable(connectionSource, HasBeenPhoto.class);
            TableUtils.createTable(connectionSource, HasBeenPlace.class);
            TableUtils.createTable(connectionSource, HasBeenPosition.class);
            TableUtils.createTable(connectionSource, HasBeenDay.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try{
            TableUtils.dropTable(connectionSource, HasBeenPhoto.class, true);
            TableUtils.dropTable(connectionSource, HasBeenPlace.class, true);
            TableUtils.dropTable(connectionSource, HasBeenPosition.class, true);
            TableUtils.dropTable(connectionSource, HasBeenDay.class, true);
            onCreate(database,connectionSource);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void clearTable(){

        mContext.deleteDatabase("hasBeen");
    }
    public Dao<HasBeenPhoto,Long> getPhotoDao() throws SQLException{
        if(photos==null)
            photos = getDao(HasBeenPhoto.class);

        return photos;
    }
    public Dao<HasBeenDay,Long> getDayDao() throws SQLException{
        if(day==null)
            day = getDao(HasBeenDay.class);

        return day;
    }
    public Dao<HasBeenPlace,Long> getPlaceDao() throws SQLException{
        if(place==null)
            place = getDao(HasBeenPlace.class);
        return place;
    }
    public Dao<HasBeenPosition,Long> getPositionDao() throws SQLException{
        if(position==null)
            position = getDao(HasBeenPosition.class);
        return position;
    }
    public HasBeenPhoto getLastPhoto() throws SQLException{
        if(photos==null)
            photos = getPhotoDao();
        if(photos.countOf()==0)
            return null;
        return photos.query(photos.queryBuilder().orderBy("id",false).limit(1L).prepare()).get(0);
    }
    public Long insertPhoto(HasBeenPhoto photo) throws SQLException{
        Dao<HasBeenPhoto,Long> photoDao = getPhotoDao();
        photoDao.create(photo);
        return photoDao.extractId(photo);
    }
    public Long insertPlace(HasBeenPlace place) throws SQLException{
        Dao<HasBeenPlace,Long> placeDao = getPlaceDao();
        placeDao.create(place);
        return placeDao.extractId(place);
    }
    public Long insertPosition(HasBeenPosition position) throws SQLException{
        Dao<HasBeenPosition,Long> positionDao = getPositionDao();
        positionDao.create(position);
        return positionDao.extractId(position);
    }
    public Long insertDay(HasBeenDay day) throws SQLException{
        Dao<HasBeenDay,Long> dayDao = getDayDao();
        dayDao.create(day);
        return dayDao.extractId(day);
    }
    public boolean hasVenueId(String venueId) throws SQLException{
        Dao<HasBeenPlace,Long> placeDao = getPlaceDao();
        if(placeDao.queryForEq("venue_id",venueId).size()==0) return false;
        return true;
    }
    public  void updatePhoto(HasBeenPhoto photo) throws SQLException{
        Dao<HasBeenPhoto,Long> photoDao = getPhotoDao();
        photoDao.update(photo);
    }
    public Long getPlaceIdByVenueId(String venueId) throws SQLException{
        Dao<HasBeenPlace,Long> placeDao = getPlaceDao();
        return placeDao.queryForEq("venue_id",venueId).get(0).getId();
    }

}
