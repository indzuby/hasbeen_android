package co.hasBeen.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.ColumnArg;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.joda.time.LocalDateTime;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Place;
import co.hasBeen.model.api.Position;
import co.hasBeen.model.api.RecentSearch;

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
    private Dao<RecentSearch,Long> recent;
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
            TableUtils.createTable(connectionSource, RecentSearch.class);
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
            TableUtils.dropTable(connectionSource, RecentSearch.class, true);
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
    public Dao<RecentSearch,Long> getRecentDao() throws SQLException{
        if(recent==null)
            recent = getDao(RecentSearch.class);
        return recent;
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
        if(venueId==null || placeDao.queryForEq("venue_id",venueId).size()==0) return false;
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

    public void updateDayMainPlaceId(Long dayId,Long placeId) throws SQLException {
        Day day = selectDay(dayId);
        day.setMainPlaceId(placeId);
        updateDay(day);
    }
    public void updatePositionMainPhotoId(Long positionId,Long photoId) throws SQLException {
        Position position = selectPosition(positionId);
        position.setMainPhotoId(photoId);
        updatePosition(position);
    }
    public void updatePositionEndTime(Long positionId, Long endTime) throws SQLException {
        Position position = selectPosition(positionId);
        position.setEndTime(endTime);
        updatePosition(position);
    }
    public Long getPlaceIdByVenueId(String venueId) throws SQLException{
        Dao<Place,Long> placeDao = getPlaceDao();
        return placeDao.queryForEq("venue_id", venueId).get(0).getId();
    }
    public List<Photo> selectAllPhoto() throws  SQLException{
        Dao<Photo,Long> photoDao = getPhotoDao();
        return photos.queryBuilder().orderBy("taken_time", true).query();
    }
    public List<Photo> selectPhotosByDayId(Long dayId) throws  SQLException{
        Dao<Photo,Long> photoDao = getPhotoDao();
        return photos.queryBuilder().orderBy("taken_time", true).where().eq("day_id", dayId).query();
    }
    public List<Photo> selectClearestPhotosByDayId(Long dayId) throws  SQLException{
        Dao<Photo,Long> photoDao = getPhotoDao();
        return photos.queryBuilder().orderBy("taken_time", true).where().eq("day_id",dayId).and().eq("clearest_id", new ColumnArg("id")).query();
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
        return photoDao.queryBuilder().orderBy("taken_time",true).where().eq("position_id", positionId).and().eq("clearest_id", new ColumnArg("id")).query();
    }
    public List<Photo> selectClearestPhoto(Long id) throws SQLException {
        Dao<Photo,Long> photoDao = getPhotoDao();
        return photoDao.queryBuilder().orderBy("taken_time", false).where().eq("clearest_id", id).query();
    }
    public List<Day> selectBeforeFiveDay() throws SQLException {
        Dao<Day,Long> dayDao = getDayDao();
        Date date = new LocalDateTime(getLastPhoto().getTakenTime()).minusDays(10).toDate();
        return dayDao.queryBuilder().orderBy("date", false).where().ge("date", date.getTime()).query();
    }
    public List<Day> selectBeforeTenDay(Long start) throws SQLException {
        Dao<Day,Long> dayDao = getDayDao();
        if(dayDao.countOf()==0)
            return null;
        return dayDao.query(dayDao.queryBuilder().orderBy("date",false).limit(10L).where().lt("date", start).prepare());
    }
    public List<Position> selectPositionByDayId(Long dayId) throws SQLException{
        Dao<Position,Long> positionDao = getPositionDao();

        return positionDao.queryBuilder().orderBy("start_time", true).where().eq("day_id", dayId).query();
    }
    public int countPhotoByDayid(Long dayId) throws SQLException{
        List<Position> positions = selectPositionByDayId(dayId);
        long photoCnt=getPhotoDao().queryBuilder().where().eq("day_id",dayId).and().eq("clearest_id", new ColumnArg("id")).countOf();
//        for(Position position : positions)
//            photoCnt += selectPhotoByPositionId(position.getId()).size();
        return (int)photoCnt;
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
    public Day selectLastDay() throws SQLException {
        Dao<Day,Long> dayDao = getDayDao();
        if(dayDao.countOf()==0)
            return null;
        return dayDao.query(dayDao.queryBuilder().orderBy("date", false).limit(1L).prepare()).get(0);
    }
    public int countPosition(Long dayid) throws SQLException {
        Dao<Position,Long> positionDao = getPositionDao();
        return (int) positionDao.queryBuilder().where().eq("day_id",dayid).countOf();
    }
    public void updateClearestId(Long id, Long newid) throws SQLException {
        Dao<Photo,Long> photoDao = getPhotoDao();
        UpdateBuilder<Photo,Long> updateBuilder = photoDao.updateBuilder();
        updateBuilder.updateColumnValue("clearest_id",newid).where().eq("clearest_id",id);
        updateBuilder.update();
    }
    public void removePhoto(Long id) throws SQLException{
        DeleteBuilder<Photo,Long> deleteBuilder = getPhotoDao().deleteBuilder();
        deleteBuilder.where().eq("id",id);
        deleteBuilder.delete();
    }
    public boolean isEmptyPosition(Long id) throws SQLException {
        Dao<Photo,Long> photoDao = getPhotoDao();
       if(photoDao.queryBuilder().where().eq("position_id",id).countOf()==0) return true;
        return false;
    }
    public boolean isEmptyDay(Long id) throws SQLException {
        if(countPhotoByDayid(id)==0) return true;
        return false;
    }
    public void removePosition(Long id) throws SQLException{
        DeleteBuilder<Position,Long> deleteBuilder = getPositionDao().deleteBuilder();
        deleteBuilder.where().eq("id",id);
        deleteBuilder.delete();
    }
    public void removeDay(Long id) throws SQLException{
        DeleteBuilder<Day,Long> deleteBuilder = getDayDao().deleteBuilder();
        deleteBuilder.where().eq("id",id);
        deleteBuilder.delete();
    }
    public boolean hasDay(Long id) throws SQLException {
        Long count = getDayDao().queryBuilder().where().eq("id",id).countOf();
        if(count==0) return false;
        return true;
    }
    public void mergePosition(Long to,Long from) throws SQLException{
        UpdateBuilder<Photo,Long>  updateBuilder = getPhotoDao().updateBuilder();
        updateBuilder.updateColumnValue("position_id",to).where().eq("position_id",from);
        updateBuilder.update();
        removePosition(from);
        updatePositionEndTime(to);
    }
    public void updatePositionEndTime(Long id) throws SQLException {
        Long start = getPhotoDao().queryBuilder().orderBy("taken_time", true).where().eq("position_id",id).queryForFirst().getTakenTime();
        Long end = getPhotoDao().queryBuilder().orderBy("taken_time", false).where().eq("position_id",id).queryForFirst().getTakenTime();
        Position position = selectPosition(id);
        position.setStartTime(start);
        position.setEndTime(end);
        updatePosition(position);
    }
    public Long insertRecent(RecentSearch recent) throws SQLException{

        RecentSearch search = getRecentDao().queryBuilder().where().eq("keyword",recent.getKeyword()).and().eq("type",recent.getType()).queryForFirst();
        if(search!=null) {
            getRecentDao().updateBuilder().updateColumnValue("create_date",new Date().getTime()).
                        where().eq("keyword",recent.getKeyword()).and().eq("type",recent.getType());
            getRecentDao().updateBuilder().update();
        }else
            getRecentDao().create(recent);
        return getRecentDao().extractId(recent);
    }
    public List<RecentSearch> getRecentSearch(String type) throws SQLException {
        if(getRecentDao().queryBuilder().where().eq("type",type).countOf()==0)
            return null;
        return getRecentDao().queryBuilder().limit(10L).orderBy("create_date",false).where().eq("type",type).query();
    }
    public void removeRecent(Long id ) throws SQLException {
        DeleteBuilder<RecentSearch,Long> deleteBuilder = getRecentDao().deleteBuilder();
        deleteBuilder.where().eq("id",id);
        deleteBuilder.delete();
    }
}
//select * from photo where id = clearest_id
