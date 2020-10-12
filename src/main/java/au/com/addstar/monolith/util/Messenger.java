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

package au.com.addstar.monolith.util;

import au.com.addstar.monolith.Monolith;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/08/2020.
 */
public class Messenger {

    private static BukkitAudiences audiences = BukkitAudiences.create(Monolith.getInstance());

    /**
     * Send a TextComponent to all.
     * @param text {@link TextComponent}
     */
    public static void sendMessageAll(Component text) {
        audiences.all().sendMessage(text);
    }

    /**
     * Send a TextComponent.
     * @param text {@link TextComponent}
     * @param player Monoplayer
     */
    public static void sendMessage(Component text, CommandSender player) {
        audiences.sender(player).sendMessage(text);
    }


    /**
     * Send a Message.
     * @param string String
     * @param player MonoPlayer
     */
    public static void sendMessage(String string, CommandSender player) {
        audiences.sender(player)
              .sendMessage(LegacyComponentSerializer.legacySection().deserialize(string));
    }

    /**
     * Send a title to a player.
     * @param title {@link TextComponent}
     * @param subTitle {@link TextComponent}
     * @param player MonoPlayer
     */
    public static void sendTitle(TextComponent title, TextComponent subTitle, CommandSender player) {
        Title out = Title.title(title,subTitle);
        sendTitle(out,player);
    }

    /**
     * Send a title to all.
     * @param title {@link Title}
     */
    public static void sendTitleAll(Title title) {
        audiences.all().showTitle(title);
    }

    /**
     * Send a title to a player.
     * @param title {@link Title}
     * @param player MonoPlayer
     */
    public static void sendTitle(Title title, CommandSender player) {
        audiences.sender(player).showTitle(title);
    }

    public static void sendActionBar(Component component, CommandSender player) {
        audiences.sender(player).sendActionBar(component);
    }

    public static Component parseString(String string) {
            return MiniMessage.get().parse(string);
    }


}
