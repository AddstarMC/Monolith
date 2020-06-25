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

import java.util.HashMap;

import org.bukkit.entity.Player;

public class MonoPlayer {
    private static final HashMap<Player, MonoPlayer> mPlayers = new HashMap<>();
    private final Player mPlayer;

    private MonoPlayer(Player player) {
        mPlayer = player;
    }

    public static MonoPlayer getPlayer(Player player) {
        MonoPlayer mplayer = mPlayers.get(player);
        if (mplayer == null) {
            mplayer = new MonoPlayer(player);
            mPlayers.put(player, mplayer);
        }

        return mplayer;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public String getLocale() {
        return mPlayer.getLocale();
    }


    protected void onDestroy() {
        mPlayers.remove(mPlayer);
    }

    @Override
    public int hashCode() {
        return mPlayer.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MonoPlayer) return mPlayer.equals(obj);
        return false;
    }

    @Override
    public String toString() {
        return "MonoPlayer: " + mPlayer.getName();
    }

}
