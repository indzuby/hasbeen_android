package example.test.hasBeen.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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

import example.test.hasBeen.model.database.Day;
import example.test.hasBeen.model.database.Photo;
import example.test.hasBeen.model.database.Place;
import example.test.hasBeen.model.database.Position;

/**
 * Created by zuby on 2015-01-13.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "hasBeen";
    private static final int DATABASE_VERSION = 1;

    private Dao<Photo,Long> photos;
    private Dao<Place,Long> place;
    private Dao<Day,Long> day;
    private Dao<Position,Long> position;
    private Context mContext;
    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME, null  , DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            TableUtils.createTable(connectionSource, Photo.class);
            TableUtils.createTable(connectionSource, Place.class);
            TableUtils.createTable(connectionSource, Position.class);
            TableUtils.createTable(connectionSource, Day.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try{
            TableUtils.dropTable(connectionSource, Photo.class, true);
            TableUtils.dropTable(connectionSource, Place.class, true);
            TableUtils.dropTable(connectionSource, Position.class, true);
            TableUtils.dropTable(connectionSource, Day.class, true);
            onCreate(database,connectionSource);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void clearTable(){

        mContext.deleteDatabase("hasBeen");
    }
    public Dao<Photo,Long> getPhotoDao() throws SQLException{
        if(photos==null)
            photos = getDao(Photo.class);

        return photos;
    }
    public Dao<Day,Long> getDayDao() throws SQLException{
        if(day==null)
            day = getDao(Day.class);

        return day;
    }
    public Dao<Place,Long> getPlaceDao() throws SQLException{
        if(place==null)
            place = getDao(Place.class);
        return place;
    }
    public Dao<Position,Long> getPositionDao() throws SQLException{
        if(position==null)
            position = getDao(Position.class);
        return position;
    }
    public Photo getLastPhoto() throws SQLException{
        if(photos==null)
            photos = getPhotoDao();
        if(photos.countOf()==0)
            return null;
        return photos.query(photos.queryBuilder().orderBy("id",false).limit(1L).prepare()).get(0);
    }
    public Long insertPhoto(Photo photo) throws SQLException{
        Dao<Photo,Long> photoDao = getPhotoDao();
        photoDao.create(photo);
        return photoDao.extractId(photo);
    }
    public Long insertPlace(Place place) throws SQLException{
        Dao<Place,Long> placeDao = getPlaceDao();
        placeDao.create(place);
        return placeDao.extractId(place);
    }
    public Long insertPosition(Position position) throws SQLException{
        Dao<Position,Long> positionDao = getPositionDao();
        positionDao.create(position);
        return positionDao.extractId(position);
    }
    public Long insertDay(Day day) throws SQLException{
        Dao<Day,Long> dayDao = getDayDao();
        dayDao.create(day);
        return dayDao.extractId(day);
    }
    public boolean hasVenueId(String venueId) throws SQLException{
        Dao<Place,Long> placeDao = getPlaceDao();
        if(placeDao.queryForEq("venue_id",venueId).size()==0) return false;
        return true;
    }
    public  void updatePhoto(Photo photo) throws SQLException{
        Dao<Photo,Long> photoDao = getPhotoDao();
        photoDao.update(photo);
    }
    public  void updateDay(Day day) throws SQLException{
        Dao<Day,Long> dayDao = getDayDao();
        dayDao.update(day);
    }
    public void updatePosition(Position position) throws SQLException {
        Dao<Position,Long> positionDao = getPositionDao();
        positionDao.update(position);
    }

    public void updateDayMainPhotoId(Long dayId,Long photoId) throws SQLException {
        Day day = selectDay(dayId);
        day.setMainPhotoId(photoId);
        updateDay(day);
    }
    public void updatePositionMainPhotoId(Long positionId,Long photoId) throws SQLException {
        Position position = selectPosition(positionId);
        position.setMainPhotoId(photoId);
        updatePosition(position);
    }
    public void updatePositionEndTime(Long positionId, Long endTime) throws SQLException {
        Position position = selectPosition(positionId);
        position.setEndDate(endTime);
        updatePosition(position);
    }
    public Long getPlaceIdByVenueId(String venueId) throws SQLException{
        Dao<Place,Long> placeDao = getPlaceDao();
        return placeDao.queryForEq("venue_id",venueId).get(0).getId();
    }
    public List<Photo> selectAllPhoto() throws  SQLException{
        Dao<Photo,Long> photoDao = getPhotoDao();
        return photos.queryBuilder().orderBy("id",true).query();
    }
    public boolean hasPhotoId(Long photoId) throws SQLException{
        Dao<Photo,Long> photoDao = getPhotoDao();
        if(photoDao.queryForEq("photo_id",photoId).size()!=0) return true;
        return false;
    }
    public void increasePhotoCount(Long dayId) throws SQLException{
        Dao<Day,Long> dayDao = getDayDao();
        Day day = selectDay(dayId);
        day.setPhotoCount(day.getPhotoCount()+1);
        dayDao.update(day);
    }
    public Day selectDay(Long dayId) throws SQLException {
        Dao<Day,Long> dayDao = getDayDao();
        return dayDao.queryForId(dayId);
    }
    public Position selectPosition(Long positionId) throws SQLException {
        Dao<Position,Long> PositionDao = getPositionDao();
        return PositionDao.queryForId(positionId);
    }
    public boolean hasMainPhotoIdInDay(Long dayId) throws  SQLException{
        Day day = selectDay(dayId);
        if(day.getMainPhotoId()==null) return false;
        return true;
    }
    public boolean hasMainPhotoIdInPosition(Long positionId) throws  SQLException{
        Position position= selectPosition(positionId);
        if(position.getMainPhotoId()==null) return false;
        return true;
    }
    public List<Photo> selectPhotoByPositionId(Long positionId) throws  SQLException{
        Dao<Photo,Long> photoDao = getPhotoDao();
        return photoDao.queryBuilder().orderBy("id",true).where().eq("position_id",positionId).and().eq("clearest_id", new ColumnArg("id")).query();
    }
    public List<Photo> selectPhotoClearestPhoto() throws SQLException {
        Dao<Photo,Long> photoDao = getPhotoDao();
        return photoDao.queryBuilder().orderBy("id",false).where().eq("clearest_id",new ColumnArg("id")).query();
    }
    public List<Day> selectBeforeFiveDay() throws SQLException {
        Dao<Day,Long> dayDao = getDayDao();
        Date date = new LocalDateTime(getLastPhoto().getTakenDate()).minusDays(5).toDate();
        return dayDao.queryBuilder().orderBy("id",false).where().ge("date", date.getTime()).query();
    }
    public List<Position> selectPositionByDayId(Long dayId) throws SQLException{
        Dao<Position,Long> positionDao = getPositionDao();

        return positionDao.queryBuilder().orderBy("id", true).where().eq("day_id",dayId).query();
    }
    public int selectPhotoByDayid(Long dayId) throws SQLException{
        List<Position> positions = selectPositionByDayId(dayId);
        Iterator iterator = positions.iterator();
        int photoCnt=0;
        while(iterator.hasNext())
            photoCnt += Math.ceil(selectPhotoByPositionId(((Position)iterator.next()).getId()).size()/3.0);
        return photoCnt;
    }
    public String selectPlaceNameByPlaceId(Long placeId) throws SQLException {
        return selectPlace(placeId).getName();
    }
    public Photo selectPhoto(Long photoId) throws SQLException {
        Dao<Photo,Long> photoDao = getPhotoDao();
        return photos.queryForId(photoId);
    }
    public Place selectPlace(Long placeId) throws SQLException {
        Dao<Place,Long> placeDao = getPlaceDao();
        return place.queryForId(placeId);
    }
    public void updatePhotosPlaceId(Long positionId, Long placeId) throws SQLException {
        Dao<Photo,Long> photoDao = getPhotoDao();
        UpdateBuilder<Photo,Long> updateBuilder = photoDao.updateBuilder();
        updateBuilder.updateColumnValue("place_id",placeId).where().eq("position_id",positionId);
        updateBuilder.update();
    }
}
//select * from photo where id = clearest_id
