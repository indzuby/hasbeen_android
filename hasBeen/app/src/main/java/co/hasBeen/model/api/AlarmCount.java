package co.hasBeen.model.api;

/**
 * Created by 주현 on 2015-02-27.
 */
public class AlarmCount {
    int newsCount;
    int youCount;
    public int getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(int newsCount) {
        this.newsCount = newsCount;
    }

    public int getYouCount() {
        return youCount;
    }

    public void setYouCount(int youCount) {
        this.youCount = youCount;
    }

    public int getTotalCount() {
        return getYouCount()+getNewsCount();
    }
}
