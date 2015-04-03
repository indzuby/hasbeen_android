package co.hasBeen.model.api;

import android.graphics.Bitmap;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created by zuby on 2015-01-09.
 */
@DatabaseTable(tableName = "photo")
public class Photo extends Social{
    @DatabaseField
    String title;
    @DatabaseField
    String description;
    @DatabaseField
    String country;
    @DatabaseField
    String city;

    public Long getDayId() {
        return dayId;
    }

    public void setDayId(Long dayId) {
        this.dayId = dayId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }
    @DatabaseField(columnName = "place_name")
    String placeName;
    @DatabaseField
    float lat;
    @DatabaseField
    float lon;
    @DatabaseField(columnName = "taken_time")
    Long takenTime;
    @DatabaseField(columnName = "day_id")
    Long dayId;
    @DatabaseField(columnName = "position_id")
    Long positionId;
    @DatabaseField(columnName = "place_id")
    Long placeId;
    @DatabaseField(columnName = "photo_path")
    String photoPath;
    @DatabaseField(columnName = "photo_id")
    Long photoId;
    @DatabaseField(columnName = "clearest_id")
    Long clearestId;


    int width;
    int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Long getClearestId() {
        return clearestId;
    }

    public void setClearestId(Long clearestId) {
        this.clearestId = clearestId;
    }

    public Integer getEdgeCount() {
        return edgeCount;
    }

    public void setEdgeCount(Integer edgeCount) {
        this.edgeCount = edgeCount;
    }

    @DatabaseField(columnName = "edge_count")

    Integer edgeCount;
    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public void setFourSquare(String venueId,String categoryId, String categoryName,String categoryIconPrefix,String categoryIconSuffix) {
        setVenueId(venueId);
        setCategoryId(categoryId);
        setCategryName(categoryName);
        setCategoryIconPrefix(categoryIconPrefix);
        setCategoryIconSuffix(categoryIconSuffix);
    }
    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategryName() {
        return categryName;
    }

    public void setCategryName(String categryName) {
        this.categryName = categryName;
    }

    String venueId;
    String categoryId;
    String categryName;
    String categoryIconPrefix;

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

    String categoryIconSuffix;
    public Photo() {
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

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

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

    public Long getTakenTime() {
        return takenTime;
    }

    public void setTakenTime(Long takenTime) {
        this.takenTime = takenTime;
    }

    User user;

    public User getUser() {
        return user;
    }

    public Day getDay() {
        return day;
    }

    public Place getPlace() {
        return place;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    Day day;
    Place place;
    List<Comment> commentList;
    Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    String smallUrl;
    String mediumUrl;
    String largeUrl;

    public String getSmallUrl() {
        return smallUrl;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }

    public String getLargeUrl() {
        return largeUrl;
    }

    Long idFromMobile;

    public Long getIdFromMobile() {
        return idFromMobile;
    }

    public void setIdFromMobile(Long idFromMobile) {
        this.idFromMobile = idFromMobile;
    }

    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }

    public void setMediumUrl(String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }

    public void setLargeUrl(String largeUrl) {
        this.largeUrl = largeUrl;
    }

    String binary;

    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }
    int page;

    public int getPage() {
        return page;
    }
}
