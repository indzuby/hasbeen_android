package example.test.hasBeen.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by zuby on 2015-01-09.
 */
@DatabaseTable(tableName = "day")
public class HasBeenDay {
    @DatabaseField(generatedId = true)
    Long id;
    @DatabaseField(canBeNull = false)
    String title;
    @DatabaseField(canBeNull = false)
    String description;
    @DatabaseField(columnName = "photo_count",canBeNull = false)
    int photoCount;
    @DatabaseField(canBeNull = false,dataType = DataType.DATE_STRING,format = "yyyy-MM-dd")
    Date date;
    @DatabaseField(canBeNull = false)
    String country;
    @DatabaseField(canBeNull = false)
    String city;
    @DatabaseField(columnName = "main_photo_id",canBeNull = false)
    Long mainPhotoId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Long getMainPhotoId() {
        return mainPhotoId;
    }

    public void setMainPhotoId(Long mainPhotoId) {
        this.mainPhotoId = mainPhotoId;
    }

    public HasBeenDay(String title ,String description, int photoCount, Date date, String country,String city,Long mainPhotoId){

        this.title = title;
        this.description = description;
        this.photoCount = photoCount;
        this.date = date;
        this.country = country;
        this.city = city;
        this.mainPhotoId = mainPhotoId;
    }

    public HasBeenDay() {
    }
}
