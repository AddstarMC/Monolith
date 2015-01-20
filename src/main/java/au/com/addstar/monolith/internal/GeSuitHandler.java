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

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;

public class GeSuitHandler
{
	private MessageWaiter mWaiter;
	private Random mRand;
	private Plugin mPlugin;
	
	public GeSuitHandler(Plugin plugin)
	{
		mWaiter = new MessageWaiter();
		mPlugin = plugin;
		mRand = new Random();
		Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "geSuitAPI", mWaiter);
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "geSuitAPI");
	}
	
	public ListenableFuture<List<PlayerDefinition>> lookupPlayerNames(Iterable<String> names)
	{
		Player toSend = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if (toSend == null)
			throw new IllegalStateException("Messaging not available");

		int requestId = mRand.nextInt();
		MessageResolvePlayer message = new MessageResolvePlayer(requestId, names);
		toSend.sendPluginMessage(mPlugin, "geSuitAPI", Message.save(message));

		return mWaiter.waitForReply(message);
	}
	
	public ListenableFuture<List<PlayerDefinition>> lookupPlayerUUIDs(Iterable<UUID> ids)
	{
		Player toSend = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if (toSend == null)
			throw new IllegalStateException("Messaging not available");

		int requestId = mRand.nextInt();
		MessageResolveUUID message = new MessageResolveUUID(requestId, ids);
		toSend.sendPluginMessage(mPlugin, "geSuitAPI", Message.save(message));

		return mWaiter.waitForReply(message);
	}
}
