package CommonClient;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Utils {
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

    public static Boolean isIDValid(String ID) {
        // Check if the string length is exactly 9 characters
        if (ID.length() != 9) {
            return false;
        }

        // Check if each character is a digit
        return checkContainsDigitsOnly(ID);
    }

    public static Boolean checkContainsDigitsOnly(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts a date and time string to a {@link Timestamp} object.
     *
     * @param date The date string in ISO local date format.
     * @param time The time string in 24-hour format.
     * @return A {@link Timestamp} representing the combined date and time.
     */
    public static Timestamp convertStringToTimestamp(String date, String time) {
        // Combine Date and Time Strings
        String dateTimeString = date + "T" + time;

        // Define the formatter for LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // Parse the String to LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

        // Convert LocalDateTime to java.sql.Timestamp
        Timestamp timestamp = Timestamp.valueOf(dateTime);

        return timestamp;
    }

    public static boolean isOrderTimeValid(String date, String time) {

        // Combine Date and Time Strings
        String dateTimeString = date + "T" + time;
        // Format of the date-time string
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // Parse the date-time string
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

        // Get the current date-time
        LocalDateTime now = LocalDateTime.now();

        // Calculate the duration between the given date-time and now
        Duration duration = Duration.between(now, dateTime);

        // Check if the duration is more than 24 hours
        return duration.toMinutes() > 1440;
    }
}
