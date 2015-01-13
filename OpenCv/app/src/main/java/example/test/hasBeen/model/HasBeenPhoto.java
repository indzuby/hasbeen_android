package example.test.hasBeen.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by zuby on 2015-01-09.
 */
@DatabaseTable(tableName = "photo")
public class HasBeenPhoto {
    @DatabaseField(generatedId = true)
    Long id;
    @DatabaseField(canBeNull = false)
    String title;
    @DatabaseField(canBeNull = false)
    String description;
    @DatabaseField(canBeNull = false)
    String country;
    @DatabaseField(canBeNull = false)
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
    @DatabaseField(columnName = "place_name",canBeNull = false)
    String placeName;
    @DatabaseField(canBeNull = false)
    float lat;
    @DatabaseField(canBeNull = false)
    float lon;
    @DatabaseField(columnName = "taken_date",canBeNull = false,dataType = DataType.DATE_STRING,format = "yyyy-MM-dd HH:mm::ss")
    Date takenDate;
    @DatabaseField(columnName = "day_id",canBeNull = false)
    Long dayId;
    @DatabaseField(columnName = "position_id",canBeNull = false)
    Long positionId;
    @DatabaseField(columnName = "place_id")
    Long placeId;

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
    public HasBeenPhoto(String title, String description, String country, String city, String place_name, float lat, float lon, Date takenDate,Long day_id, Long position_id) {
        this.title = title;
        this.description = description;
        this.country = country;
        this.city = city;
        this.placeName = place_name;
        this.lat = lat;
        this.lon = lon;
        this.takenDate = takenDate;
        this.dayId = day_id;
        this.positionId = position_id;
    }

    public HasBeenPhoto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getTakenDate() {
        return takenDate;
    }

    public void setTakenDate(Date takenDate) {
        this.takenDate = takenDate;
    }
//    public HasBeenPhoto clone(HasBeenPhoto photo){
//        HasBeenPhoto copyPhoto = new HasBeenPhoto();
//        copyPhoto.setId(photo.getId());
//        copyPhoto.setCategoryIconSuffix(photo.getCategoryIconSuffix());
//        copyPhoto.setCategoryIconPrefix(photo.getCategoryIconPrefix());
//        copyPhoto.setCity(photo.getCity());
//        copyPhoto.setCategryName(photo.getCategryName());
//
//    }

}
