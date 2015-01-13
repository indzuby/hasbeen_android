package example.test.hasBeen.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by zuby on 2015-01-12.
 */
@DatabaseTable(tableName = "position")
public class HasBeenPosition {
    @DatabaseField(generatedId = true)
    Long id;
    @DatabaseField(columnName = "day_id",canBeNull = false)
    Long dayId;
    @DatabaseField(canBeNull = false)
    String type;
    @DatabaseField(columnName = "start_date",canBeNull = false,dataType = DataType.DATE_STRING,format = "yyyy-MM-dd HH:mm::ss")
    Date startDate;
    @DatabaseField(columnName = "end_date",canBeNull = false,dataType = DataType.DATE_STRING,format = "yyyy-MM-dd HH:mm::ss")
    Date endDate;
    @DatabaseField(columnName = "place_id")
    Long placeId;
    @DatabaseField(columnName = "category_icon_prefix")
    String categoryIconPrefix;
    @DatabaseField(columnName = "category_icon_suffix")
    String categoryIconSuffix;
    @DatabaseField(columnName = "main_photo_id",canBeNull = false)
    Long mainPhotoId;

    public HasBeenPosition() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDayId() {
        return dayId;
    }

    public void setDayId(Long dayId) {
        this.dayId = dayId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getCategoryIconPrefix() {
        return categoryIconPrefix;
    }

    public void setCategoryIconPrefix(String categoryIconPrefix) {
        this.categoryIconPrefix = categoryIconPrefix;
    }

    public String getCategoryIconSuffix() {
        return categoryIconSuffix;
    }

    public void setCategoryIconSuffix(String categoryIconSuffix) {
        this.categoryIconSuffix = categoryIconSuffix;
    }

    public HasBeenPosition(Long day_id, String type, Date startDate, Date endDate,Long mainPhotoId, Long placeId, String categoryIconPrefix, String categoryIconSuffix) {

        this.dayId = day_id;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mainPhotoId = mainPhotoId;
        this.placeId = placeId;
        this.categoryIconPrefix = categoryIconPrefix;
        this.categoryIconSuffix = categoryIconSuffix;
    }
}