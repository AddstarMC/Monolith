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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import au.com.addstar.monolith.chat.ChatMessage;
import au.com.addstar.monolith.internal.GeSuitHandler;
import au.com.addstar.monolith.lookup.Lookup;

import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_15_R1.DedicatedServer;
import net.minecraft.server.v1_15_R1.DedicatedServerProperties;

public class Monolith extends JavaPlugin
{
	private static Monolith mInstance;
	private GeSuitHandler mGeSuitHandler;
	public Boolean DebugMode = false;

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
		mInstance = this;
		Lookup.initialize(this);
		Bukkit.getPluginManager().registerEvents(new Listeners(), this);
		getCommand("monolith").setExecutor(new MonolithCommand(this));
		mGeSuitHandler = new GeSuitHandler(this);
		getLogger().info("enabled");

	}

	/**
	 *
	 * @param message the message to broadcast
	 * @deprecated use {@link Monolith#broadcastMessage(BaseComponent[])}
	 */
	@Deprecated
	public static void broadcastMessage(ChatMessage message)
	{
		broadcast(message, Server.BROADCAST_CHANNEL_USERS);
	}

	public static void broadcastMessage(BaseComponent[] message){
		broadcast(message,Server.BROADCAST_CHANNEL_USERS);
	}
	/**
	 *
	 * @param message the message
	 * @param permission the perm
	 * @deprecated use {@link Monolith#broadcast(BaseComponent[], String)}
	 */
	@Deprecated
	public static void broadcast(ChatMessage message, String permission)
	{
		for(Permissible perm : Bukkit.getPluginManager().getPermissionSubscriptions(permission))
		{
			if(perm instanceof CommandSender && perm.hasPermission(permission))
				message.send((CommandSender)perm);
		}
	}

	/**
	 *
	 * @param message the message
	 * @param permission the permission
	 */
	public static void broadcast(BaseComponent[] message, String permission){
		for(Permissible perm : Bukkit.getPluginManager().getPermissionSubscriptions(permission))
		{
			if(perm instanceof CommandSender && perm.hasPermission(permission))
				((CommandSender) perm).spigot().sendMessage(message);
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

    /**
     * Use to retrieve the now defunct servername from server.properties.  However you should just use
     * this method to write to a local config -
     * @return String Servername
     */
	@Deprecated
	public static String getServerName(){
		CraftServer server =  ((CraftServer) Bukkit.getServer());
		String result;
		try {
			Field field = server.getClass().getDeclaredField("console");
			field.setAccessible(true);
			DedicatedServer dServer = (DedicatedServer) field.get(server);
			DedicatedServerProperties props = dServer.getDedicatedServerProperties();
			Method getString = props.getClass().getMethod("getString", String.class,String.class);
			result = (String) getString.invoke(props,"server-name","");
		} catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			return "";
		}
		return result;
	}

	public GeSuitHandler getGeSuitHandler()
	{
		return mGeSuitHandler;
	}

	public void DebugMsg (String msg) {
		if (DebugMode) {
			Bukkit.getLogger().info("[Monolith] " + msg);
		}
	}
}
