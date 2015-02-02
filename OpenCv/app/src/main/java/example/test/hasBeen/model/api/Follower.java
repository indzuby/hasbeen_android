package example.test.hasBeen.model.api;

/**
 * Created by 주현 on 2015-02-02.
 */
public class Follower {
    Integer id;
    User toUser;
    User fromUser;
    Integer followingId;

    public Integer getId() {
        return id;
    }

    public User getToUser() {
        return toUser;
    }

    public Integer getFollowingId() {
        return followingId;
    }

    public User getFromUser() {
        return fromUser;
    }
}
