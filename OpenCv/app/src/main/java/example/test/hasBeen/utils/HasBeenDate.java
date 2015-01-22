package example.test.hasBeen.utils;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.Date;

import example.test.hasBeen.model.HasBeenPhoto;

/**
 * Created by zuby on 2015-01-15.
 */
public class HasBeenDate {
    public static boolean isSameDate(HasBeenPhoto beforePhoto,HasBeenPhoto photo) {
        int k = calculateDateRange(beforePhoto,photo);
        if(k==0) return true;
        return false;
    }
    public static boolean isBeforeThatDate(HasBeenPhoto lastPhoto , HasBeenPhoto currentPhoto){
        if(lastPhoto==null) return false;
//        Log.i("photo time",lastPhoto.getTakenDate().getTime()+"");
//        Log.i("photo time",currentPhoto.getTakenDate().getTime()+"");
        if(currentPhoto.getTakenDate().getTime() - lastPhoto.getTakenDate().getTime() > 1000) return false;
        return true;
    }

    public static Date getDate(long taken_date) {
        return new Date(taken_date);
    }
    public static int calculateDateRange(HasBeenPhoto from, HasBeenPhoto to) {
        LocalDateTime start = new LocalDateTime(from.getTakenDate());
        LocalDateTime end = new LocalDateTime(to.getTakenDate());
        return Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays();
    }

    public static boolean isDateRangeInThree(HasBeenPhoto photo, HasBeenPhoto standardPhoto){
        int k = calculateDateRange(photo,standardPhoto);
        if(k<=5) return true;

        return false;
    }
    public static String convertDate(Date date) {
        LocalDate newDate = new LocalDate(date);
        return newDate.toString("YYYY년 MM월 dd일");
    }
    public static String convertTime(Date startTime, Date endTime) {
        LocalDateTime start = new LocalDateTime(startTime);
        LocalDateTime end = new LocalDateTime(endTime);
        if(start.toString("hh:mma").equals(end.toString("hh:mma")))
            return start.toString("hh : mma");
        return start.toString("hh : mma – ")+end.toString("hh : mma");
    }
    public static boolean isTimeRangeInFive(Date a, Date b){

        return Math.abs(a.getTime() - b.getTime()) < 5000;
    }

}
