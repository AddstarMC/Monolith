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

package au.com.addstar.monolith.lookup;

import au.com.addstar.monolith.Monolith;
import au.com.addstar.monolith.internal.FutureWaiter;
import au.com.addstar.monolith.internal.lookup.EnchantDB;
import au.com.addstar.monolith.internal.lookup.EntityDB;
import au.com.addstar.monolith.internal.lookup.ItemDB;
import au.com.addstar.monolith.internal.lookup.PotionsDB;
import au.com.addstar.monolith.util.Crafty;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class Lookup {
    private static ItemDB mNameDB;
    private static EnchantDB mEnchantDB;
    private static PotionsDB mPotionDB;
    private static EntityDB mEntityDB;

    private static Class minecraftkey = Crafty.findNmsClass("MinecraftKey");

    /**
     * Initializes lookup systems. This should not be called by users of this API
     *
     * @param plugin the plugin to initialize
     */
    public static void initialize(Monolith plugin) {
        mNameDB = new ItemDB();
        File nameFile = new File(plugin.getDataFolder(), "items.csv");

        try {
            if (!nameFile.exists())
                mNameDB.load(plugin.getResource("items.csv"));
            else
                mNameDB.load(nameFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Unable to load item name database:");
            e.printStackTrace();
        }

        mEnchantDB = new EnchantDB();
        nameFile = new File(plugin.getDataFolder(), "enchantments.csv");

        try {
            if (!nameFile.exists())
                mEnchantDB.load(plugin.getResource("enchantments.csv"));
            else
                mEnchantDB.load(nameFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Unable to load enchantment name database:");
            e.printStackTrace();
        }

        mPotionDB = new PotionsDB();
        nameFile = new File(plugin.getDataFolder(), "potions.csv");

        try {
            if (!nameFile.exists())
                mPotionDB.load(plugin.getResource("potions.csv"));
            else
                mPotionDB.load(nameFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Unable to load potion effect name database:");
            e.printStackTrace();
        }

        mEntityDB = new EntityDB();
        nameFile = new File(plugin.getDataFolder(), "entities.csv");

        try {
            if (!nameFile.exists())
                mEntityDB.load(plugin.getResource("entities.csv"));
            else
                mEntityDB.load(nameFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Unable to load entity database:");
            e.printStackTrace();
        }
    }

    /**
     * Gets a material using the minecraft name.
     *
     * @param name The id of the object eg. minecraft:stone
     * @return The material or null
     */
    public static Material findByMinecraftName(String name) {
        MethodHandle handled = Crafty.findConstructor(minecraftkey, String.class);
        if (handled != null) {
            try {
                MethodHandle keyHandles = MethodHandles.lookup().findVirtual(minecraftkey, "getKey", MethodType.methodType(String.class));
                Object key = handled.invoke(name);
                Object minecraftName = keyHandles.invoke(key);
                Monolith.getInstance().DebugMsg("findByMinecraftName(" + name + ") = " + minecraftName);
                return Material.matchMaterial((String) minecraftName);
            } catch (Throwable throwable) {
                Monolith.getInstance().getLogger().warning("Unable to perform lookup via NMS for " + name);
                if (Monolith.getInstance().DebugMode) {
                    throwable.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Finds a matching ItemStack that is known by the specified name.
     *
     * @param name The name to search for
     * @return An MaterialDefinition object, or null
     */
    public static Material findItemByName(String name) {
        return mNameDB.getByName(name);
    }

    /**
     * Finds the names registered against this item.
     * The returned names can all be used to lookup this item using {@link #findItemByName(String)}.
     * If the actual translated name is wanted, use StringTranslator#getName(ItemStack).
     *
     * @param material The material to look for
     * @return The names this item is registered against, or an empty set
     */
    public static Set<String> findNameByItem(Material material) {
        Set<String> names = mNameDB.getbyMaterial(material);
        if (names == null)
            return Collections.emptySet();
        else
            return names;
    }

    public static Set<String> getAllEntityNames() {
        return mEntityDB.getAllTypes();
    }

    /**
     * Finds the minecraft name of the specified material
     *
     * @param material The material to look for
     * @return The minecraft name, or null of none was found
     */
    public static String findMinecraftNameByItem(Material material) {
        return material.getKey().toString();
    }

    /**
     * Finds the {@link PotionEffectType} that is known by the specified name.
     *
     * @param name The name of the potion effect
     * @return The PotionEffectType, or null
     */
    public static PotionEffectType findPotionEffectByName(String name) {
        return mPotionDB.getByName(name);
    }

    /**
     * Finds the names registered against this potion effect.
     * The returned names can all be used to lookup this potion effect using {@link #findPotionEffectByName(String)}.
     *
     * @param type The potion effect to look for
     * @return The names this potion effect is registered against, or an empty set
     */
    public static Set<String> findNameByPotionEffect(PotionEffectType type) {
        return mPotionDB.getByEffect(type);
    }

    /**
     * Finds the {@link Enchantment} that is known by the specified name.
     *
     * @param name The name of the enchantment
     * @return The Enchantment, or null
     */
    public static Enchantment findEnchantmentByName(String name) {
        return mEnchantDB.getByName(name);
    }

    /**
     * Finds the names registered against this enchantment
     * The returned names can all be used to lookup this enchantment using {@link #findEnchantmentByName(String)}.
     *
     * @param enchant The enchantment to look for
     * @return The names this enchantment is registered against, or an empty set
     */
    public static Set<String> findNameByEnchantment(Enchantment enchant) {
        return mEnchantDB.getByEnchant(enchant);
    }

    /**
     * Does a bungee-aware lookup to resolve the specified name to a PlayerDefinition.
     *
     * @param name The name to look for
     * @return A ListenableFuture that can be used to get the PlayerDefinition once ready.
     * <b>WARNING</b>: Any listeners will be fired asynchronously to the main thread
     */
    public static ListenableFuture<PlayerDefinition> lookupPlayerName(String name) {
        return Futures.transform(lookupPlayerNames(Collections.singletonList(name)), list -> {
            if (list == null || list.isEmpty())
                return null;
            else
                return list.get(0);
        }, MoreExecutors.directExecutor());
    }

    /**
     * Does a bungee-aware lookup to resolve the specified name to a PlayerDefinition.
     * <b>NOTES</b>:
     * <ul>
     * <li>A time limit of 5 seconds has been placed on these lookups in case the proxy is unavailable. </li>
     * <li>A player is required to be on this server to perform this lookup</li>
     * </ul>
     *
     * @param name     The name to look for.
     * @param callback A callback that will be called on the server thread when the lookup is completed.
     */
    public static void lookupPlayerName(String name, LookupCallback<PlayerDefinition> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Monolith.getInstance(), new FutureWaiter<>(lookupPlayerName(name), callback, 5, TimeUnit.SECONDS));
    }

    /**
     * Does a bungee-aware lookup to resolve the specified uuid to a PlayerDefinition.
     * <b>NOTES</b>:
     * <ul>
     * <li>A time limit of 5 seconds has been placed on these lookups in case the proxy is unavailable. </li>
     * <li>A player is required to be on this server to perform this lookup</li>
     * </ul>
     *
     * @param id The uuid to look for
     * @return A ListenableFuture that can be used to get the PlayerDefinition once ready.
     * <b>WARNING</b>: Any listeners will be fired asynchronously to the main thread
     */
    public static ListenableFuture<PlayerDefinition> lookupPlayerUUID(UUID id) {
        return Futures.transform(lookupPlayerUUIDs(Collections.singletonList(id)), list -> {
            if (list == null || list.isEmpty())
                return null;
            else
                return list.get(0);
        }, MoreExecutors.directExecutor());
    }

    /**
     * Does a bungee-aware lookup to resolve the specified uuid to a PlayerDefinition.
     * <b>NOTES</b>:
     * <ul>
     * <li>A time limit of 5 seconds has been placed on these lookups in case the proxy is unavailable. </li>
     * <li>A player is required to be on this server to perform this lookup</li>
     * </ul>
     *
     * @param id       The uuid to resolve.
     * @param callback A callback that will be called on the server thread when the lookup is completed.
     */
    public static void lookupPlayerUUID(UUID id, LookupCallback<PlayerDefinition> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Monolith.getInstance(), new FutureWaiter<>(lookupPlayerUUID(id), callback, 5, TimeUnit.SECONDS));
    }

    /**
     * Does a bungee-aware lookup to resolve the specified names to a PlayerDefinitions.
     * <b>NOTES</b>:
     * <ul>
     * <li>A time limit of 5 seconds has been placed on these lookups in case the proxy is unavailable. </li>
     * <li>A player is required to be on this server to perform this lookup</li>
     * </ul>
     *
     * @param names An Iterable containing the names to resolve
     * @return A ListenableFuture that can be used to get a list of PlayerDefinitions once ready.
     * <b>WARNING</b>: Any listeners will be fired asynchronously to the main thread
     */
    public static ListenableFuture<List<PlayerDefinition>> lookupPlayerNames(Iterable<String> names) {
        return Monolith.getInstance().getGeSuitHandler().lookupPlayerNames(names);
    }

    /**
     * Does a bungee-aware lookup to resolve the specified names to PlayerDefinitions.
     * <b>NOTES</b>:
     * <ul>
     * <li>A time limit of 5 seconds has been placed on these lookups in case the proxy is unavailable. </li>
     * <li>A player is required to be on this server to perform this lookup</li>
     * </ul>
     *
     * @param names    An Iterable containing the names to resolve
     * @param callback A callback that will be called on the server thread when the lookup is completed.
     */
    public static void lookupPlayerNames(Iterable<String> names, LookupCallback<List<PlayerDefinition>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Monolith.getInstance(), new FutureWaiter<>(lookupPlayerNames(names), callback, 5, TimeUnit.SECONDS));
    }

    /**
     * Does a bungee-aware lookup to resolve the specified uuids to PlayerDefinitions.
     * <b>NOTES</b>:
     * <ul>
     * <li>A time limit of 5 seconds has been placed on these lookups in case the proxy is unavailable. </li>
     * <li>A player is required to be on this server to perform this lookup</li>
     * </ul>
     *
     * @param ids An Iterable containing the uuids to resolve
     * @return A ListenableFuture that can be used to get a list of PlayerDefinitions once ready.
     * <b>WARNING</b>: Any listeners will be fired asynchronously to the main thread
     */
    public static ListenableFuture<List<PlayerDefinition>> lookupPlayerUUIDs(Iterable<UUID> ids) {
        return Monolith.getInstance().getGeSuitHandler().lookupPlayerUUIDs(ids);
    }

    /**
     * Does a bungee-aware lookup to resolve the specified uuids to PlayerDefinitions.
     * <b>NOTES</b>:
     * <ul>
     * <li>A time limit of 5 seconds has been placed on these lookups in case the proxy is unavailable. </li>
     * <li>A player is required to be on this server to perform this lookup</li>
     * </ul>
     *
     * @param ids      An Iterable containing the uuids to resolve
     * @param callback A callback that will be called on the server thread when the lookup is completed.
     */
    public static void lookupPlayerUUIDs(Iterable<UUID> ids, LookupCallback<List<PlayerDefinition>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Monolith.getInstance(), new FutureWaiter<>(lookupPlayerUUIDs(ids), callback, 5, TimeUnit.SECONDS));
    }

    /**
     * Finds a matching EntityDefinition that is known by the specified name.
     *
     * @param name The name to search for
     * @return An EntityDefinition object, or null
     */
    public static EntityDefinition findEntityByName(String name) {
        return mEntityDB.getByName(name);
    }

    /**
     * Finds the names registered against this entity definition.
     * The returned names can all be used to lookup this item using {@link #findEntityByName(String)}.
     * If the actual translated name is wanted, use StringTranslator#getName(Entity).
     *
     * @param entity The entity to lookup
     * @return The names this entity is registered against, or an empty set
     */
    public static Set<String> findNameByEntity(Entity entity) {
        return findNameByEntity(new EntityDefinition(entity));
    }

    /**
     * Finds the names registered against this entity definition.
     * The returned names can all be used to lookup this item using {@link #findEntityByName(String)}.
     *
     * @param def The extended entity type to look for
     * @return The names this entity is registered against, or an empty set
     */
    public static Set<String> findNameByEntity(EntityDefinition def) {
        return mEntityDB.getByType(def);
    }

    /**
     * Finds the names registered against this entity definition.
     * The returned names can all be used to lookup this item using {@link #findEntityByName(String)}.
     *
     * @param type The entity type to look for
     * @return The names this entity is registered against, or an empty set
     */
    public static Set<String> findNameByEntity(EntityType type) {
        return findNameByEntity(new EntityDefinition(type, null));
    }
}
