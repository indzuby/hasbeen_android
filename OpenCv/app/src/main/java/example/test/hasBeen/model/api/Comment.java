package example.test.hasBeen.model.api;

/**
 * Created by zuby on 2015-01-26.
 */
public class Comment {
    Long id;
    Long createdTime;
    String contents;
    User user;

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public String getContents() {
        return contents;
    }

    public Long getCreatedTime() {
        return createdTime;
    }
}
