package example.test.hasBeen.model.api;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by zuby on 2015-01-28.
 */
public class DayApi {
    Long id;
    String title;
    String description;
    int photoCount;
    Long date;
    String country;
    String city;
    User user;
    Long createdTime;
    int commentCount;
    int loveCount;
    int shareCount;
    int itineraryIndex;
    PlaceApi mainPlace;
    User following;
    String newsFeedType;
    List<PhotoApi> photoList;
    List<PositionApi> positionList;
    List<Comment> commentList;
    Long updatedTime ;

    public void setLove(Loved love) {
        this.love = love;
    }

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
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



    public DayApi() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getLoveCount() {
        return loveCount;
    }

    public void setLoveCount(int loveCount) {
        this.loveCount = loveCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public int getItineraryIndex() {
        return itineraryIndex;
    }

    public void setItineraryIndex(int itineraryIndex) {
        this.itineraryIndex = itineraryIndex;
    }

    public PlaceApi getMainPlace() {
        return mainPlace;
    }


    public List<PositionApi> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<PositionApi> positionList) {
        this.positionList = positionList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public User getFollowing() {
        return following;
    }

    public String getNewsFeedType() {
        return newsFeedType;
    }

    public List<PhotoApi> getPhotoList() {
        return photoList;
    }
    Bitmap mainPhoto;

    public Bitmap getMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(Bitmap mainPhoto) {
        this.mainPhoto = mainPhoto;
    }
    Loved love;

    public Loved getLove() {
        return love;
    }
}
