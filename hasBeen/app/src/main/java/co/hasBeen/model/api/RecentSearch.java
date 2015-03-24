package co.hasBeen.model.api;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by 주현 on 2015-03-24.
 */
@DatabaseTable(tableName = "recentsearch")
public class RecentSearch {
    @DatabaseField(generatedId = true)
    Long id;
    @DatabaseField
    String keyword;
    @DatabaseField(columnName = "create_date")
    Long createDate;

    public Long getId() {
        return id;
    }
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }
}
