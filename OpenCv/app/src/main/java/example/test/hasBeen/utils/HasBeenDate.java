package example.test.hasBeen.utils;

import android.util.Log;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.Date;

import example.test.hasBeen.model.database.Photo;

/**
 * Created by zuby on 2015-01-15.
 */
public class HasBeenDate {
    String[] month = {"","Jan.","Feb.","Mar.","April.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};
    public static boolean isSameDate(Photo beforePhoto,Photo photo) {
        int k = calculateDateRange(beforePhoto,photo);
        if(k==0) return true;
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
    public static int calculateDateRange(Photo from, Photo to) {
        LocalDateTime start = new LocalDateTime(from.getTakenTime());
        LocalDateTime end = new LocalDateTime(to.getTakenTime());
        return Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays();
    }

    public static boolean isDateRangeInThree(Photo photo, Photo standardPhoto){
        int k = calculateDateRange(photo,standardPhoto);
        if(k<=5) return true;

        return false;
    }
    public static String convertDate(Date date) {
        LocalDate newDate = new LocalDate(date);
        return newDate.toString("YYYY, MMMMM dd");
    }
    public static String convertDate(long timeStamp) {
        Date date = new Date();
        date.setTime(timeStamp);
        return new LocalDate(date).toString("MMMM dd, yyyy");
    }
    public static String convertTime(Long startTime, Long endTime) {
        LocalDateTime start = new LocalDateTime(startTime);
        LocalDateTime end = new LocalDateTime(endTime);
        if(start.toString("a hh:mm").equals(end.toString("a hh:mm")))
            return start.toString("a hh : mm");
        return start.toString("a hh : mm – ")+end.toString("a hh : mm");
    }
    public static boolean isTimeRangeInFive(Long a, Long b){

        return Math.abs(a - b) < 1000;
    }
    public static String getGapTime(Long time) {
        LocalDateTime currentTime = new LocalDateTime();
        LocalDateTime commentTime = new LocalDateTime(time);
        Log.i("TIME",new Date().getTime()+"");
        Long gap = new Date().getTime() - time;
        int dayGap = Days.daysBetween(commentTime.toLocalDate(),currentTime.toLocalDate()).getDays();
        if(dayGap<7) {
            if(dayGap<=1) {
                gap = gap/1000;
                long hour = gap/3600;
                if(hour>=1)
                    return hour+" hours ago";
                else {
                    if(gap/60 ==0)
                        return "Just Now";
                    return gap / 60 + " minutes ago";
                }
            }else
                return commentTime.toString("E, a h:mm");
        }else if(dayGap<28){
            return (dayGap/7) + " weeks ago";
        }else
            return commentTime.toString("MMMM dd, yyyy");
    }

}
