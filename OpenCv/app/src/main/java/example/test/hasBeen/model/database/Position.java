package example.test.hasBeen.model.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import example.test.hasBeen.gallery.GalleryAdapter;

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
    @DatabaseField(columnName = "start_date")
    Long startDate;
    @DatabaseField(columnName = "end_date")
    Long endDate;
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

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
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
    List<Photo> photos;

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
    Place place;

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
    GalleryAdapter galleryAdapter;

    public GalleryAdapter getGalleryAdapter() {
        return galleryAdapter;
    }

    public void setGalleryAdapter(GalleryAdapter galleryAdapter) {
        this.galleryAdapter = galleryAdapter;
    }
}
