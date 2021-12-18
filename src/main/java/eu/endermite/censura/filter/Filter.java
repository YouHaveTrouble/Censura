package eu.endermite.censura.filter;

import eu.endermite.censura.Censura;
import eu.endermite.censura.config.CachedConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

public class Filter {
    private static final Pattern diacreticMarks = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * @param string String to check
     * @return true if passed the prefilter
     */
    public static boolean preFilter(String string) {
        String prefilter = Censura.getCachedConfig().getPrefilterRegex();
        if (prefilter == null) return true;
        return string.matches(prefilter);
    }

    public static String preprocessString(String string) {
        String message = string.toLowerCase();
        message = Normalizer.normalize(message, Normalizer.Form.NFD);
        message = ChatColor.stripColor(message);
        message = diacreticMarks.matcher(message).replaceAll("");
        message = Censura.getCachedConfig().getReplacementMap().process(message);
        return message;
    }

    public static boolean detect(String message, CachedConfig.FilterCategory filter) {
        message = preprocessString(message);
        List<MatchType> matches = filter.getMatches();

        FilterCache cache = new FilterCache();
        for (MatchType match : matches) {
            if (match.match(message, cache)) {
                if (Censura.getCachedConfig().isLogDetections())
                    Censura.getPlugin().getLogger().info(String.format("Detected \"%s\" in phrase \"%s\" (type: %s)", match.getSnippet(), message, match.getType()));
                return true;
            }
        }
        return false;
    }

    public static boolean filter(String message, Player player) {
        if (player.isOp() && Censura.getCachedConfig().getOpBypass())
            return false;

        if (player.hasPermission("censura.bypass"))
            return false;

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
            if (detect(message, filter))
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
