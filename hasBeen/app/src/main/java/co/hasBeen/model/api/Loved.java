package co.hasBeen.model.api;

/**
 * Created by 주현 on 2015-02-06.
 */
public class Loved {
    Long id;
    Photo photo;
    Day day;
    String type;

    public Long getId() {
        return id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public Day getDay() {
        return day;
    }

    public String getType() {
        return type;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
