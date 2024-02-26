package CommonClientUI;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public  class UtilsUI {
    public static String parseVisitDate(Timestamp visitTime) {
        // Convert the Timestamp to a Date object
        Date date = new Date(visitTime.getTime());

        // Create a SimpleDateFormat instance with "yyyy-MM-dd" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Format the Date object into a string
        return dateFormat.format(date);
    }

    public static String parseVisitTime(Timestamp visitTime) {
        // Create a SimpleDateFormat instance with "yyyy-MM-dd" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        // Format the Date object into a string
        return dateFormat.format(visitTime);
    }
}
