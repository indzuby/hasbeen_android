package co.hasBeen.model.api;

import android.graphics.Bitmap;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created by zuby on 2015-01-09.
 */
@DatabaseTable(tableName = "day")
public class Day extends Social{
    @DatabaseField
    String title;
    @DatabaseField
    String description;
    @DatabaseField(columnName = "photo_count")
    int photoCount;
    @DatabaseField
    Long date;
    @DatabaseField
    String country;
    @DatabaseField
    String city;
    @DatabaseField(columnName = "main_photo_id")
    Long mainPhotoId;
    @DatabaseField(columnName = "main_place_id")
    Long mainPlaceId;
    @DatabaseField(columnName = "created_time")
    Long createdTime;

    public Long getMainPlaceId() {
        return mainPlaceId;
    }

    public void setMainPlaceId(Long mainPlaceId) {
        this.mainPlaceId = mainPlaceId;
    }

    public void setMainPhoto(Photo mainPhoto) {
        this.mainPhoto = mainPhoto;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getMainPhotoId() {
        return mainPhotoId;
    }

    public void setMainPhotoId(Long mainPhotoId) {
        this.mainPhotoId = mainPhotoId;
    }

    public Day() {
    }
    User user;
    int itineraryIndex;
    Place mainPlace;
    User following;
    String newsFeedType;
    List<Photo> photoList;
    List<Position> positionList;
    List<Comment> commentList;
    Long updatedTime ;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public int getItineraryIndex() {
        return itineraryIndex;
    }

    public void setItineraryIndex(int itineraryIndex) {
        this.itineraryIndex = itineraryIndex;
    }

    public Place getMainPlace() {
        return mainPlace;
    }

    public void setMainPlace(Place mainPlace) {
        this.mainPlace = mainPlace;
    }

    public User getFollowing() {
        return following;
    }

    public void setFollowing(User following) {
        this.following = following;
    }

    public String getNewsFeedType() {
        return newsFeedType;
    }

    public void setNewsFeedType(String newsFeedType) {
        this.newsFeedType = newsFeedType;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public List<Position> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<Position> positionList) {
        this.positionList = positionList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }
    Photo mainPhoto;

    public Photo getMainPhoto() {
        return mainPhoto;
    }
    Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
   Boolean[] isCheckedPosition;

    public Boolean[] getIsCheckedPosition() {
        return isCheckedPosition;
    }

    public void setIsCheckedPosition(Boolean[] isCheckedPosition) {
        this.isCheckedPosition = isCheckedPosition;
    }
    Long tripId=0L;

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }
    String staticMapUrl;

    public String getStaticMapUrl() {
        return staticMapUrl;
    }

    public void setStaticMapUrl(String staticMapUrl) {
        this.staticMapUrl = staticMapUrl;
    }
    List<Place> placeList;

    public List<Place> getPlaceList() {
        return placeList;
    }

    public void setPlaceList(List<Place> placeList) {
        this.placeList = placeList;
    }
}
