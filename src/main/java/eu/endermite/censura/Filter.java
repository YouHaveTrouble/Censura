package eu.endermite.censura;

import eu.endermite.censura.config.CachedConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Filter {
    private static final Pattern diacreticMarks = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern singleLetterSurroundedBySpacers = Pattern.compile("^(\\w)\\W+(?=\\w\\w)|\\W+(\\w)((\\W+(?=\\w\\w))|(?!\\w))");

    public static String normalizedString(String string) {
        String message = string.toLowerCase();
        message = Normalizer.normalize(message, Normalizer.Form.NFD);
        message = ChatColor.stripColor(message);
        message = diacreticMarks.matcher(message).replaceAll("");
        message = message.replace('0', 'o');
        message = message.replace('1', 'i');
        message = message.replace('3', 'e');
        message = message.replace('4', 'a');
        message = message.replace('5', 's');
        message = message.replace('7', 't');
        message = message.replace('9', 'g');
        message = message.replace('$', 's');
        message = message.replace('@', 'a');
        message = singleLetterSurroundedBySpacers.matcher(message).replaceAll("$1$2");
        return message;
    }

    public static boolean detectPhrases(String string, CachedConfig.FilterCategory filter) {
        List<String> exceptions = filter.getExceptions();
        List<Pattern> matches = filter.getMatches();

        if (matches == null) {
            return false;
        }

        try {
            for (String exception : exceptions) {
                string = string.replaceAll(exception, "*");
            }
        } catch (NullPointerException ignored) {}

        for (Pattern match : matches) {
            Matcher m = match.matcher(string);
            if (m.find())
                return true;
        }

        return false;
    }

    /**
     * Checks if a snippet is present in the string.
     * The snippet must start and end on a word boundary. Besides that the snippet may be interrupted by any non-alphabetic character. A character may also be repeated.
     *
     * For the snippet {@code test} the following counts:
     * "test": true
     * "aa test aa": true
     * "aatestaa": false
     * "aa te st aa" true
     * "aa t^^e0s--t aa" true
     * "teeessstttt" true
     *
     * @param string string to find snippet in
     * @param snippet snippet to check for
     * @return true if the snippet was found
     */
    public static boolean find(String string, String snippet) {
        char[] detectChars = snippet.toCharArray();
        int state = 0;
        boolean wasSpacer = true; //start of string counts as spacer

        for (char c : string.toCharArray()) {
            if (state >= detectChars.length) {
                if (isSpacer(c)) {
                    return true; //We've reached the end and reached a spacer
                }
            } else if (match(c, detectChars[state])) {
                if (state == 0) {
                    //Can only match the first letter of the snippet after a space
                    if (wasSpacer) state++;
                } else {
                    state++;
                }
            }
            if (state > 0 && !match(c,detectChars[state-1]) && !isSpacer(c)) {
                //This is not a repeated character. We should reset
                state = 0;
            }

            wasSpacer = isSpacer(c);
        }

        if (state >= detectChars.length) return true;

        return false;
    }

    private static boolean isSpacer(char c) {
        return !Character.isAlphabetic(c);
    }

    private static boolean match(char a, char b) {
        if (b == '*') return true;
        return a == b;
    }

    public static boolean detect(String message, CachedConfig.FilterCategory filter) {
        return detectPhrases(message, filter) ||
                detectPhrases(normalizedString(message), filter) ||
                detectPhrases(noRepeatChars(message), filter) ||
                detectPhrases(noRepeatChars(normalizedString(message)), filter);
    }

    public static boolean filter(String message, Player player) {

        if (player.isOp() && Censura.getCachedConfig().getOpBypass())
            return false;

        if (player.hasPermission("censura.bypass")) {
            return false;
        }

        for (CachedConfig.FilterCategory filter : Censura.getCachedConfig().getCategories()) {
            if (detect(message, filter)) {
                doActions(filter.getPunishments(), player);
                return true;
            }
        }

        return false;
    }

    public static boolean filterNoActions(String message) {

        for (CachedConfig.FilterCategory filter : Censura.getCachedConfig().getCategories()) {
            if (detect(message, filter)) {
                return true;
            }
        }

        return false;

    }

    private static String noRepeatChars(String string) {
        char[] chars;
        chars = string.toCharArray();
        StringBuilder result = new StringBuilder();
        char lastChar = ' ';
        for (char c : chars) {
            if (lastChar == c)
                continue;
            lastChar = c;
            result.append(c);
        }
        return result.toString();
    }

    public static void doActions(List<String> actions, Player player) {
        for (String a : actions) {
            if (a.startsWith("command:")) {
                CommandSender sender = Censura.getPlugin().getServer().getConsoleSender();
                String command = a.replaceFirst("command: ", "");
                String cmd = command.replaceAll("%player%", player.getName());
                Censura.getPlugin().getServer().getScheduler().runTask(Censura.getPlugin(), () -> Bukkit.dispatchCommand(sender, cmd));
            } else if (a.startsWith("message:")) {
                String message = a.replaceFirst("message: ", "");
                String msg = message.replaceAll("%player%", player.getName());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }

    public enum FilterStrength {
        SEVERE,
        NORMAL,
        LITE
    }
}
