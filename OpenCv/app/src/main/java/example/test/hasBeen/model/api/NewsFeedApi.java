package example.test.hasBeen.model.api;

import java.util.ArrayList;

import example.test.hasBeen.model.database.Place;

/**
 * Created by zuby on 2015-01-26.
 */
public class NewsFeedApi {
    int id;
    long createdTime;
    long updatedTime;
    User user;
    String title;
    String description;
    long date;
    int photoCount;
    int commnetCount;
    int loveCount;
    int shareCount;
    int itineraryIndex;
    PlaceApi mainPlace;
    PhotoApi mainPhoto;
    ArrayList<Comment> commnetList;

    public int getId() {
        return id;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getDate() {
        return date;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public int getCommnetCount() {
        return commnetCount;
    }

    public int getLoveCount() {
        return loveCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public int getItineraryIndex() {
        return itineraryIndex;
    }

    public PlaceApi getMainPlace() {
        return mainPlace;
    }

    public PhotoApi getMainPhoto() {
        return mainPhoto;
    }

    public ArrayList<Comment> getCommnetList() {
        return commnetList;
    }
}