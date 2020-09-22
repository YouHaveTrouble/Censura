package eu.endermite.censura;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
        if (exceptions == null || matches == null) {
            return false;
        }
        for (String exception : exceptions) {
            if (string.contains(exception)) {
                string = string.replaceAll(exception, "*");
            }
        }
        for (String match : matches) {
            if (string.contains(match)) {
                return true;
            }
        }
        return false;
    }

    public static boolean filter(String message, Player player) {

        if (player.isOp() && Censura.getCachedConfig().getOpBypass())
            return false;

        if (player.hasPermission("censura.bypass")) {
            return false;
        }

        String string = normalizedString(message);

        if (detectPhrases(string, "severe")) {
            doActions(Censura.getCachedConfig().getSeverePunishments(), player);
            return true;
        }

        if (detectPhrases(string, "normal")) {
            doActions(Censura.getCachedConfig().getNormalPunishments(), player);
            return true;
        }

        if (detectPhrases(string, "lite")) {
            doActions(Censura.getCachedConfig().getLitePunishments(), player);
            return true;
        }
        return false;
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
