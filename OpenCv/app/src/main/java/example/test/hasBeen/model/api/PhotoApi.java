package example.test.hasBeen.model.api;

/**
 * Created by zuby on 2015-01-09.
 */
public class PhotoApi {
    Long id;
    String title;
    String description;
    String country;
    String city;
    String placeName;
    float lat;
    float lon;
    Long takenDate;

    public PhotoApi() {
    }

    public Long getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }


    public String getCountry() {
        return country;
    }


    public String getCity() {
        return city;
    }


    public String getPlaceName() {
        return placeName;
    }


    public float getLat() {
        return lat;
    }


    public float getLon() {
        return lon;
    }


    public Long getTakenDate() {
        return takenDate;
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


    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }


    public boolean isPortrait() {
        return isPortrait;
    }


    int width;
    int height;
    boolean isPortrait;
}