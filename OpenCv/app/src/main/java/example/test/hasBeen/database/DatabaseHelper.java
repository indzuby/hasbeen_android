package example.test.hasBeen.database;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.ColumnArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.joda.time.LocalDateTime;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
    public  void updateDay(HasBeenDay day) throws SQLException{
        Dao<HasBeenDay,Long> dayDao = getDayDao();
        dayDao.update(day);
    }
    public void updatePosition(HasBeenPosition position) throws SQLException {
        Dao<HasBeenPosition,Long> positionDao = getPositionDao();
        positionDao.update(position);
    }

    public void updateDayMainPhotoId(Long dayId,Long photoId) throws SQLException {
        HasBeenDay day = selectDay(dayId);
        day.setMainPhotoId(photoId);
        updateDay(day);
    }
    public void updatePositionMainPhotoId(Long positionId,Long photoId) throws SQLException {
        HasBeenPosition position = selectPosition(positionId);
        position.setMainPhotoId(photoId);
        updatePosition(position);
    }
    public void updatePositionEndTime(Long positionId, Date endTime) throws SQLException {
        HasBeenPosition position = selectPosition(positionId);
        position.setEndDate(endTime);
        updatePosition(position);
    }
    public Long getPlaceIdByVenueId(String venueId) throws SQLException{
        Dao<HasBeenPlace,Long> placeDao = getPlaceDao();
        return placeDao.queryForEq("venue_id",venueId).get(0).getId();
    }
    public List<HasBeenPhoto> selectAllPhoto() throws  SQLException{
        Dao<HasBeenPhoto,Long> photoDao = getPhotoDao();
        return photos.queryBuilder().orderBy("id",true).query();
    }
    public boolean hasPhotoId(Long photoId) throws SQLException{
        Dao<HasBeenPhoto,Long> photoDao = getPhotoDao();
        if(photoDao.queryForEq("photo_id",photoId).size()!=0) return true;
        return false;
    }
    public void increasePhotoCount(Long dayId) throws SQLException{
        Dao<HasBeenDay,Long> dayDao = getDayDao();
        HasBeenDay day = selectDay(dayId);
        day.setPhotoCount(day.getPhotoCount()+1);
        dayDao.update(day);
    }
    public HasBeenDay selectDay(Long dayId) throws SQLException {
        Dao<HasBeenDay,Long> dayDao = getDayDao();
        return dayDao.queryForId(dayId);
    }
    public HasBeenPosition selectPosition(Long positionId) throws SQLException {
        Dao<HasBeenPosition,Long> PositionDao = getPositionDao();
        return PositionDao.queryForId(positionId);
    }
    public boolean hasMainPhotoIdInDay(Long dayId) throws  SQLException{
        HasBeenDay day = selectDay(dayId);
        if(day.getMainPhotoId()==null) return false;
        return true;
    }
    public boolean hasMainPhotoIdInPosition(Long positionId) throws  SQLException{
        HasBeenPosition position= selectPosition(positionId);
        if(position.getMainPhotoId()==null) return false;
        return true;
    }
    public List<HasBeenPhoto> selectPhotoByPositionId(Long positionId) throws  SQLException{
        Dao<HasBeenPhoto,Long> photoDao = getPhotoDao();
        return photoDao.queryBuilder().orderBy("id",true).where().eq("position_id",positionId).and().eq("clearest_id", new ColumnArg("id")).query();
    }
    public List<HasBeenPhoto> selectPhotoClearestPhoto() throws SQLException {
        Dao<HasBeenPhoto,Long> photoDao = getPhotoDao();
        return photoDao.queryBuilder().orderBy("id",false).where().eq("clearest_id",new ColumnArg("id")).query();
    }
    public List<HasBeenDay> selectBeforeFiveDay() throws SQLException {
        Dao<HasBeenDay,Long> dayDao = getDayDao();
        Date date = new LocalDateTime(getLastPhoto().getTakenDate()).minusDays(5).toDate();
        return dayDao.queryBuilder().orderBy("id",false).where().ge("date", date).query();
    }
    public List<HasBeenPosition> selectPositionByDayId(Long dayId) throws SQLException{
        Dao<HasBeenPosition,Long> positionDao = getPositionDao();

        return positionDao.queryBuilder().orderBy("id", true).where().eq("day_id",dayId).query();
    }
    public int selectPhotoByDayid(Long dayId) throws SQLException{
        List<HasBeenPosition> positions = selectPositionByDayId(dayId);
        Iterator iterator = positions.iterator();
        int photoCnt=0;
        while(iterator.hasNext())
            photoCnt += Math.ceil(selectPhotoByPositionId(((HasBeenPosition)iterator.next()).getId()).size()/3.0);
        return photoCnt;
    }
    public String selectPlaceNameByPlaceId(Long placeId) throws SQLException {
        return selectPlace(placeId).getName();
    }
    public HasBeenPhoto selectPhoto(Long photoId) throws SQLException {
        Dao<HasBeenPhoto,Long> photoDao = getPhotoDao();
        return photos.queryForId(photoId);
    }
    public HasBeenPlace selectPlace(Long placeId) throws SQLException {
        Dao<HasBeenPlace,Long> placeDao = getPlaceDao();
        return place.queryForId(placeId);
    }
    public void updatePhotosPlaceId(Long positionId, Long placeId) throws SQLException {
        Dao<HasBeenPhoto,Long> photoDao = getPhotoDao();
        UpdateBuilder<HasBeenPhoto,Long> updateBuilder = photoDao.updateBuilder();
        updateBuilder.updateColumnValue("place_id",placeId).where().eq("position_id",positionId);
        updateBuilder.update();
    }
}
//select * from photo where id = clearest_id
