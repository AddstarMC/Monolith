package au.com.addstar.monolith;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MonolithCommand implements CommandExecutor {
    Monolith plugin;

    public MonolithCommand(Monolith instance) {
        plugin = instance;
    }

    // Very simple command handler for Monolith
    // Eventually if this is expanded, it should use Monolith's Command interface.. but I'm not so familiar with that
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Op only for Monolith commands
        if (!sender.isOp()) { return false; }

        // Show help if nothing no arguments given
        if (args.length == 0) {
            showMonolithHelp(sender);
            return true;
        }

        // Check arguments (only debug at this stage)
        switch(args[0].toUpperCase()) {
            case "DEBUG":
                plugin.DebugMode = !plugin.DebugMode;
                sender.sendMessage(ChatColor.RED + "Monolith debugging is now "
                        + (Monolith.getInstance().DebugMode ? "ON" : "OFF"));
                break;

            default:
                showMonolithHelp(sender);
                break;
        }
        return true;
    }

    private void showMonolithHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Monolith commands:");
        sender.sendMessage(ChatColor.GREEN + "   /monolith debug - Toggle debug");
    }
}