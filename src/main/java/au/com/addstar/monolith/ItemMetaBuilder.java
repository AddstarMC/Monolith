/*
 * Copyright (c) 2020. AddstarMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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

import au.com.addstar.monolith.lookup.Lookup;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;

/**
 * A Class that creates custom  ItemMeta for an item.
 */
@SuppressWarnings("unused")
public class ItemMetaBuilder {
    /**
     * The Item meta.
     */
    private final ItemMeta meta;
    /**
     * The item.
     */
    private ItemStack item;
    /**
     * True if it has NBT.
     */
    private boolean hasNbt;
    /**
     * A firework builder.
     */
    private FireworkEffect.Builder metaFireWorkBuilder;
    /**
     * potion effect .
     */
    private PotionEffectType metaPotionEffect = null;
    /**
     * Potion power.
     */
    private int metaPotionPower = -1;
    /**
     * Potion duration.
     */
    private int metaPotionDuration = -1;

    /**
     * Create the Meta builder.
     *
     * @param item the item to build meta for.
     */
    public ItemMetaBuilder(ItemStack item) {
        this.item = item;
        meta = item.getItemMeta();
    }

    /**
     * Parse a string to create and add data.
     *
     * @param definition a String
     * @throws IllegalArgumentException if the data cannot be parsed
     */
    public void accept(String definition) throws IllegalArgumentException {
        String[] parts = definition.split(":", 2);

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid definition '" + definition
                    + "'. Must be in the format of <name>:<value>");
        }

        String name = parts[0];
        String content = parts[1];

        accept(name, content);
    }

    /**
     * Decodes the value by each method until it finds one thats true.
     *
     * @param name  The meta name
     * @param value the meta value
     * @throws IllegalArgumentException if it cannot be decoded
     */
    private void accept(final String name, final String value) throws IllegalArgumentException {
        if (decodeDefault(name, value)) {
            return;
        }
        if (decodeBook(name, value)) {
            return;
        }
        if (decodeFirework(name, value)) {
            return;
        }
        if (decodeLeatherArmor(name, value)) {
            return;
        }
        if (decodeMap(name, value)) {
            return;
        }
        if (decodePotion(name, value)) {
            return;
        }
        if (decodeSkull(name, value)) {
            return;
        }
        if (decodeStoredEnchants(name, value)) {
            return;
        }
        if (decodeEnchants(name, value)) {
            return;
        }
        if (decodeNbtString(name, value)) {
            return;
        }
        throw new IllegalArgumentException("Unknown meta id: " + name);
    }

    /**
     * Gets the Meta.
     *
     * @return the ItemMeta
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public ItemMeta build() {
        if (meta instanceof FireworkMeta) {
            completeFirework(null);
        }
        return meta;
    }

    /**
     * Gets the built stack with the new meta.
     *
     * @return the ItemStack
     */
    public ItemStack getItemStack() {
        if (!hasNbt) {
            build();
            item.setItemMeta(meta);
        }
        return item;

    }

    private int getInt(String string, int def) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private int getPosInt(String string, int def) throws IllegalArgumentException {
        Validate.isTrue(def >= 0);
        int val = getInt(string, def);
        if (val < 0) {
            val = def;
        }
        return val;
    }

    private boolean decodeDefault(String name, String content) {
        if (name.equalsIgnoreCase("name")) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', content));
            return true;
        } else if (name.equalsIgnoreCase("lore")) {
            List<String> lore = new ArrayList<>();
            for (String line : content.split("\\|")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }

            meta.setLore(lore);
            return true;
        }

        return false;
    }

    private boolean decodeEnchants(String name, String content) {
        String ent = name.replace(' ', '_');
        Enchantment enchant = Lookup.findEnchantmentByName(ent);
        if (enchant == null) {
            return false;
        }

        int level;

        level = getLevel(name, content);

        meta.addEnchant(enchant, level, true);
        return true;
    }

    private int getLevel(String name, String content) {
        int level;
        try {
            level = Integer.parseInt(content);
            if (level <= 0 || level > 127) {
                throw new IllegalArgumentException("Invalid level for enchantment " + name
                        + ". Level must be between 1 and 127");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid level for enchantment " + name
                    + ". Expected number between 1 and 127");
        }
        return level;
    }

    private boolean decodeBook(String name, String content) {
        if (!(meta instanceof BookMeta)) {
            return false;
        }
        if (name.equalsIgnoreCase("author")) {
            ((BookMeta) meta).setAuthor(
                    ChatColor.translateAlternateColorCodes('&', content));
            return true;
        } else if (name.equalsIgnoreCase("title")) {
            ((BookMeta) meta).setTitle(
                    ChatColor.translateAlternateColorCodes('&', content));
            return true;
        } else if (name.equalsIgnoreCase("book")) {
            ((BookMeta) meta).setPages(content.split("\\|"));
            return true;
        }

        return false;
    }

    private boolean decodeStoredEnchants(String name, String content) {
        if (!(meta instanceof EnchantmentStorageMeta)) {
            return false;
        }
        Enchantment enchant = Lookup.findEnchantmentByName(name);
        if (enchant == null) {
            return false;
        }
        int level = getLevel(name, content);

        ((EnchantmentStorageMeta) meta).addStoredEnchant(enchant, level, true);
        return true;
    }

    private FireworkEffect.Builder completeFirework(FireworkEffect.Builder current) {
        if (metaFireWorkBuilder != null) {
            ((FireworkMeta) meta).addEffect(metaFireWorkBuilder.build());
            metaFireWorkBuilder = null;
            return FireworkEffect.builder();
        }

        return current;
    }

    private boolean decodeFirework(String name, String content) {
        if (!(meta instanceof FireworkMeta)) {
            return false;
        }
        FireworkEffect.Builder builder = metaFireWorkBuilder;
        if (builder == null) {
            builder = FireworkEffect.builder();
        }
        if (name.equalsIgnoreCase("color") || name.equalsIgnoreCase("colour")) {
            builder = completeFirework(builder);

            String[] colours = content.split(",");
            for (String colour : colours) {
                if (colour.startsWith("#")) { //hex color
                    if (colour.length() != 4 && colour.length() != 7) {
                        throw new IllegalArgumentException("Invalid value for " + name
                                + ". Hex colour value " + colour
                                + " must be 3 or 6 characters long");
                    }
                    try {
                        int val = Integer.parseInt(colour.substring(1), 16);
                        builder.withColor(Color.fromRGB(val));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid value for " + name
                                + ". Hex colour value " + colour
                                + " must be 3 or 6 characters long");
                    }
                } else {
                    boolean ok = false;
                    for (DyeColor dye : DyeColor.values()) {
                        if (colour.equalsIgnoreCase(dye.name())) {
                            builder.withColor(dye.getFireworkColor());
                            ok = true;
                            break;
                        }
                    }

                    if (!ok) {
                        throw new IllegalArgumentException("Invalid value for " + name
                                + ". Unknown named colour value " + colour);
                    }
                }
            }
        } else if (name.equalsIgnoreCase("fade")) {
            String[] colours = content.split(",");
            for (String colour : colours) {
                if (colour.startsWith("#")) { //Hex Colour
                    if (colour.length() != 4 && colour.length() != 7) {
                        throw new IllegalArgumentException("Invalid value for " + name
                                + ". Hex colour value " + colour
                                + " must be 3 or 6 characters long");
                    }
                    try {
                        int val = Integer.parseInt(colour.substring(1), 16);
                        builder.withFade(Color.fromRGB(val));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid value for " + name
                                + ". Hex colour value " + colour
                                + " must be 3 or 6 characters long");
                    }
                } else {
                    boolean ok = false;
                    for (DyeColor dye : DyeColor.values()) {
                        if (colour.equalsIgnoreCase(dye.name())) {
                            builder.withFade(dye.getFireworkColor());
                            ok = true;
                            break;
                        }
                    }

                    if (!ok) {
                        throw new IllegalArgumentException("Invalid value for " + name
                                + ". Unknown named colour value " + colour);
                    }
                }
            }
        } else if (name.equalsIgnoreCase("shape") || name.equalsIgnoreCase("type")) {
            boolean ok = false;
            for (FireworkEffect.Type type : FireworkEffect.Type.values()) {
                if (content.equalsIgnoreCase(type.name())) {
                    builder.with(type);
                    ok = true;
                    break;
                }
            }

            if (content.equalsIgnoreCase("large")) {
                builder.with(FireworkEffect.Type.BALL_LARGE);
                ok = true;
            }

            if (!ok) {
                throw new IllegalArgumentException("Invalid value for " + name
                        + ". Unknown firework type value " + content);
            }
        } else if (name.equalsIgnoreCase("effect")) {
            for (String effect : content.split(",")) {
                if (effect.equalsIgnoreCase("twinkle")
                        || effect.equalsIgnoreCase("flicker")) {
                    builder.flicker(true);
                } else if (effect.equalsIgnoreCase("trail")) {
                    builder.trail(true);
                } else {
                    throw new IllegalArgumentException("Invalid value for " + name
                            + ". Unknown firework effect " + effect);
                }
            }
        } else {
            return false;
        }

        metaFireWorkBuilder = builder;
        return true;
    }

    private boolean decodeLeatherArmor(String name, String content) {
        if (!(meta instanceof LeatherArmorMeta)) {
            return false;
        }
        if (name.equalsIgnoreCase("color") || name.equalsIgnoreCase("colour")) {
            String[] parts = content.split("([|,])");

            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid value for " + name
                        + ". Should be <red>,<green>,<blue>");
            }
            try {
                ((LeatherArmorMeta) meta).setColor(Color.fromRGB(getPosInt(parts[0], 0),
                        getPosInt(parts[1], 0), getPosInt(parts[2], 0)));
            } catch (IllegalArgumentException e) {
                IllegalArgumentException newE =
                        new IllegalArgumentException("You cannot use negative integers for colors");
                newE.addSuppressed(e);
                throw newE;
            }
            return true;
        }

        return false;
    }

    private boolean decodeMap(String name, String content) {
        if (!(meta instanceof MapMeta)) {
            return false;
        }
        if (name.equalsIgnoreCase("scaling")) {
            ((MapMeta) meta).setScaling(Boolean.parseBoolean(content));
            return true;
        }

        return false;
    }

    private boolean decodePotion(String name, String content) {
        if (!(meta instanceof PotionMeta)) {
            return false;
        }
        if (name.equalsIgnoreCase("effect")) {
            metaPotionEffect = Lookup.findPotionEffectByName(content);
            if (metaPotionEffect == null) {
                throw new IllegalArgumentException("Invalid value for '" + name
                        + "'. Should be a potion effect");

            }
            return true;
        } else if (name.equalsIgnoreCase("power")) {
            try {
                metaPotionPower = Integer.parseInt(content);
                if (metaPotionPower <= 0) {
                    throw new IllegalArgumentException("Invalid value for '" + name
                            + "'. Expected integer greater than 0 for Potion Power");
                }
                metaPotionPower--;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid value for '" + name
                        + "'. Expected integer greater than 0 for Potion Power");
            }
        } else if (name.equalsIgnoreCase("duration")) {
            boolean ticks = false;
            if (content.endsWith("t")) {
                ticks = true;
                content = content.substring(0, content.length() - 1);
            } else if (content.endsWith("ticks")) {
                ticks = true;
                content = content.substring(0, content.length() - 5);
            }

            try {
                metaPotionDuration = Integer.parseInt(content);
                if (metaPotionDuration <= 0) {
                    throw new IllegalArgumentException("Invalid value for '" + name
                            + "'. Expected integer greater than 0 for Potion Duration");
                }
                if (!ticks) {
                    metaPotionDuration *= 20;
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid value for '" + name
                        + "'. Expected integer greater than 0 for Potion Power");
            }
        } else {
            return false;
        }
        if (metaPotionEffect != null && metaPotionDuration > 0 && metaPotionPower >= 0) {
            PotionMeta meta = (PotionMeta) this.meta;
            meta.addCustomEffect(metaPotionEffect.createEffect(metaPotionDuration,
                    metaPotionPower), true);
            metaPotionEffect = null;
            metaPotionDuration = metaPotionPower = -1;
        }

        return true;
    }

    private boolean decodeSkull(String name, String content) {
        if (!(meta instanceof SkullMeta)) {
            return false;
        }

        if (name.equalsIgnoreCase("player")
                || name.equalsIgnoreCase("owner")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(content);
            if (player.getName() == null) {
                throw new IllegalArgumentException("Invalid value for '" + name + "'. Player "
                        + "named:" + content + " not found");
            }
            ((SkullMeta) meta).setOwningPlayer(player);
            return true;
        }

        return false;
    }

    private boolean decodeNbtString(String name, String content) {
        if (name.equalsIgnoreCase("nbt")) {
            String[] parts = content.split(":", 2);
            if (parts.length == 2) {
                String key = parts[0];
                String value = parts[1];
                try {
                    NBTContainer container = new NBTContainer(value);
                    NBTItem nbtItem = new NBTItem(item);
                    NBTCompound compound = nbtItem.getCompound(key);
                    if (compound == null) {
                        compound = nbtItem.addCompound(key);
                        compound.mergeCompound(container);
                    }
                    compound.mergeCompound(container);
                    item = nbtItem.getItem();
                } catch (Exception e) {
                    e.printStackTrace();
                    hasNbt = false;
                    return false;
                }
            }
            hasNbt = true;
            return true;
        }
        return false;
    }

}
