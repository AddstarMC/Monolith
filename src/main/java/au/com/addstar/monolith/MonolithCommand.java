/*
 * Copyright (c) 2020. AddstarMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute,
 *  sublicense, and/or copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

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
        if (!sender.isOp()) {
            return false;
        }

        // Show help if nothing no arguments given
        if (args.length == 0) {
            showMonolithHelp(sender);
            return true;
        }

        // Check arguments (only debug at this stage)
        switch (args[0].toUpperCase()) {
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