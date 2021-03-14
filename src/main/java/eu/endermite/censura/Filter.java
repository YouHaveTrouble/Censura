package eu.endermite.censura;

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

    public static boolean detectPhrases(String string, FilterStrength mode) {
        List<String> exceptions = null;
        List<Pattern> matches = null;

        switch (mode) {
            case SEVERE:
                exceptions = Censura.getCachedConfig().getSevereExceptions();
                matches = Censura.getCachedConfig().getSevereMatches();
                break;
            case NORMAL:
                exceptions = Censura.getCachedConfig().getNormalExceptions();
                matches = Censura.getCachedConfig().getNormalMatches();
                break;
            case LITE:
                exceptions = Censura.getCachedConfig().getLiteExceptions();
                matches = Censura.getCachedConfig().getLiteMatches();
                break;
        }

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

    public static boolean detect(String message, FilterStrength mode) {
        return detectPhrases(message, mode) ||
                detectPhrases(normalizedString(message), mode) ||
                detectPhrases(noRepeatChars(message), mode) ||
                detectPhrases(noRepeatChars(normalizedString(message)), mode);
    }

    public static boolean filter(String message, Player player) {

        if (player.isOp() && Censura.getCachedConfig().getOpBypass())
            return false;

        if (player.hasPermission("censura.bypass")) {
            return false;
        }

        if (detect(message, FilterStrength.SEVERE)) {
            doActions(Censura.getCachedConfig().getSeverePunishments(), player);
            return true;
        }

        if (detect(message, FilterStrength.NORMAL)) {
            doActions(Censura.getCachedConfig().getNormalPunishments(), player);
            return true;
        }

        if (detect(message, FilterStrength.LITE)) {
            doActions(Censura.getCachedConfig().getLitePunishments(), player);
            return true;
        }

        return false;
    }

    public static boolean filterNoActions(String message) {

        if (detect(message, FilterStrength.SEVERE)) {
            return true;
        }

        if (detect(message, FilterStrength.NORMAL)) {
            return true;
        }

        if (detect(message, FilterStrength.LITE)) {
            return true;
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
