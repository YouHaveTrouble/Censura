package eu.endermite.censura;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Filter {

    public static String normalizedString(String string) {
        String message = string.toLowerCase();
        message = ChatColor.stripColor(message);
        message = message.replaceAll("0", "o");
        message = message.replaceAll("1", "i");
        message = message.replaceAll("3", "e");
        message = message.replaceAll("4", "a");
        message = message.replaceAll("5", "s");
        message = message.replaceAll("7", "t");
        message = message.replaceAll("9", "g");
        message = message.replaceAll("\\$", "s");
        message = message.replaceAll("@", "a");
        message = message.replaceAll("/[^A-Za-z]/g", "");
        message = message.replaceAll(" ", "");
        return message;
    }

    public static boolean detectPhrases(String string, String mode) {
        List<String> exceptions = null;
        List<String> matches = null;
        if (mode.equalsIgnoreCase("lite")) {
            exceptions = Censura.getCachedConfig().getLiteExceptions();
            matches = Censura.getCachedConfig().getLiteMatches();
        } else if (mode.equalsIgnoreCase("normal")) {
            exceptions = Censura.getCachedConfig().getNormalExceptions();
            matches = Censura.getCachedConfig().getNormalMatches();
        } else if (mode.equalsIgnoreCase("severe")) {
            exceptions = Censura.getCachedConfig().getSevereExceptions();
            matches = Censura.getCachedConfig().getSevereMatches();
        }
        if (matches == null) {
            return false;
        }

        try {
            for (String exception : exceptions) {
                string = string.replaceAll(exception, "*");
            }
        } catch (NullPointerException ignored) {}

        for (String match : matches) {
            Matcher m = Pattern.compile(match).matcher(string);
            if (m.find())
                return true;
        }

        return false;
    }

    public static boolean filter(String message, Player player) {

        if (player.isOp() && Censura.getCachedConfig().getOpBypass())
            return false;

        if (player.hasPermission("censura.bypass")) {
            return false;
        }

        if (detectPhrases(message, "severe") || detectPhrases(normalizedString(message), "severe")) {
            doActions(Censura.getCachedConfig().getSeverePunishments(), player);
            return true;
        }

        if (detectPhrases(noRepeatChars(message), "severe") || detectPhrases(noRepeatChars(normalizedString(message)), "severe")) {
            doActions(Censura.getCachedConfig().getSeverePunishments(), player);
            return true;
        }

        if (detectPhrases(message, "normal") || detectPhrases(normalizedString(message), "normal")) {
            doActions(Censura.getCachedConfig().getNormalPunishments(), player);
            return true;
        }

        if (detectPhrases(noRepeatChars(message), "normal") || detectPhrases(noRepeatChars(normalizedString(message)), "normal")) {
            doActions(Censura.getCachedConfig().getNormalPunishments(), player);
            return true;
        }

        if (detectPhrases(message, "lite") || detectPhrases(normalizedString(message), "lite")) {
            doActions(Censura.getCachedConfig().getLitePunishments(), player);
            return true;
        }

        if (detectPhrases(noRepeatChars(message), "lite") || detectPhrases(noRepeatChars(normalizedString(message)), "lite")) {
            doActions(Censura.getCachedConfig().getLitePunishments(), player);
            return true;
        }
        return false;
    }

    public static boolean filterNoActions(String message) {

        if (detectPhrases(message, "severe") || detectPhrases(normalizedString(message), "severe")) {
            return true;
        }

        if (detectPhrases(noRepeatChars(message), "severe") || detectPhrases(noRepeatChars(normalizedString(message)), "severe")) {
            return true;
        }

        if (detectPhrases(message, "normal") || detectPhrases(normalizedString(message), "normal")) {
            return true;
        }

        if (detectPhrases(noRepeatChars(message), "normal") || detectPhrases(noRepeatChars(normalizedString(message)), "normal")) {
            return true;
        }

        if (detectPhrases(message, "lite") || detectPhrases(normalizedString(message), "lite")) {
            return true;
        }

        if (detectPhrases(noRepeatChars(message), "lite") || detectPhrases(noRepeatChars(normalizedString(message)), "lite")) {
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

}
