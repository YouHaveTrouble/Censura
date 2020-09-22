package eu.endermite.censura.config;

import eu.endermite.censura.Censura;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    private static final Censura plugin = Censura.getPlugin();

    public static void setupConfig(String name) {

        File creatorConfigFile = new File(plugin.getDataFolder(), name+".yml");
        FileConfiguration creatorConfig = new YamlConfiguration();

        if (!creatorConfigFile.exists()) {
            creatorConfigFile.getParentFile().mkdirs();
            plugin.saveResource(name+".yml", false);
        }
        try {
            creatorConfig.load(creatorConfigFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
