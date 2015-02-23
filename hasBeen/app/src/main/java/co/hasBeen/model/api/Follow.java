package co.hasBeen.model.api;

/**
 * Created by 주현 on 2015-02-02.
 */
public class Follow {
    Long id;
    User toUser;
    User fromUser;
    Long followingId;

    public Long getId() {
        return id;
    }

    public User getToUser() {
        return toUser;
    }

    public Long getFollowingId() {
        return followingId;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }
}
