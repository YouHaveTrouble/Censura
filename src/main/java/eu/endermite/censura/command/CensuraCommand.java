package eu.endermite.censura.command;

import eu.endermite.censura.Censura;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.List;

public class CensuraCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("censura.reload")) {
                    Censura.getPlugin().asyncReloadConfigCache(sender);
                } else {
                    sender.sendMessage(Censura.getCachedConfig().getNoPermission());
                }
            } else {
                sender.sendMessage(Censura.getCachedConfig().getNoSuchCommand());
            }
        } else {
            sendCredits(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> allSubCommands = new ArrayList<>();
        List<String> result = new ArrayList<>();

        allSubCommands.add("reload");

        if (args.length == 1) {
            for (String sub : allSubCommands) {
                if (sub.startsWith(args[0].toLowerCase()))
                    result.add(sub);
            }
        }
        return result;
    }

    public void sendCredits(CommandSender sender) {
        PluginDescriptionFile desc = Censura.getPlugin().getDescription();
        sender.sendMessage("Censura "+ desc.getVersion()+ " by YouHaveTrouble");
        assert desc.getDescription() != null;
        sender.sendMessage(desc.getDescription());
    }
}

