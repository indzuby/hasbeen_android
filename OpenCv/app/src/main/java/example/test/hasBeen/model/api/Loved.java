package example.test.hasBeen.model.api;

/**
 * Created by 주현 on 2015-02-06.
 */
public class Loved {
    Long id;
    PhotoApi photo;
    DayApi day;
    String type;

    public Long getId() {
        return id;
    }

    public PhotoApi getPhoto() {
        return photo;
    }

    public DayApi getDay() {
        return day;
    }

    public String getType() {
        return type;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
