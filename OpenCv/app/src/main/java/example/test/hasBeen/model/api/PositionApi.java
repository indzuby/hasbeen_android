package example.test.hasBeen.model.api;

import java.util.List;

/**
 * Created by zuby on 2015-01-28.
 */
public class PositionApi {
    Long id;
    Long dayId;
    String type;
    Long startTime;
    Long endTime;
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


    public Long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }




    public String getCategoryIconPrefix() {
        return categoryIconPrefix;
    }


    public String getCategoryIconSuffix() {
        return categoryIconSuffix;
    }

    PlaceApi place;

    public PlaceApi getPlace() {
        return place;
    }

    PhotoApi mainPhoto;

    List<PhotoApi> photoList;

    public PhotoApi getMainPhoto() {
        return mainPhoto;
    }

    public List<PhotoApi> getPhotoList() {
        return photoList;
    }

}