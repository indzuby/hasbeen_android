package example.test.hasBeen.model.api;

/**
 * Created by zuby on 2015-01-26.
 */
public class Comment {
    int id;
    Long createdTime;
    String contents;

    public int getId() {
        return id;
    }

    public String getContents() {
        return contents;
    }

    public Long getCreatedTime() {
        return createdTime;
    }
}
