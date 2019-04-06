package tk.burdukowsky.rutorparser;

public class Utils {

    public static String trimHorizontalWhitespaceChars(String input) {
        return input.replaceAll("(^\\h*)|(\\h*$)", "");
    }

    public static long stringToLong(String input) {
        return Long.parseLong(trimHorizontalWhitespaceChars(input));
    }

}
