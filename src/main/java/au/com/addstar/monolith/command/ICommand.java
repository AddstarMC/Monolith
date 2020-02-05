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

package au.com.addstar.monolith.command;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.command.CommandSender;


public interface ICommand 
{
	/**
	 * Gets the name of the command
	 * @return the name of the Command
	 */
	String getName();
	/**
	 * Gets any aliases this command has
	 * @return an array of the aliases or null if there are none
	 */
	String[] getAliases();
	
	/**
	 * @return Gets the permission that this command needs to be used, or null if there isnt one
	 */
	String getPermission();
	
	/**
	 * Gets the usage string for this command. This should be in the format of: {@code <command> <usage>}
	 * @param label This is either the name of the command or an alias if they used one.
	 * @param sender The sender of the command
	 * @return The usage string  
	 */
	String getUsageString(String label, CommandSender sender);
	
	/**
	 * @return Gets the description of the command for the help system
	 */
	String getDescription();

	/**
	 * @return Gets what types of command senders are allowed
	 */
	EnumSet<CommandSenderType> getAllowedSenders();
	
	/**
	 * Called when this command is executed. By this time the permission has been checked, and if this command does not accept the console as a sender, that wont trigger this command.
	 * @param sender The sender of this command. If canBeConsole() == false, this will only ever be an instance of a Player
	 * @param parent The command name and path of the parent command.
	 * @param label The command name or the alias that was used to call this command
	 * @param args The arguments for this command
	 * @return True if this command was executed. False otherwise
	 */
	boolean onCommand(CommandSender sender, String parent, String label, String[] args) throws BadArgumentException;
	
	/**
	 * Called when tab complete is used on this command. 
	 * @param sender The sender of the tab complete
	 * @param parent The command name and path of the parent command.
	 * @param label The command name or the alias that was used to specity this command
	 * @param args The current arguments entered.
	 * @return A list of all results or null
	 */
	List<String> onTabComplete(CommandSender sender, String parent, String label, String[] args);
}
