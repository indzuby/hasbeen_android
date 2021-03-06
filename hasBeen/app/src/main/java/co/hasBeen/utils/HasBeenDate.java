package co.hasBeen.utils;

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;

import java.util.Date;

import co.hasBeen.R;
import co.hasBeen.model.api.Photo;

/**
 * Created by zuby on 2015-01-15.
 */
public class HasBeenDate {
    String[] month = {"","Jan.","Feb.","Mar.","April.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};

    public static boolean isSameDate(Long aDate, Long bDate) {
        String from = new DateTime(aDate).withZone(DateTimeZone.UTC).toString("MMMM dd, yyyy");
        String to = new DateTime(bDate).withZone(DateTimeZone.UTC).toString("MMMM dd, yyyy");
        if(from.equals(to))
            return true;
        return false;
    }
    public static boolean isBeforeThatDate(Photo lastPhoto , Photo currentPhoto){
        if(lastPhoto==null) return false;
//        Log.i("photo time",lastPhoto.getTakenTime().getTime()+"");
//        Log.i("photo time",currentPhoto.getTakenTime().getTime()+"");
        if(currentPhoto.getTakenTime()- lastPhoto.getTakenTime() > 1000) return false;
        return true;
    }

    public static Date getDate(long taken_date) {
        return new Date(taken_date);
    }
    public static int calculateDateRange(Long from, Long to) {
        DateTime start = new DateTime(from).withZone(DateTimeZone.UTC);
        DateTime end = new DateTime(to).withZone(DateTimeZone.UTC);
        return Days.daysBetween(start,end).getDays();
    }
    public static Long getBeforeDay(Long currentTime, int day){
        Date date = new DateTime(currentTime).withZone(DateTimeZone.UTC).minusDays(day).toDate();
        return date.getTime();
    }
    public static String convertDate(Date date) {
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//        LocalDate newDate = new LocalDate(date);
//        return newDate.toString("MMMM dd, yyyy")
        return convertDate(date.getTime());
    }
    public static String convertDate(long timeStamp) {
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//        Date date = new Date();
//        date.setTime(timeStamp);
//        return new LocalDate(date).toString("MMMM dd, yyyy");

        DateTime date = new DateTime(timeStamp).withZone(DateTimeZone.UTC);
        return date.toString("MMMM dd, yyyy");
    }
    public static String convertTime(Long startTime, Long endTime,Context context) {
        String start = new DateTime(startTime).withZone(DateTimeZone.UTC).toString(context.getString(R.string.photo_time));
        String end = new DateTime(endTime).withZone(DateTimeZone.UTC).toString(context.getString(R.string.photo_time));

        if(start.equals(end))
            return start;
        return start+" – "+end;
    }
    public static String convertDate(Long startTime, Long endTime) {
        String start = convertDate(startTime);
        String end = convertDate(endTime);
        if(start.equals(end))
            return start;
        return start+" – "+end;
    }
    public static boolean isTimeRangeInFive(Long a, Long b){

        return Math.abs(a - b) < 500;
    }
    public static String getGapTime(Long time,Context context) {
        Date currentTime = new Date();
        Date commentTime = new Date(time);
        Log.i("TIME", new Date().getTime() + "");
        Long gap = currentTime.getTime() - commentTime.getTime();
        int dayGap = Days.daysBetween(new DateTime(commentTime),new DateTime(currentTime)).getDays();
        if(dayGap<7) {
            if(dayGap<1) {
                gap = gap/1000;
                long hour = gap/3600;
                if(hour>=1)
                    return context.getString(R.string.hour_ago,hour);
                else {
                    if(gap/60 ==0)
                        return context.getString(R.string.just_now);
                    return context.getString(R.string.miniutes_ago,gap / 60);
                }
            }else
                return new LocalDateTime(commentTime).toString(context.getString(R.string.day_ago));
        }else if(dayGap<28){
            return context.getString(R.string.week_ago, dayGap / 7);
        }else
            return new LocalDateTime(commentTime).toString(context.getString(R.string.month_ago));
    }

}
