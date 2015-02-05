package example.test.hasBeen.model.api;

import android.graphics.Bitmap;

/**
 * Created by zuby on 2015-01-09.
 */
public class PlaceApi {
    Long id;
    String venueId;
    String categoryId;
    String categoryName;
    String country;
    String city;
    String name;
    float lat;
    float lon;
    String categoryIconPrefix;
    String categoryIconSuffix;

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
    public PlaceApi() {
    }
    PhotoApi mainPhoto;

    public PhotoApi getMainPhoto() {
        return mainPhoto;
    }
    Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
     }

}
