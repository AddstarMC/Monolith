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

package au.com.addstar.monolith.chat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import au.com.addstar.monolith.Monolith;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_15_R1.PlayerConnection;

/**
 * @deprecated use PaperMC Title Api
 */
@Deprecated
public class Title {
    private ChatMessage mTitle;
    private ChatMessage mSubtitle;

    private int mFadeInTime;
    private int mFadeOutTime;
    private int mDisplayTime;
    private Set<Player> shownPlayers = Collections.emptySet();

    /**
     * Sets the amount of time to fade in
     *
     * @param time The time in {@code unit}
     * @param unit The time unit for {@code time}
     */
    public void setFadeInTime(long time, TimeUnit unit) {
        mFadeInTime = (int) (unit.toMillis(time) / 50);
    }

    /**
     * Sets the amount of time to fade out
     *
     * @param time The time in {@code unit}
     * @param unit The time unit for {@code time}
     */
    public void setFadeOutTime(long time, TimeUnit unit) {
        mFadeOutTime = (int) (unit.toMillis(time) / 50);
    }

    /**
     * Sets the amount of time to display the title for (not including fades)
     *
     * @param time The time in {@code unit}
     * @param unit The time unit for {@code time}
     */
    public void setDisplayTime(long time, TimeUnit unit) {
        mDisplayTime = (int) (unit.toMillis(time) / 50);
    }

    /**
     * Sets the amount of time to display the title. No fades will be used
     *
     * @param time The total time in {@code unit}
     * @param unit The time unit for {@code time}
     */
    public void setTime(long time, TimeUnit unit) {
        mDisplayTime = (int) (unit.toMillis(time) / 50);
        mFadeInTime = mFadeOutTime = 0;
    }

    /**
     * Sets display and fade times at once
     *
     * @param fadeIn  The time to fade in the message in {@code unit}
     * @param display The time to display the message in {@code unit}
     * @param fadeOut The time to fade out the message in {@code unit}
     * @param unit    The time unit for all times
     */
    public void setTime(long fadeIn, long display, long fadeOut, TimeUnit unit) {
        mFadeInTime = (int) (unit.toMillis(fadeIn) / 50);
        mDisplayTime = (int) (unit.toMillis(display) / 50);
        mFadeOutTime = (int) (unit.toMillis(fadeOut) / 50);
    }

    /**
     * Sets the title line
     *
     * @param message The message to display
     */
    public void setTitle(String message) {
        mTitle = ChatMessage.begin(message);
    }

    /**
     * Sets the title line
     *
     * @param message The message to display
     */
    public void setTitle(ChatMessage message) {
        mTitle = message;
    }

    /**
     * Sets the subtitle line
     *
     * @param message The message to display
     */
    public void setSubtitle(String message) {
        mSubtitle = ChatMessage.begin(message);
    }

    /**
     * Sets the subtitle line
     *
     * @param message The message to display
     */
    public void setSubtitle(ChatMessage message) {
        mSubtitle = message;
    }

    /**
     * Shows this title to all players on the server
     */
    public void showAll() {
        show(Bukkit.getOnlinePlayers());
    }

    /**
     * Shows this title to a specific player
     *
     * @param player The player to show to
     */
    public void show(Player player) {
        show(Collections.singletonList(player));
    }

    /**
     * Shows this title to a bunch of players
     *
     * @param players The players to show
     */
    public void show(Iterable<? extends Player> players) {
        final Set<Player> playerSet = Sets.newHashSet(players);
        shownPlayers = playerSet;

        // Prepare packets to send
        PacketPlayOutTitle setupTitle;
        PacketPlayOutTitle setupSubtitle;
        PacketPlayOutTitle setupTime;

        if (mTitle != null)
            setupTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, mTitle.toComponents()[0]);
        else
            setupTitle = null;

        if (mSubtitle != null)
            setupSubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, mSubtitle.toComponents()[0]);
        else
            setupSubtitle = null;

        setupTime = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, mFadeInTime, mDisplayTime, mFadeOutTime);

        // Send the packets
        for (Player player : shownPlayers) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            if (setupTitle != null)
                connection.sendPacket(setupTitle);
            if (setupSubtitle != null)
                connection.sendPacket(setupSubtitle);

            // This will display it
            connection.sendPacket(setupTime);
        }

        // Clean up the player set, use the specific instance so we dont clear overriding invoks
        Bukkit.getScheduler().runTaskLater(Monolith.getInstance(), playerSet::clear, mFadeInTime + mDisplayTime + mFadeOutTime);
    }

    /**
     * Hides this title immediately from anyone it was shown to
     */
    public void hide() {
        PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.CLEAR, null);

        for (Player player : shownPlayers)
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
