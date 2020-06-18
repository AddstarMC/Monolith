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

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;

public enum CommandSenderType {
    /**
     * Can be Player
     */
    Player,
    /**
     * Can be BlockCommandSender
     */
    Block,
    /**
     * Can be ConsoleCommandSender OR RemoteConsoleCommandSender
     */
    Console;

    public static CommandSenderType from(CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player)
            return Player;
        if (sender instanceof BlockCommandSender)
            return Block;
        if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender)
            return Console;

        return null;
    }

    public boolean matches(CommandSender sender) {
        switch (this) {
            case Player:
                return (sender instanceof org.bukkit.entity.Player);
            case Block:
                return (sender instanceof BlockCommandSender);
            case Console:
                return (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender);
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case Block:
                return "Command Block";
            case Console:
                return "Console";
            case Player:
                return "Player";
            default:
                return name();
        }
    }
}
