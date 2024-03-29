package CommonUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * CommonUtils provides utility methods for various tasks.
 */
public class CommonUtils {

    public static Integer DB_ERROR = 1;
    public static Integer SERVER_ERROR = 2;

    /**
     * Checks if the given string is a valid email address.
     *
     * @param email The email address to check.
     * @return True if the string is a valid email address, false otherwise.
     */
    public static boolean isEmailAddressValid(String email) {
        if (email == null)
            return false;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    /**
     * Checks if the given order ID is valid.
     *
     * @param orderID String representation of the order ID.
     * @return True if the order ID is valid (positive integer), false otherwise.
     */
    public static boolean isValidOrderID(String orderID) {
        return (isAllDigits(orderID) && Integer.parseInt(orderID) > 0);
    }

    /**
     * Converts a string to an integer.
     *
     * @param str The string to convert.
     * @return The integer value of the string, or 0 if not a valid integer.
     */
    public static int convertStringToInt(String str) {
        try {
            // Convert the string to an int
            int intValue = Integer.parseInt(str);

            return intValue;
        } catch (NumberFormatException e) {
            // Handle the case when the string is not a valid integer
            System.err.println("Error: Not a valid integer");
        }
        return 0;
    }

    /**
     * Checks if the given id is value (9 digits).
     *
     * @param id - string of the id to check.
     * @return true if the id is valid, false otherwise.
     */
    public static boolean isValidID(String id) {
        return (isAllDigits(id) && id.length() == 9);
    }

    /**
     * Checks if the given name is valid.
     *
     * @param name - string of the name to check.
     * @return true if the name is valid, false otherwise.
     */
    public static boolean isValidName(String name) {
        if (name == null)
            return false;
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isLetter(name.charAt(i)))
                return false;
        }
        return true;
    }

    /**
     * Checks if the given phone number is valid.
     *
     * @param phone - string of the phone number to check.
     * @return true if the phone number is valid, false otherwise.
     */
    public static boolean isValidPhone(String phone) {
        return (isAllDigits(phone) && phone.startsWith("05") && phone.length() == 10);
    }


    /**
     * Checks if the given string contains only digits.
     *
     * @param str - string to check.
     * @return true if the string contains only digits, false otherwise.
     */
    public static boolean isAllDigits(String str) {
        if (str == null)
            return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }

    /**
     * Checks if any of the provided strings are empty.
     *
     * @param strings Varargs parameter to accept multiple string arguments.
     * @return true if any string is empty, false otherwise.
     */
    public static boolean anyStringEmpty(String... strings) {
        for (String str : strings) {
            if (str == null || str.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts a timestamp string to minutes.
     *
     * @param timestamp - the timestamp to convert.
     * @return the timestamp in minutes.
     */
    public static Integer convertTimestampToMinutes(Timestamp timestamp) {
        String[] time = timestamp.toString().split(" ")[1].split(":");
        return Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1]);
    }

    /**
     * Converts minutes to a timestamp.
     *
     * @param minutes - the minutes to convert.
     * @return the timestamp.
     */
    public static Timestamp convertMinutesToTimestamp(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;

        Date date = new Date();

        // Create a SimpleDateFormat instance with "yyyy-MM-dd" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return Timestamp.valueOf(dateFormat.format(date) + " " + hours + ":" + mins + ":00");
    }

    /**
     * Retrieves a list of valid hours within the next week starting from the given timestamp.
     *
     * @param startTimestamp The starting timestamp.
     * @return A list of valid hours (within 8:00 AM to 8:59 PM) for the next week.
     */
    public static List<Timestamp> getNextWeekHours(Timestamp startTimestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTimestamp.getTime());
        List<Timestamp> validHours = new ArrayList<>();
        Calendar endTimestamp = (Calendar) cal.clone();
        endTimestamp.add(Calendar.DAY_OF_MONTH, 7);
        cal.add(Calendar.HOUR_OF_DAY, 1);
        while (cal.before(endTimestamp)) {
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (hour >= 8 && hour < 21) {
                validHours.add(new Timestamp(cal.getTimeInMillis()));
            }
            cal.add(Calendar.HOUR_OF_DAY, 1);
        }

        return validHours;
    }

    /**
     * Converts a combined date and time string to a java.sql.Timestamp.
     *
     * @param date The date string (ISO format: yyyy-MM-dd).
     * @param time The time string (ISO format: HH:mm:ss).
     * @return The corresponding java.sql.Timestamp.
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

    /**
     * Parses the visit date from a Timestamp object.
     *
     * @param visitTime The Timestamp object representing the visit date and time.
     * @return The visit date as a string in "yyyy-MM-dd" format.
     */
    public static String parseVisitDate(Timestamp visitTime) {
        // Convert the Timestamp to a Date object
        Date date = new Date(visitTime.getTime());

        // Create a SimpleDateFormat instance with "yyyy-MM-dd" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Format the Date object into a string
        return dateFormat.format(date);
    }

    /**
     * Parses the visit time from a Timestamp object.
     *
     * @param visitTime The Timestamp object representing the visit date and time.
     * @return The visit time as a string in "HH:mm:ss" format.
     */
    public static String parseVisitTime(Timestamp visitTime) {
        // Create a SimpleDateFormat instance with "yyyy-MM-dd" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        // Format the Date object into a string
        return dateFormat.format(visitTime);
    }

    /**
     * Checks if the current time is between the given start and end times.
     *
     * @param startTime The start time.
     * @param endTime   The end time.
     * @return True if the current time is between the start and end times, false otherwise.
     */
    public static boolean isTimeBetween(Timestamp startTime, Timestamp endTime) {
        // Generate the current timestamp
        Timestamp currentTime = Timestamp.from(Instant.now());

        // Convert Timestamps directly to LocalDateTime for comparison
        LocalDateTime startDateTime = startTime.toLocalDateTime();
        LocalDateTime endDateTime = endTime.toLocalDateTime();
        LocalDateTime currentDateTime = currentTime.toLocalDateTime();

        // Check if the current date-time is between start and end date-times
        if (startDateTime.isBefore(endDateTime)) {
            return !currentDateTime.isBefore(startDateTime) && !currentDateTime.isAfter(endDateTime);
        } else {
            // For intervals that cross midnight (start date-time is after end date-time),
            // check if current date-time is after start date-time or before end date-time
            return !currentDateTime.isBefore(startDateTime) || !currentDateTime.isAfter(endDateTime);
        }

    }

}
