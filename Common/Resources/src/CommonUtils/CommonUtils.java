package CommonUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class CommonUtils {
    public static Boolean isEmailAddressValid(String str)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(str);

        return matcher.matches();
    }
}
