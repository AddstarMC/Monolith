/*
 * Copyright (c) 2021. AddstarMC
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

package au.com.addstar.monolith.command;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;


/**
 * This allows sub commands to be handled in a clean easily expandable way.
 * Just create a new command that implements ICommand
 * Then register it with registerCommand() in the static constructor
 *
 * @author Schmoller
 */
public class CommandDispatcher {
    public static Pattern usageArgumentPattern = Pattern.compile("(\\[<.*?>\\])|(\\[.*?\\])|(<.*?>)");

    private final String mRootCommandDescription;
    private final HashMap<String, ICommand> mCommands;

    private ICommand mDefaultCommand = null;

    public CommandDispatcher(String description) {
        mCommands = new HashMap<>();

        mRootCommandDescription = description;

        registerCommand(new InternalHelp());
    }

    public static String colorUsage(String usage) {
        Matcher matcher = usageArgumentPattern.matcher(usage);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String str;
            if (matcher.group(1) != null)
                str = ChatColor.GREEN + matcher.group(1);
            else if (matcher.group(2) != null)
                str = ChatColor.GREEN + matcher.group(2);
            else
                str = ChatColor.GOLD + matcher.group(3);

            matcher.appendReplacement(buffer, str);
        }

        matcher.appendTail(buffer);

        return buffer.toString();
    }

    /**
     * Registers a command to be handled by this dispatcher
     *
     * @param command the command to register must implement @link{ICommand}
     */
    public void registerCommand(ICommand command) {
        mCommands.put(command.getName().toLowerCase(), command);
    }

    public void setDefault(ICommand command) {
        mDefaultCommand = command;
    }

    public boolean dispatchCommand(CommandSender sender, String parent, String label, String[] args) {
        parent += label + " ";

        if (args.length == 0 && mDefaultCommand == null) {
            displayUsage(sender, parent, label, null);
            return true;
        }

        ICommand com = null;
        String subCommand = "";

        String[] subArgs = args;

        if (args.length > 0) {
            subCommand = args[0].toLowerCase();
            subArgs = (args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);

            if (mCommands.containsKey(subCommand))
                com = mCommands.get(subCommand);
            else {
                com = getAlias(subCommand);
            }
        }

        if (com == null)
            com = mDefaultCommand;

        // Was not found
        if (com == null) {
            displayUsage(sender, parent, label, subCommand);
            return true;
        }

        // Check that the sender is correct
        if (!com.getAllowedSenders().contains(CommandSenderType.from(sender))) {
            if (com == mDefaultCommand)
                displayUsage(sender, parent, label, subCommand);
            else
                sender.sendMessage(ChatColor.RED + String.format("%s %s cannot be called from the %s", label, subCommand, CommandSenderType.from(sender)));
            return true;
        }

        // Check that they have permission
        if (com.getPermission() != null && !sender.hasPermission(com.getPermission())) {
            sender.sendMessage(ChatColor.RED + String.format("You do not have permission to use %s %s", label, subCommand));
            return true;
        }

        try {
            if (!com.onCommand(sender, parent, subCommand, subArgs))
                sender.sendMessage(ChatColor.RED + "Usage: " + parent + com.getUsageString(subCommand, sender));
        } catch (BadArgumentException e) {
            StringBuilder cmdString = new StringBuilder(ChatColor.GRAY + parent);
            for (int i = 0; i < args.length; ++i) {
                if (i == e.getArgument() + 1)
                    cmdString.append(ChatColor.RED).append(args[i]).append(ChatColor.GRAY);
                else
                    cmdString.append(args[i]);

                cmdString.append(" ");
            }

            if (e.getArgument() >= args.length - 1)
                cmdString.append(ChatColor.RED + "?");

            sender.sendMessage(ChatColor.RED + "Error in command: " + cmdString);
            sender.sendMessage(ChatColor.RED + " " + e.getMessage());

            for (String line : e.getInfoLines())
                sender.sendMessage(ChatColor.GRAY + " " + line);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    private void displayUsage(CommandSender sender, String parent, String label, String subcommand) {
        StringBuilder usage = new StringBuilder();

        boolean first = true;
        boolean odd = true;
        // Build the list
        for (ICommand command : mCommands.values()) {
            // Check that the sender is correct
            if (!command.getAllowedSenders().contains(CommandSenderType.from(sender)))
                continue;

            // Check that they have permission
            if (command.getPermission() != null && !sender.hasPermission(command.getPermission()))
                continue;

            if (odd)
                usage.append(ChatColor.WHITE);
            else
                usage.append(ChatColor.GRAY);
            odd = !odd;

            if (first)
                usage.append(command.getName());
            else
                usage.append(", ").append(command.getName());

            first = false;
        }

        if (subcommand != null)
            sender.sendMessage(ChatColor.RED + "Unknown command: " + ChatColor.RESET + parent + ChatColor.GOLD + subcommand);
        else
            sender.sendMessage(ChatColor.RED + "No command specified: " + ChatColor.RESET + parent + ChatColor.GOLD + "<command>");

        if (!first) {
            sender.sendMessage("Valid commands are:");
            sender.sendMessage(usage.toString());
        } else
            sender.sendMessage("There are no commands available to you");


    }

    public List<String> tabComplete(CommandSender sender, String parent, String label, String[] args) {
        parent += label + " ";

        List<String> results = new ArrayList<>();
        if (args.length == 1) // Tab completing the sub command
        {
            for (ICommand registeredCommand : mCommands.values()) {
                if (registeredCommand.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    // Check that the sender is correct
                    if (!registeredCommand.getAllowedSenders().contains(CommandSenderType.from(sender)))
                        continue;

                    // Check that they have permission
                    if (registeredCommand.getPermission() != null && !sender.hasPermission(registeredCommand.getPermission()))
                        continue;

                    results.add(registeredCommand.getName());
                }
            }
        } else {
            // Find the command to use
            String subCommand = args[0].toLowerCase();
            String[] subArgs = (args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);

            ICommand com = null;
            if (mCommands.containsKey(subCommand)) {
                com = mCommands.get(subCommand);
            } else {
                // Check aliases
                com = getAlias(subCommand);
            }

            // Was not found
            if (com == null) {
                return results;
            }

            // Check that the sender is correct
            if (!com.getAllowedSenders().contains(CommandSenderType.from(sender)))
                return results;

            // Check that they have permission
            if (com.getPermission() != null && !sender.hasPermission(com.getPermission()))
                return results;

            results = com.onTabComplete(sender, parent, subCommand, subArgs);
            if (results == null)
                return new ArrayList<>();
        }
        return results;
    }

    private ICommand getAlias(String subCommand) {
        for (Entry<String, ICommand> ent : mCommands.entrySet()) {
            if (ent.getValue().getAliases() != null) {
                String[] aliases = ent.getValue().getAliases();
                for (String alias : aliases) {
                    if (subCommand.equalsIgnoreCase(alias)) {
                        return ent.getValue();
                    }
                }
            }
        }
        return null;
    }

    private class InternalHelp implements ICommand {
        @Override
        public String getName() {
            return "help";
        }

        @Override
        public String[] getAliases() {
            return null;
        }

        @Override
        public String getPermission() {
            return null;
        }

        @Override
        public String getUsageString(String label, CommandSender sender) {
            return label;
        }

        @Override
        public String getDescription() {
            return "Displays this screen.";
        }

        @Override
        public EnumSet<CommandSenderType> getAllowedSenders() {
            return EnumSet.allOf(CommandSenderType.class);
        }

        @Override
        public boolean onCommand(CommandSender sender, String parent, String label, String[] args) {
            if (args.length != 0)
                return false;

            sender.sendMessage("");
            sender.sendMessage(ChatColor.YELLOW + parent + ChatColor.GOLD + "<command>");
            sender.sendMessage(ChatColor.GRAY + "\u25B7 " + mRootCommandDescription);
            sender.sendMessage(ChatColor.YELLOW + "Available commands:");

            if (mDefaultCommand != null) {
                if (mDefaultCommand.getAllowedSenders().contains(CommandSenderType.from(sender)) && (mDefaultCommand.getPermission() == null || sender.hasPermission(mDefaultCommand.getPermission()))) {
                    sender.sendMessage(ChatColor.WHITE + parent + ChatColor.YELLOW + colorUsage(mDefaultCommand.getUsageString(mDefaultCommand.getName(), sender)));

                    String[] descriptionLines = mDefaultCommand.getDescription().split("\n");
                    for (String line : descriptionLines)
                        sender.sendMessage(ChatColor.GRAY + " \u25B7 " + line);
                }
            }

            for (ICommand command : mCommands.values()) {
                // Dont show commands that are irrelevant
                if (!command.getAllowedSenders().contains(CommandSenderType.from(sender)))
                    continue;

                if (command.getPermission() != null && !sender.hasPermission(command.getPermission()))
                    continue;


                sender.sendMessage(" " + ChatColor.WHITE + parent + ChatColor.YELLOW + colorUsage(command.getUsageString(command.getName(), sender)));

                String[] descriptionLines = command.getDescription().split("\n");
                for (String line : descriptionLines)
                    sender.sendMessage(ChatColor.GRAY + " \u25B7 " + line);
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String parent, String label, String[] args) {
            return null;
        }

    }
}
