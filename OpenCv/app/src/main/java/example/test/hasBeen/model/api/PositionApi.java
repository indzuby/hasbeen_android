package example.test.hasBeen.model.api;

import java.util.List;

import example.test.hasBeen.model.database.Photo;
import example.test.hasBeen.model.database.Place;

/**
 * Created by zuby on 2015-01-28.
 */
public class PositionApi {
    Long id;
    Long dayId;
    String type;
    Long startDate;
    Long endDate;
    String categoryIconPrefix;
    String categoryIconSuffix;


    public PositionApi() {

    }

    public Long getId() {
        return id;
    }


    public Long getDayId() {
        return dayId;
    }


    public String getType() {
        return type;
    }


    public Long getStartDate() {
        return startDate;
    }

    public Long getEndDate() {
        return endDate;
    }




    public String getCategoryIconPrefix() {
        return categoryIconPrefix;
    }


    public String getCategoryIconSuffix() {
        return categoryIconSuffix;
    }

    Place place;

    public Place getPlace() {
        return place;
    }

    Photo mainPhoto;
    List<Photo> photoList;

    public Photo getMainPhoto() {
        return mainPhoto;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

}