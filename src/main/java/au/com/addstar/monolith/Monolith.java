package au.com.addstar.monolith;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import au.com.addstar.monolith.chat.ChatMessage;

public class Monolith extends JavaPlugin
{
	private static Monolith mInstance;
	
	public static Monolith getInstance()
	{
		return mInstance;
	}
	
	@Override
	public void onEnable()
	{
		mInstance = this;
		Bukkit.getPluginManager().registerEvents(new Listeners(), this);
	}

	public static void broadcastMessage(ChatMessage message)
	{
		broadcast(message, Server.BROADCAST_CHANNEL_USERS);
	}
	
	public static void broadcast(ChatMessage message, String permission)
	{
		for(Permissible perm : Bukkit.getPluginManager().getPermissionSubscriptions(permission))
		{
			if(perm instanceof CommandSender && perm.hasPermission(permission))
				message.send((CommandSender)perm);
		}
	}
}
