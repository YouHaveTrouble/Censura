package eu.endermite.censura.command;

import eu.endermite.censura.Censura;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.PluginDescriptionFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CensuraCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendCredits(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("censura.reload")) {
                Censura.getPlugin().asyncReloadConfigCache(sender);
            } else {
                sender.sendMessage(Censura.getCachedConfig().getNoPermission());
            }
        } else {
            sender.sendMessage(Censura.getCachedConfig().getNoSuchCommand());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        HashMap<String, String> allSubCommands = new HashMap<>();
        List<String> result = new ArrayList<>();

        allSubCommands.put("reload", "censura.reload");

        if (args.length == 1) {
            for (Map.Entry<String, String> sub : allSubCommands.entrySet()) {
                if (sub.getKey().startsWith(args[0].toLowerCase()) && sender.hasPermission(sub.getValue()))
                    result.add(sub.getKey());
            }
        }
        return result;
    }

    public void sendCredits(CommandSender sender) {
        PluginDescriptionFile desc = Censura.getPlugin().getDescription();
        sender.sendMessage("Censura " + desc.getVersion() + " by YouHaveTrouble");
        assert desc.getDescription() != null;
        sender.sendMessage(desc.getDescription());
    }
}

