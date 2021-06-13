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

package au.com.addstar.monolith.internal;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import au.com.addstar.monolith.internal.messages.Message;
import au.com.addstar.monolith.internal.messages.MessageResolvePlayer;
import au.com.addstar.monolith.internal.messages.MessageResolveUUID;
import au.com.addstar.monolith.lookup.PlayerDefinition;

import com.google.common.util.concurrent.ListenableFuture;

public class GeSuitHandler {
    private final MessageWaiter mWaiter;
    private final Random mRand;
    private final Plugin mPlugin;
    private final String geSuitChannel = "gesuit:api";

    public GeSuitHandler(Plugin plugin) {
        mWaiter = new MessageWaiter();
        mPlugin = plugin;
        mRand = new Random();
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, geSuitChannel, mWaiter);
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, geSuitChannel);
    }

    public ListenableFuture<List<PlayerDefinition>> lookupPlayerNames(Iterable<String> names) {
        // Now using this method thanks to the damn invalid method crap bukkit did (Bukkit.getOnlinePlayers() returns either a collection or array when compiling.)
        Player toSend = null;
        for (Player player : Bukkit.getOnlinePlayers()) {
            toSend = player;
            break;
        }
        if (toSend == null)
            throw new IllegalStateException("Messaging not available");

        int requestId = mRand.nextInt();
        MessageResolvePlayer message = new MessageResolvePlayer(requestId, names);
        toSend.sendPluginMessage(mPlugin, geSuitChannel, Message.save(message));

        return mWaiter.waitForReply(message);
    }

    public ListenableFuture<List<PlayerDefinition>> lookupPlayerUUIDs(Iterable<UUID> ids) {
        // Now using this method thanks to the damn invalid method crap bukkit did (Bukkit.getOnlinePlayers() returns either a collection or array when compiling.)
        Player toSend = null;
        for (Player player : Bukkit.getOnlinePlayers()) {
            toSend = player;
            break;
        }
        if (toSend == null)
            throw new IllegalStateException("Messaging not available");

        int requestId = mRand.nextInt();
        MessageResolveUUID message = new MessageResolveUUID(requestId, ids);
        toSend.sendPluginMessage(mPlugin, geSuitChannel, Message.save(message));

        return mWaiter.waitForReply(message);
    }
}
