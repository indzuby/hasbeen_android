package example.test.hasBeen.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by zuby on 2015-01-09.
 */
@DatabaseTable(tableName = "place")
public class HasBeenPlace {
    @DatabaseField(generatedId = true)
    Long id;
    @DatabaseField(columnName = "venue_id",unique=true)
    String venueId;
    @DatabaseField(columnName = "category_id")
    String categoryId;
    @DatabaseField(columnName = "category_name")
    String categoryName;
    @DatabaseField(canBeNull = false)
    String country;
    @DatabaseField(canBeNull = false)
    String city;
    @DatabaseField(canBeNull = false)
    String name;
    @DatabaseField(canBeNull = false)
    float lat;
    @DatabaseField(canBeNull = false)
    float lon;
    @DatabaseField(columnName = "category_icon_prefix",canBeNull = false)
    String categoryIconPrefix;
    @DatabaseField(columnName = "category_icon_suffix",canBeNull = false)
    String categoryIconSuffix;


    public Long getId() {
        return id;
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

    public HasBeenPlace(String venue_id, String category_id, String category_name, String country, String city, String name, float lat, float lon, String categoryIconPrefix,String categoryIconSuffix) {
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

    public HasBeenPlace() {
    }
}
