package co.hasBeen.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import co.hasBeen.model.api.Alarm;
import co.hasBeen.model.api.Follow;
import co.hasBeen.model.api.Loved;
import co.hasBeen.model.api.User;
import co.hasBeen.model.database.Day;
import co.hasBeen.model.database.Photo;
import co.hasBeen.model.database.Position;

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
    public static Position convertJsonToPosition(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if(f.getDeclaredClass() == Photo.class){
                        return true;
                }
                return false;
            }
            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        Position position = gson.fromJson(json, Position.class);
        return position;
    }
    public static Day convertJsonDay(Reader reader) {
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
        return gson.fromJson(reader, Day.class);
    }
    public static List<Day> convertJsonDayList(Reader reader) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if(f.getDeclaredClass() == Photo.class){
                    if(f.getName().equals("day") || f.getName().equals("place"))
                        return true;
                }
                else if(f.getDeclaredClass() == Loved.class) {
                    if(f.getName().equals("day") || f.getName().equals("photo"))
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
        Type listType = new TypeToken<List<Day>>(){}.getType();
        return gson.fromJson(reader, listType);
    }
    public static List<Photo> convertJsonPhotoList(Reader reader) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if(f.getName().equals("day") || f.getName().equals("place") ){
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
        Type listType = new TypeToken<List<Photo>>(){}.getType();
        return gson.fromJson(reader, listType);
    }
    public static List<Loved> convertJsonLovedList(Reader reader) {
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
        Type listType = new TypeToken<List<Loved>>(){}.getType();
        return gson.fromJson(reader, listType);
    }
    public static List<Follow> convertJsonFollowList(Reader reader) {


        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if(f.getName().equals("coverPhoto"))
                    return true;
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        Type listType = new TypeToken<List<Follow>>(){}.getType();
        return gson.fromJson(reader, listType);
    }
    public static List<Alarm> convertJsonAlarmList(Reader reader) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if(f.getDeclaredClass() == Photo.class){
                    if(f.getName().equals("day") || f.getName().equals("place") || f.getName().equals("user"))
                        return true;
                }
                else if(f.getDeclaredClass() == Day.class) {
                    if(f.getName().equals("photoList") || f.getName().equals("photoCount"))
                        return false;
                    else
                        return true;
                }
                else if(f.getDeclaredClass() == Follow.class) {
                    if(f.getName().equals("toUser") || f.getName().equals("fromUser"))
                        return true;
                }
                else if(f.getDeclaredClass()==Loved.class) {
                    if(f.getName().equals("id"))
                        return false;
                    else
                        return true;
                }else if(f.getDeclaredClass()== User.class) {
                    if(f.getName().equals("follow"))
                        return true;
                }
                if(f.getName().equals("coverPhoto") || f.getName().equals("commentList"))
                    return true;
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        Type listType = new TypeToken<List<Alarm>>(){}.getType();
        return gson.fromJson(reader, listType);
    }
}
