package au.com.addstar.monolith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import au.com.addstar.monolith.util.nbtapi.ItemNBTAPI;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import au.com.addstar.monolith.chat.ChatMessage;
import au.com.addstar.monolith.internal.GeSuitHandler;
import au.com.addstar.monolith.lookup.Lookup;

public class Monolith extends JavaPlugin
{
	private static Monolith mInstance;
	private GeSuitHandler mGeSuitHandler;
	private ItemNBTAPI api;
	
	public static Monolith getInstance()
	{
		return mInstance;
	}
	
	@Override
	public void onEnable()
	{
		String version;

		try {

			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

		} catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
			whatVersionAreYouUsingException.printStackTrace();
			version = null;
		}
		getLogger().info("Your server is running version " + version);

        if (version != null && version.equals("v1_12_R1")) {
            mInstance = this;
			Lookup.initialize(this);
			Bukkit.getPluginManager().registerEvents(new Listeners(), this);
			mGeSuitHandler = new GeSuitHandler(this);
			api = new ItemNBTAPI();
			api.onEnable();
			getLogger().info("enabled");
		} else {
			getLogger().severe("This plugin is for NMS Version 1.12.R1 or Server Version 1.12 Disabled");
		}
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

	public static List<String> matchStrings(String prefix, Collection<String> values)
	{
		ArrayList<String> matches = new ArrayList<>();
		
		prefix = prefix.toLowerCase();
		for(String value : values)
		{
			if(value.toLowerCase().startsWith(prefix))
				matches.add(value);
		}
		
		return matches;
	}
	
	public GeSuitHandler getGeSuitHandler()
	{
		return mGeSuitHandler;
	}
}
