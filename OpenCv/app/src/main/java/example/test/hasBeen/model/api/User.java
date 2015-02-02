package example.test.hasBeen.model.api;

/**
 * Created by zuby on 2015-01-26.
 */
public class User {
    int id;
    String firstName;
    String lastName;
    String imageUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    PhotoApi coverPhoto;
    int dayCount;
    int photoCount;
    int loveCount;
    int followerCount;
    int followingCount;
    int tripCount;

    public int getTripCount() {
        return tripCount;
    }

    public PhotoApi getCoverPhoto() {
        return coverPhoto;
    }

    public int getDayCount() {
        return dayCount;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public int getLoveCount() {
        return loveCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }
}
