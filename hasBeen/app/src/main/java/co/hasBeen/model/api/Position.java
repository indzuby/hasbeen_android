package co.hasBeen.model.api;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created by zuby on 2015-01-12.
 */
@DatabaseTable(tableName = "position")
public class Position {
    @DatabaseField(generatedId = true)
    Long id;
    @DatabaseField(columnName = "day_id")
    Long dayId;
    @DatabaseField
    String type;
    @DatabaseField(columnName = "start_time")
    Long startTime;
    @DatabaseField(columnName = "end_time")
    Long endTime;
    @DatabaseField(columnName = "place_id")
    Long placeId;
    @DatabaseField(columnName = "category_icon_prefix")
    String categoryIconPrefix;
    @DatabaseField(columnName = "category_icon_suffix")
    String categoryIconSuffix;
    @DatabaseField(columnName = "main_photo_id")
    Long mainPhotoId;

    public Long getMainPhotoId() {
        return mainPhotoId;
    }

    public void setMainPhotoId(Long mainPhotoId) {
        this.mainPhotoId = mainPhotoId;
    }

    public Position() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDayId() {
        return dayId;
    }

    public void setDayId(Long dayId) {
        this.dayId = dayId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getCategoryIconPrefix() {
        return categoryIconPrefix;
    }

    public void setCategoryIconPrefix(String categoryIconPrefix) {
        this.categoryIconPrefix = categoryIconPrefix;
    }

    public String getCategoryIconSuffix() {
        return categoryIconSuffix;
    }

    public void setCategoryIconSuffix(String categoryIconSuffix) {
        this.categoryIconSuffix = categoryIconSuffix;
    }

    Place place;

    public Place getPlace() {
        return place;
    }

    Photo mainPhoto;

    List<Photo> photoList;

    public Photo getMainPhoto() {
        return mainPhoto;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public void setMainPhoto(Photo mainPhoto) {
        this.mainPhoto = mainPhoto;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }
    Boolean[] isCheckedPhoto;

    public Boolean[] getIsCheckedPhoto() {
        return isCheckedPhoto;
    }

    public void setIsCheckedPhoto(Boolean[] isCheckedPhoto) {
        this.isCheckedPhoto = isCheckedPhoto;
    }

    Long idFromMobile;

    public Long getIdFromMobile() {
        return idFromMobile;
    }

    public void setIdFromMobile(Long idFromMobile) {
        this.idFromMobile = idFromMobile;
    }
    String direction;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    List<Gps> gpsList;

    public List<Gps> getGpsList() {
        return gpsList;
    }

    public void setGpsList(List<Gps> gpsList) {
        this.gpsList = gpsList;
    }

    public class Gps {
        float lat,lon;

        public float getLat() {
            return lat;
        }

        public void setLat(float lat) {
            this.lat = lat;
        }

        public float getLon() {
            return lon;
        }

        public void setLon(float lon) {
            this.lon = lon;
        }
    }

}
