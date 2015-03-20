package co.hasBeen.model.api;

/**
 * Created by 주현 on 2015-02-03.
 */
public class Alarm {
    public enum Type {
        DAY_POST,DAY_POST_DONE, DAY_COMMENT, DAY_LOVE, PHOTO_COMMENT, PHOTO_LOVE, FOLLOW, FB_FRIEND;
    }
    Long id;
    User user;
    String category;
    Type type;
    Day day;
    Photo photo;
    Loved love;
    Comment comment;
    Follow follow;
    User toUser;
    Long createdTime;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getCategory() {
        return category;
    }

    public Type getType() {
        return type;
    }

    public Day getDay() {
        return day;
    }

    public Photo getPhoto() {
        return photo;
    }

    public Loved getLove() {
        return love;
    }

    public Comment getComment() {
        return comment;
    }

    public Follow getFollow() {
        return follow;
    }

    public User getToUser() {
        return toUser;
    }

    public Long getCreatedTime() {
        return createdTime;
    }
}
