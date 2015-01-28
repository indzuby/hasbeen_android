package example.test.hasBeen.model.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

import example.test.hasBeen.gallery.GalleryPositionAdapter;
import example.test.hasBeen.model.api.Comment;
import example.test.hasBeen.model.api.User;

/**
 * Created by zuby on 2015-01-09.
 */
@DatabaseTable(tableName = "day")
public class Day {
    @DatabaseField(generatedId = true)
    Long id;
    @DatabaseField
    String title;
    @DatabaseField
    String description;
    @DatabaseField(columnName = "photo_count")
    int photoCount;
    @DatabaseField
    Long date;
    @DatabaseField
    String country;
    @DatabaseField
    String city;
    @DatabaseField(columnName = "main_photo_id")
    Long mainPhotoId;

    String area;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public Long getMainPhotoId() {
        return mainPhotoId;
    }

    public void setMainPhotoId(Long mainPhotoId) {
        this.mainPhotoId = mainPhotoId;
    }



    public Day() {
    }
    List<Position> positions;

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }
    GalleryPositionAdapter positionAdapter;

    public GalleryPositionAdapter getPositionAdapter() {
        return positionAdapter;
    }

    public void setPositionAdapter(GalleryPositionAdapter positionAdapter) {
        this.positionAdapter = positionAdapter;
    }
    User user;
    Long createdTime;
    int commentCount;
    int loveCount;
    int shareCount;
    int itineraryIndex;
    Place mainPlace;
    Photo mainPhoto;
    List<Position> positionList;
    List<Comment> commentList;

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

    public Place getMainPlace() {
        return mainPlace;
    }

    public void setMainPlace(Place mainPlace) {
        this.mainPlace = mainPlace;
    }

    public Photo getMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(Photo mainPhoto) {
        this.mainPhoto = mainPhoto;
    }

    public List<Position> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<Position> positionList) {
        this.positionList = positionList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
}
