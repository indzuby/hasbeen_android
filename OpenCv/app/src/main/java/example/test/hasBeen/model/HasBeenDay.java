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
    @DatabaseField
    String title;
    @DatabaseField
    String description;
    @DatabaseField(columnName = "photo_count")
    int photoCount;
    @DatabaseField(dataType = DataType.DATE_STRING,format = "yyyy-MM-dd")
    Date date;
    @DatabaseField
    String country;
    @DatabaseField
    String city;
    @DatabaseField(columnName = "main_photo_id")
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



    public HasBeenDay() {
    }
}
