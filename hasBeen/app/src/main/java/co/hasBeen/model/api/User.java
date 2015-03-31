package co.hasBeen.model.api;

/**
 * Created by zuby on 2015-01-26.
 */
public class User {
    public enum Gender {
        MALE,FEMALE;
    }
    public enum SignUpType {
        EMAIL,FACEBOOK;
    }
    Long id;
    String firstName;
    String lastName;
    String imageUrl;
    Long birthDay;
    Gender gender;
    SignUpType signUpType;
    String country;
    String city;
    String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getId() {
        return id;
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

    Photo coverPhoto;
    int dayCount;
    int photoCount;
    int loveCount;
    int followerCount;
    int followingCount;
    int tripCount;

    public int getTripCount() {
        return tripCount;
    }

    public Photo getCoverPhoto() {
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
    Follow follow;

    public Follow getFollow() {
        return follow;
    }

    public void setFollow(Follow follow) {
        this.follow = follow;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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

    public Long getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Long birthDay) {
        this.birthDay = birthDay;
    }

    public SignUpType getSignUpType() {
        return signUpType;
    }
}
