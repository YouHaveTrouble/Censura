package eu.endermite.censura.config;

import eu.endermite.censura.Censura;
import eu.endermite.censura.filter.MatchType;
import eu.endermite.censura.listener.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CachedConfig {
    FileConfiguration config;
    List<FilterCategory> categories = new ArrayList<>();
    CharReplacementMap replacementMap;
    List<String> commandsToFilter = new ArrayList<>();

    String noPermission, noSuchCommand, configReloaded, kickBadName, prefilterRegex, prefilterFailed;
    boolean opBypass, kickOnJoin, logDetections;

    public CachedConfig() {
        Censura plugin = Censura.getPlugin();
        this.config = plugin.getConfig();

        // Unregister all listeners created by Censura
        HandlerList.unregisterAll(plugin);

        if (config.getBoolean("checks.chat", true))
            registerListener(ChatEventListener.class);

        if (config.getBoolean("checks.sign", true))
            registerListener(SignChangeListener.class);

        if (config.getBoolean("checks.book", true))
            registerListener(BookEditListener.class);

        if (config.getBoolean("checks.command", true))
            registerListener(CommandListener.class);

        if (config.getBoolean("checks.anvil-name", true))
            registerListener(ItemRenameListener.class);

        if (config.getBoolean("checks.nametag-use", true))
            registerListener(EntityRenameListener.class);


        ConfigurationSection filter = config.getConfigurationSection("filter");
        if (filter == null) {
            config.createSection("filter");
            filter = config.getConfigurationSection("filter");
            return;
        }

        ConfigurationSection prefilter = config.getConfigurationSection("prefilter");
        if (prefilter != null && prefilter.getBoolean("enabled", false)) {
            prefilterRegex = prefilter.getString("regex");
            prefilterFailed = prefilter.getString("failed", "Censura - Your input contained disallowed characters.");
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
                continue;
            }

            ArrayList<MatchType> matches = new ArrayList<>();

            List<?> matchList = categorySection.getList("match");
            if (matchList == null) {
                plugin.getLogger().severe("Configuration malformed!");
                plugin.getLogger().severe(filterCategory + " doesn't contain a match section.");
                continue;
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

    public String getPrefilterRegex() {
        return prefilterRegex;
    }

    public String getPrefilterFailed() {
        return ChatColor.translateAlternateColorCodes('&', prefilterFailed);
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

    private void registerListener(Class<?> clazz) {
        try {
            Censura plugin = Censura.getPlugin();
            Listener listener = (org.bukkit.event.Listener) clazz.getConstructor().newInstance();
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
