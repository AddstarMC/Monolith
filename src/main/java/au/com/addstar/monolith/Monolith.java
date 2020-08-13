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
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_16_R2.DedicatedServer;
import net.minecraft.server.v1_16_R2.DedicatedServerProperties;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Monolith extends JavaPlugin {
    private static Monolith mInstance;
    public Boolean DebugMode = false;
    private GeSuitHandler mGeSuitHandler;

    public static Monolith getInstance() {
        return mInstance;

    }

    protected Monolith(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    /**
     * @param message    the message
     * @param permission the permission
     */
    @SuppressWarnings("WeakerAccess")
    public static void broadcast(final BaseComponent[] message, final String permission) {
        for (Permissible perm : Bukkit.getPluginManager().getPermissionSubscriptions(permission)) {
            if (perm instanceof CommandSender && perm.hasPermission(permission)) {
                ((CommandSender) perm).spigot().sendMessage(message);            }
        }
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
        CraftServer server = ((CraftServer) Bukkit.getServer());
        String result;
        try {
            Field field = server.getClass().getDeclaredField("console");
            field.setAccessible(true);
            DedicatedServer dServer = (DedicatedServer) field.get(server);
            DedicatedServerProperties props = dServer.getDedicatedServerProperties();
            Method getString = props.getClass().getMethod("getString", String.class, String.class);
            result = (String) getString.invoke(props, "server-name", "");
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return "";
        }
        return result;
    }

    @Override
    public void onEnable() {
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
