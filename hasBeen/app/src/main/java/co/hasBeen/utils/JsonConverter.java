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
import co.hasBeen.model.api.Comment;
import co.hasBeen.model.api.Follow;
import co.hasBeen.model.api.Loved;
import co.hasBeen.model.api.PushAlarm;
import co.hasBeen.model.api.Trip;
import co.hasBeen.model.api.User;
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Photo;
import co.hasBeen.model.api.Position;

/**
 * Created by 주현 on 2015-02-13.
 */
public class JsonConverter {

    public static Day convertJsonToDay(String json) throws Exception{
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
    public static Position convertJsonToPosition(String json) throws Exception{
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
    public static Day convertJsonDay(Reader reader) throws Exception{
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
    public static List<Day> convertJsonDayList(Reader reader) throws Exception{
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
    public static List<Photo> convertJsonPhotoList(Reader reader) throws Exception{
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
    public static List<Photo> convertJsonPhotoList(String reader) throws Exception{
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
    public static List<Loved> convertJsonLovedList(Reader reader) throws Exception{
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
    public static List<Follow> convertJsonFollowList(Reader reader) throws Exception{


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
    public static List<Alarm> convertJsonAlarmList(Reader reader) throws Exception{
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if(f.getDeclaredClass() == Photo.class){
                    if(f.getName().equals("day") || f.getName().equals("place") || f.getName().equals("user"))
                        return true;
                }
                else if(f.getDeclaredClass() == Day.class) {
                    if(f.getName().equals("positionList") || f.getName().equals("commentList") || f.getName().equals("user")|| f.getName().equals("placeList") || f.getName().equals("user") || f.getName().contains("main"))
                        return true;
                }
                else if(f.getDeclaredClass() == Follow.class) {
                    if(f.getName().equals("toUser") || f.getName().equals("fromUser"))
                        return true;
                }
                else if(f.getDeclaredClass()==Loved.class) {
                    if(f.getName().equals("day") || f.getName().equals("photo"))
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
    public static PushAlarm convertJsonToPushAlarm(String json) throws Exception{
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        PushAlarm push = gson.fromJson(json, PushAlarm.class);
        return push;
    }
    public static Comment convertJsonToComment(Reader reader) {

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
        Comment comment = gson.fromJson(reader, Comment.class);
        return comment;
    }
    public static Comment convertJsonToComment(String reader) {

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
        Comment comment = gson.fromJson(reader, Comment.class);
        return comment;
    }
    public static List<Comment> convertJsonToCommentList(Reader reader){

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
        Type listType = new TypeToken<List<Comment>>(){}.getType();
        List<Comment> comments = gson.fromJson(reader, listType);
        return comments;
    }
    public static List<Trip> convertJsonToTripList(Reader reader){

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if(f.getDeclaredClass() == Day.class){
                    if(!f.getName().equals("id") && !f.getName().equals("mainPhoto"))
                        return false;
                    return true;
                }
                return false;
            }
            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        Type listType = new TypeToken<List<Trip>>(){}.getType();
        List<Trip> trips = gson.fromJson(reader, listType);
        return trips;
    }
    public static User convertJsonToUser(Reader reader) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if (f.getDeclaringClass() == Photo.class)
                    if (f.getName().equals("day") || f.getName().equals("place"))
                        return true;

                if(f.getDeclaredClass() == Follow.class) {
                    if(f.getName().equals("toUser") || f.getName().equals("fromUser"))
                        return true;
                }
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        User user = gson.fromJson(reader, User.class);
        return user;
    }
    public static List<User> convertJsonToUserList(Reader reader) {
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
        Type listType = new TypeToken<List<User>>(){}.getType();
        List<User> users = gson.fromJson(reader, listType);
        return users;
    }

}
