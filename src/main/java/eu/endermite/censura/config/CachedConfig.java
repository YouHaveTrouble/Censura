package eu.endermite.censura.config;

import eu.endermite.censura.Censura;
import eu.endermite.censura.filter.MatchType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CachedConfig {
    List<FilterCategory> categories = new ArrayList<>();
    CharReplacementMap replacementMap;
    List<String> commandsToFilter = new ArrayList<>();

    String noPermission, noSuchCommand, configReloaded, kickBadName;
    boolean opBypass, kickOnJoin, logDetections;

    public CachedConfig(FileConfiguration config) {
        Censura plugin = Censura.getPlugin();
        ConfigurationSection filter = config.getConfigurationSection("filter");
        if (filter == null) {
            plugin.getLogger().severe("Configuration malformed! No filter section found.");
            plugin.getLogger().severe("Try deleting current config files and regenerating them.");
            return;
        }

        ConfigurationSection replacements = config.getConfigurationSection("replacements");
        if (replacements == null) {
            plugin.getLogger().severe("Configuration malformed! No replacements section found or it is invalid");
            plugin.getLogger().severe("Try deleting current config files and regenerating them.");
            return;
        }
        replacementMap = new CharReplacementMap(replacements.getValues(false));

        Set<String> filterCategories = filter.getKeys(false);
        for (String filterCategory : filterCategories) {
            ConfigurationSection categorySection = filter.getConfigurationSection(filterCategory);
            if (categorySection == null) {
                plugin.getLogger().severe("Configuration malformed! No category section found or it is invalid: "+filterCategory);
                plugin.getLogger().severe("Try deleting current config files and regenerating them.");
                return;
            }

            ArrayList<MatchType> matches = new ArrayList<>();

            List<?> matchList = categorySection.getList("match");
            if (matchList == null) {
                plugin.getLogger().severe("Configuration malformed!");
                plugin.getLogger().severe(filterCategory + " doesn't contain a match section.");
                return;
            }

            // This is a list of either strings or maps
            for (Object matchObject : matchList) {
                if (matchObject == null) continue;
                if (matchObject instanceof Integer) matchObject = matchObject.toString();
                if (matchObject instanceof String) {
                    matches.add(MatchType.fromString(null, (String)matchObject));
                } else if (matchObject instanceof Map) {
                    Map<?,?> map = (Map<?,?>)matchObject;
                    if (map.size() != 1) {
                        plugin.getLogger().warning("Expected only one object in map. This usually means you forgot to add a '-' in front of a match.");
                    }
                    Map.Entry<?,?> entry = map.entrySet().stream().findFirst().orElseThrow(IllegalStateException::new);

                    if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                        MatchType match = MatchType.fromString((String)entry.getValue(), (String)entry.getKey());
                        if (match == null) {
                            plugin.getLogger().warning(entry.getValue() + " is not a valid type! Skipping...");
                        } else {
                            matches.add(match);
                        }
                    } else {
                        plugin.getLogger().warning(matchObject + " in " + filterCategory + " does not contain strings.");
                    }
                } else {
                    plugin.getLogger().warning(matchObject + " in " + filterCategory + " is not a string nor a map. Instead it's a: " + matchObject.getClass().getSimpleName());
                }
            }

            this.categories.add(new FilterCategory(
                    matches,
                    categorySection.getStringList("action")
            ));
        }


        commandsToFilter.addAll(config.getStringList("filtered-commands"));
        opBypass = config.getBoolean("op-bypass", true);
        kickOnJoin = config.getBoolean("kick-on-bad-name", true);
        logDetections = config.getBoolean("log-detections", true);

        ConfigurationSection messages = config.getConfigurationSection("messages");
        if (messages == null) {
            plugin.getLogger().severe("Configuration malformed! No messages section found.");
            plugin.getLogger().severe("Try deleting current config files and regenerating them.");
            return;
        }
        noPermission = messages.getString("no-permission", "Censura - &cYou don't have permission to do this.");
        noSuchCommand = messages.getString("no-such-command", "Censura - &cThere is no such command.");
        configReloaded = messages.getString("config-reloaded", "Censura - &aConfiguration reloaded.");
        kickBadName = messages.getString("kick-bad-name", "Censura\n&cYour name contains bad words!");
    }

    public List<FilterCategory> getCategories() {
        return categories;
    }

    public CharReplacementMap getReplacementMap() {
        return replacementMap;
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

    public boolean isLogDetections() {
        return logDetections;
    }

    public static class FilterCategory {
        final List<MatchType> matches;
        final List<String> punishments;

        public FilterCategory(List<MatchType> matches, List<String> punishments) {
            this.matches = matches;
            this.punishments = punishments;
        }

        public List<MatchType> getMatches() {
            return matches;
        }

        public List<String> getPunishments() {
            return punishments;
        }
    }
}
