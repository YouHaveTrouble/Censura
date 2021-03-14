package eu.endermite.censura.config;

import eu.endermite.censura.Censura;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CachedConfig {

    List<String> liteExceptions = new ArrayList<>();
    List<Pattern> liteMatches = new ArrayList<>();
    List<String> litePunishments = new ArrayList<>();

    List<String> normalExceptions = new ArrayList<>();
    List<Pattern> normalMatches = new ArrayList<>();
    List<String> normalPunishments = new ArrayList<>();

    List<String> severeExceptions = new ArrayList<>();
    List<Pattern> severeMatches = new ArrayList<>();
    List<String> severePunishments = new ArrayList<>();

    List<String> commandsToFilter = new ArrayList<>();

    String noPermission, noSuchCommand, configReloaded, kickBadName;
    boolean opBypass, kickOnJoin;

    public CachedConfig(FileConfiguration config) {

        ConfigurationSection lite = config.getConfigurationSection("filter.light");
        if (lite == null) {
            Censura.getPlugin().getLogger().severe("Configuration malformed!");
            Censura.getPlugin().getLogger().severe("Try deleting current config files and regenerating them.");
            return;
        }

        litePunishments = lite.getStringList("action");

        ConfigurationSection liteMatch = lite.getConfigurationSection("match");

        for (String liteMatchString : liteMatch.getKeys(false)) {
            liteMatches.add(Pattern.compile(liteMatchString));
            liteExceptions.addAll(liteMatch.getStringList(liteMatchString + ".exceptions"));
        }

        ConfigurationSection normal = config.getConfigurationSection("filter.normal");
        if (normal == null) {
            Censura.getPlugin().getLogger().severe("Configuration malformed!");
            Censura.getPlugin().getLogger().severe("Try deleting current config files and regenerating them.");
            return;
        }

        normalPunishments = normal.getStringList("action");

        ConfigurationSection normalMatch = normal.getConfigurationSection("match");

        for (String normalMatchString : normalMatch.getKeys(false)) {
            normalMatches.add(Pattern.compile(normalMatchString));
            normalExceptions.addAll(normalMatch.getStringList(normalMatchString + ".exceptions"));
        }

        ConfigurationSection severe = config.getConfigurationSection("filter.severe");
        if (severe == null) {
            Censura.getPlugin().getLogger().severe("Configuration malformed!");
            Censura.getPlugin().getLogger().severe("Try deleting current config files and regenerating them.");
            return;
        }

        severePunishments = severe.getStringList("action");

        ConfigurationSection severeMatch = severe.getConfigurationSection("match");

        for (String severeMatchString : severeMatch.getKeys(false)) {
            severeMatches.add(Pattern.compile(severeMatchString));
            severeExceptions.addAll(severeMatch.getStringList(severeMatchString + ".exceptions"));
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

    public List<Pattern> getLiteMatches() {
        return liteMatches;
    }

    public List<String> getLiteExceptions() {
        return liteExceptions;
    }

    public List<String> getLitePunishments() {
        return litePunishments;
    }

    public List<Pattern> getNormalMatches() {
        return normalMatches;
    }

    public List<String> getNormalExceptions() {
        return normalExceptions;
    }

    public List<String> getNormalPunishments() {
        return normalPunishments;
    }

    public List<Pattern> getSevereMatches() {
        return severeMatches;
    }

    public List<String> getSevereExceptions() {
        return severeExceptions;
    }

    public List<String> getSeverePunishments() {
        return severePunishments;
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

}
