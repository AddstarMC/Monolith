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
		Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "bungeecord:gesuitapi",
				mWaiter);
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "bungeecord:gesuitapi");
	}
	
	public ListenableFuture<List<PlayerDefinition>> lookupPlayerNames(Iterable<String> names)
	{
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
		toSend.sendPluginMessage(mPlugin, "bungeecord:gesuitapi", Message.save(message));

		return mWaiter.waitForReply(message);
	}
	
	public ListenableFuture<List<PlayerDefinition>> lookupPlayerUUIDs(Iterable<UUID> ids)
	{
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
		toSend.sendPluginMessage(mPlugin, "bungeecord:gesuitapi", Message.save(message));

		return mWaiter.waitForReply(message);
	}
}
