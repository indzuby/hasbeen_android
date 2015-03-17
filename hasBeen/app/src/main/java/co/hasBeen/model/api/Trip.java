package co.hasBeen.model.api;

import java.util.List;

/**
 * Created by 주현 on 2015-03-17.
 */
public class Trip extends Social{
    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }
    String startCity;
    String startCountry;
    String endCity;
    String endCountry;

    public String getStartCity() {
        return startCity;
    }

    public String getStartCountry() {
        return startCountry;
    }

    public String getEndCity() {
        return endCity;
    }

    public String getEndCountry() {
        return endCountry;
    }

    Long startTime;
    Long endTime;
    int itineraryLong;
    List<Day> dayList;
    public Long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public int getItineraryLong() {
        return itineraryLong;
    }

    public List<Day> getDayList() {
        return dayList;
    }
}
