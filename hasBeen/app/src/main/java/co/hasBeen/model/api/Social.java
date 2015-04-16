package co.hasBeen.model.api;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by 주현 on 2015-03-12.
 */
public class Social {
    @DatabaseField(generatedId = true)
    Long id;
    int loveCount;
    int shareCount;
    int commentCount;
    Loved love;
    Long loveId;

    public Long getLoveId() {
        return loveId;
    }

    public void setLoveId(Long loveId) {
        this.loveId = loveId;
    }

    public Loved getLove() {
        return love;
    }

    public void setLove(Loved love) {
        this.love = love;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
