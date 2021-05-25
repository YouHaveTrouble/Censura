package eu.endermite.censura;

import eu.endermite.censura.command.CensuraCommand;
import eu.endermite.censura.config.CachedConfig;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Censura extends JavaPlugin {

    private static Censura plugin;
    private static CachedConfig cachedConfig;

    @Override
    public void onEnable() {
        plugin = this;
        reloadConfigCache();

        try {
            getCommand("censura").setExecutor(new CensuraCommand());
            getCommand("censura").setTabCompleter(new CensuraCommand());
        } catch (NullPointerException e) {
            getLogger().severe("It seems like plugin.yml is missing command info.");
            getLogger().severe("Censura commands will not function properly.");
        }

        int pluginId = 8924;
        Metrics metrics = new Metrics(this, pluginId);

    }

    private void reloadConfigCache() {
        saveDefaultConfig();
        reloadConfig();
        cachedConfig = new CachedConfig();
    }

    public void asyncReloadConfigCache(CommandSender sender) {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            reloadConfigCache();
            sender.sendMessage(getCachedConfig().getConfigReloaded());
        });
    }

    public static Censura getPlugin() {
        return plugin;
    }

    public static CachedConfig getCachedConfig() {
        return cachedConfig;
    }

}
