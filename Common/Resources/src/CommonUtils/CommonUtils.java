package CommonUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class CommonUtils {
    /**
     * Checks if the given string is a valid email address.
     * @param email - the email address to check
     * @return true if the string is a valid email address false otherwise
     */
    public static Boolean isEmailAddressValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    /**
     * Checks if the given value is a positive number.
     * @param value - the value to check.
     * @return true if the value is a positive number false otherwise
     */
    public static Boolean isPositiveNumber(int value)
    {
        return value > 0;
    }
}
