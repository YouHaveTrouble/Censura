package eu.endermite.censura.config;

import eu.endermite.censura.Censura;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class CachedConfig {
    List<FilterCategory> categories = new ArrayList<>();

    List<String> commandsToFilter = new ArrayList<>();

    String noPermission, noSuchCommand, configReloaded, kickBadName;
    boolean opBypass, kickOnJoin;

    public CachedConfig(FileConfiguration config) {
        ConfigurationSection filter = config.getConfigurationSection("filter");
        if (filter == null) {
            Censura.getPlugin().getLogger().severe("Configuration malformed!");
            Censura.getPlugin().getLogger().severe("Try deleting current config files and regenerating them.");
            return;
        }

        Set<String> filterCategories = filter.getKeys(false);
        for (String filterCategory : filterCategories) {
            ConfigurationSection categorySection = filter.getConfigurationSection(filterCategory);

            ArrayList<Pattern> matches = new ArrayList<>();
            ArrayList<String> exceptions = new ArrayList<>();

            ConfigurationSection matchSection = categorySection.getConfigurationSection("match");
            for (String matchString : matchSection.getKeys(false)) {
                matches.add(Pattern.compile(matchString));
                exceptions.addAll(matchSection.getStringList(matchString + ".exceptions"));
            }

            this.categories.add(new FilterCategory(
                    matches,
                    exceptions,
                    categorySection.getStringList("action")
            ));
        }


        commandsToFilter.addAll(config.getStringList("filtered-commands"));
        opBypass = config.getBoolean("op-bypass", true);
        kickOnJoin = config.getBoolean("kick-on-bad-name", true);

        ConfigurationSection messages = config.getConfigurationSection("messages");
        noPermission = messages.getString("no-permission", "Censura - &cYou don't have permission to do this.");
        noSuchCommand = messages.getString("no-such-command", "Censura - &cThere is no such command.");
        configReloaded = messages.getString("config-reloaded", "Censura - &aConfiguration reloaded.");
        kickBadName = messages.getString("kick-bad-name", "Censura\n&cYour name contains bad words!");

    }

    public List<FilterCategory> getCategories() {
        return categories;
    }

    public List<String> getCommandsToFilter() {
        return commandsToFilter;
    }

    public String getNoPermission() {
        return ChatColor.translateAlternateColorCodes('&', noPermission);
    }

    public String getNoSuchCommand() {
        return ChatColor.translateAlternateColorCodes('&', noSuchCommand);
    }

    public String getConfigReloaded() {
        return ChatColor.translateAlternateColorCodes('&', configReloaded);
    }

    public String getKickBadName() {
        return ChatColor.translateAlternateColorCodes('&', kickBadName);
    }

    public boolean getOpBypass() {
        return opBypass;
    }

    public boolean getKickOnJoin() {
        return kickOnJoin;
    }

    public static class FilterCategory {
        final List<Pattern> matches;
        final List<String> exceptions;
        final List<String> punishments;

        public FilterCategory(List<Pattern> matches, List<String> exceptions, List<String> punishments) {
            this.matches = matches;
            this.exceptions = exceptions;
            this.punishments = punishments;
        }

        public List<Pattern> getMatches() {
            return matches;
        }

        public List<String> getExceptions() {
            return exceptions;
        }

        public List<String> getPunishments() {
            return punishments;
        }
    }
}
