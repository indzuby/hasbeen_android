package example.test.hasBeen.model.database;

import android.graphics.Bitmap;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by zuby on 2015-01-09.
 */
@DatabaseTable(tableName = "place")
public class Place {
    @DatabaseField(generatedId = true)
    Long id;
    @DatabaseField(columnName = "venue_id")
    String venueId;
    @DatabaseField(columnName = "category_id")
    String categoryId;
    @DatabaseField(columnName = "category_name")
    String categoryName;
    @DatabaseField
    String country;
    @DatabaseField
    String city;
    @DatabaseField
    String name;
    @DatabaseField
    float lat;
    @DatabaseField
    float lon;
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

    public Long getId() {
        return id;
    }

    public String getCategoryIconSuffix() {
        return categoryIconSuffix;
    }

    public void setCategoryIconSuffix(String categoryIconSuffix) {
        this.categoryIconSuffix = categoryIconSuffix;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCategoryIconPrefix() {
        return categoryIconPrefix;
    }

    public void setCategoryIconPrefix(String categoryIconPrefix) {
        this.categoryIconPrefix = categoryIconPrefix;
    }

    public Place(String venue_id, String category_id, String category_name, String country, String city, String name, float lat, float lon, String categoryIconPrefix, String categoryIconSuffix) {
        this.venueId = venue_id;
        this.categoryId = category_id;
        this.categoryName = category_name;
        this.country = country;
        this.city = city;
        this.name = name;

        this.lat = lat;
        this.lon = lon;
        this.categoryIconPrefix = categoryIconPrefix;
        this.categoryIconSuffix = categoryIconSuffix;
    }

    public Place() {
    }
    Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    Photo mainPhoto;

    public Photo getMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(Photo mainPhoto) {
        this.mainPhoto = mainPhoto;
    }
}
