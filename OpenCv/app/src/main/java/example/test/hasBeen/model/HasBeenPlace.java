package example.test.hasBeen.model;

/**
 * Created by zuby on 2015-01-09.
 */
public class HasBeenPlace {
    int id;
    int venue_id;
    int category_id;
    String category_name;
    String country;
    String city;
    String name;
    float lat,lon;
    String category_icon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVenue_id() {
        return venue_id;
    }

    public void setVenue_id(int venue_id) {
        this.venue_id = venue_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
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

    public String getCategory_icon() {
        return category_icon;
    }

    public void setCategory_icon(String category_icon) {
        this.category_icon = category_icon;
    }

    public HasBeenPlace(int venue_id, int category_id, String category_name, String country, String city, String name, float lat, float lon, String category_icon) {
        this.venue_id = venue_id;
        this.category_id = category_id;
        this.category_name = category_name;
        this.country = country;
        this.city = city;
        this.name = name;

        this.lat = lat;
        this.lon = lon;
        this.category_icon = category_icon;
    }
}
