package example.test.hasBeen.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by zuby on 2015-01-09.
 */
public class HasBeenPhoto {
    int id;
    String title;
    String description;
    String country;
    String city;

    public int getDay_id() {
        return day_id;
    }

    public void setDay_id(int day_id) {
        this.day_id = day_id;
    }

    public int getPosition_id() {
        return position_id;
    }

    public void setPosition_id(int position_id) {
        this.position_id = position_id;
    }

    public int getPlace_id() {
        return place_id;
    }

    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }

    String place_name;
    float lat,lon;
    String taken_date;
    int day_id;
    int position_id;
    int place_id;
    public HasBeenPhoto(int id,String title, String description, String country, String city, String place_name, float lat, float lon, String taken_date,int day_id, int position_id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.country = country;
        this.city = city;
        this.place_name = place_name;
        this.lat = lat;
        this.lon = lon;
        this.taken_date = taken_date;
        this.day_id = day_id;
        this.position_id = position_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
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

    public String getTaken_date() {
        return taken_date;
    }

    public void setTaken_date(String taken_date) {
        this.taken_date = taken_date;
    }
}
