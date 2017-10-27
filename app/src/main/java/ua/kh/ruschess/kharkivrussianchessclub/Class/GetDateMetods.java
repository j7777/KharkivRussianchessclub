package ua.kh.ruschess.kharkivrussianchessclub.Class;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class GetDateMetods {

    public String getFullTime(long timeInMillis){
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        c.setTimeZone(TimeZone.getDefault());
        return format.format(c.getTime());
    }

    public long getStampTime(long timeInMillis){
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        c.setTimeZone(TimeZone.getDefault());
        return c.getTimeInMillis();
    }
}
