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

import au.com.addstar.monolith.internal.GeSuitHandler;
import au.com.addstar.monolith.lookup.Lookup;
import au.com.addstar.monolith.util.Crafty;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeCordComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Monolith extends JavaPlugin {
    private static Monolith mInstance;
    public Boolean DebugMode = false;
    private GeSuitHandler mGeSuitHandler;

    public final AudienceProvider getAudienceProvider() {
        return audienceProvider;
    }

    private static AudienceProvider audienceProvider;

    public static Monolith getInstance() {
        return mInstance;

    }
    public Monolith(){
        //not required.
    }

    protected Monolith(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    /**
     * Send a message with permission.
     * @param message    the message
     * @param permission the permission
     */
    public static void broadcast(Component message, final String permission) {
        audienceProvider.permission(permission).sendMessage(message);
    }

    /**
     * Send a message with permission.
     * @param message    the message
     * @param permission the permission
     * @deprecated  use {@link Monolith#broadcast(Component, String)}
     */
    @Deprecated
    public static void broadcast(final BaseComponent[] message, final String permission) {
        audienceProvider.permission(permission).sendMessage(BungeeCordComponentSerializer.get().deserialize(message));
    }

    /**
     * Matches strings in a Collection and returns those matches.
     *
     * @param prefix the prefix to match
     * @param values The values to check
     * @return a List
     */

    public static List<String> matchStrings(final String prefix, final Collection<String> values) {
        ArrayList<String> matches = new ArrayList<>();
        String checkValue = prefix.toLowerCase();
        for (String value : values) {
            if (value.toLowerCase().startsWith(checkValue)) {
                matches.add(value);
            }
        }
        return matches;
    }

    /**
     * Use to retrieve the now defunct servername from server.properties.  However you should just
     * use this method to write to a local config -
     *
     * @return String Servername
     */
    @Deprecated
    public static String getServerName() {
        String result;
        try {
            Class craftServerClass = Crafty.findCraftClass("CraftServer");
            Field field = Crafty.needField(craftServerClass,"console");
            field.setAccessible(true);
            Class dedicatedServer = Crafty.findNmsClass("DedicatedServer");
            Class dedicatedServerProperties = Crafty.findNmsClass("DedicatedServerProperties");
            Object dServer = field.get(Bukkit.getServer());
            MethodHandle dedicatedPropsHandle = Crafty.findMethod(dedicatedServer,"getDedicatedServerProperties",dedicatedServerProperties);
            Object props = dedicatedPropsHandle.invokeWithArguments(dServer);
            MethodHandle getProperty = Crafty.findMethod(dedicatedServerProperties,"getString",String.class,String.class,String.class);
            result = String.valueOf(getProperty.invoke(props,"server-name", ""));
            //DedicatedServerProperties props = dServer.getDedicatedServerProperties();
            //Method getString = props.getClass().getMethod("getString", String.class, String.class);
            //result = (String) getString.invoke(props, "server-name", "");
        } catch (Throwable e) {
            e.printStackTrace();
            result = "";
        }
        return result;
    }

    @Override
    public void onEnable() {
        String version;
        version = Crafty.VERSION;
        getLogger().info("Your server is running version " + version);
        mInstance = this;
        Lookup.initialize(this);
        audienceProvider = BukkitAudiences.create(this);
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
        try {
            getCommand("monolith").setExecutor(new MonolithCommand(this));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        mGeSuitHandler = new GeSuitHandler(this);
        getLogger().info("enabled");

    }

    public GeSuitHandler getGeSuitHandler() {
        return mGeSuitHandler;
    }

    public void DebugMsg(String msg) {
        if (DebugMode) {
            Bukkit.getLogger().info("[Monolith] " + msg);
        }
    }
}
