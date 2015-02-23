package co.hasBeen.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import co.hasBeen.model.database.Day;
import co.hasBeen.model.database.Photo;

/**
 * Created by 주현 on 2015-02-13.
 */
public class JsonConverter {

    public static Day convertJsonToDay(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if(f.getDeclaredClass() == Photo.class){
                    if(f.getName().equals("day") || f.getName().equals("place"))
                        return true;
                }
                if(f.getName().equals("coverPhoto"))
                    return true;
                return false;
            }
            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        Day day = gson.fromJson(json, Day.class);
        return day;
    }
}
