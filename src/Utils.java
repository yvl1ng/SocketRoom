import java.util.regex.Pattern;

public class Utils {
    public static boolean isValidIP(String ip) {
        String ipv4Regex = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        String ipv6Regex = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";
        return Pattern.matches(ipv4Regex, ip) || Pattern.matches(ipv6Regex, ip);
    }

    public static boolean isValidPort(String port) {
        String portRegex = "^[0-9]{1,5}$";
        return Pattern.matches(portRegex, port);
    }
}
