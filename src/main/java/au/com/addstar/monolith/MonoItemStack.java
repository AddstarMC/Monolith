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

import au.com.addstar.monolith.attributes.ItemAttributes;
import au.com.addstar.monolith.attributes.MonoItemAttributes;
import au.com.addstar.monolith.properties.PropertyContainer;
import au.com.addstar.monolith.properties.PropertyContainerImpl;
import au.com.addstar.monolith.util.Crafty;
import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * This class is a type of ItemStack that provides extra
 * useful functionality.
 * <br>
 * <b>NOTE:</b> If the ItemStack held within is changed by external
 * code, the accuracy of values here can not be ensured.
 * //todo Consider reflection vs NMS
 */
public class MonoItemStack extends ItemStack {
    private static final String PropertiesNBTKey = "-mono-properties";
    private static final String AttributesNBTKey = "AttributeModifiers";

    private static final Field CraftStack_Handle;
    private static final Field NMSStack_Tag;

    private static final Field BukkitStack_UnhandledTags;
    private static Class<?> craftItemStackClass;

    static {
        // Work out how to get the NBT data from item stacks
        try {
            // For CraftItemStacks

            craftItemStackClass = Crafty.findCraftClass("inventory.CraftItemStack");
            CraftStack_Handle = craftItemStackClass.getDeclaredField("handle");
            NMSStack_Tag = CraftStack_Handle.getType().getDeclaredField("tag");

            CraftStack_Handle.setAccessible(true);
            NMSStack_Tag.setAccessible(true);

            // For plain old bukkit item stacks
            ItemStack temp = new ItemStack(Material.STONE);
            Class<?> metaClass = temp.getItemMeta().getClass();

            BukkitStack_UnhandledTags = metaClass.getDeclaredField("unhandledTags");
            BukkitStack_UnhandledTags.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("This version of Monolith is not compatible with this version of minecraft", e);
        }
    }

    private final ItemStack item;
    private PropertyContainer properties;
    private MonoItemAttributes attributes;

    /**
     * Creates a new MonoItemStack.
     * This ItemStack is a wrapper on the provided ItemStack.
     * Changes to this ItemStack will affect the provided ItemStack.
     *
     * @param item The item to base this on
     * @throws IllegalArgumentException Thrown if the material is AIR
     */
    public MonoItemStack(ItemStack item) {
        if (item.getType() == Material.AIR) {
            throw new IllegalArgumentException("AIR cannot have properties");
        }

        this.item = item;
    }

    /**
     * Gets the properties stored in this item
     *
     * @return The properties
     */
    public PropertyContainer getProperties() {
        if (getType() == Material.AIR) {
            throw new UnsupportedOperationException("AIR cannot have properties");
        }

        // Create the properties if needed
        if (properties == null) {
            NBTTagList list = getNBTList(PropertiesNBTKey);
            properties = new PropertyContainerImpl(list);
        }

        return properties;
    }

    /**
     * Gets the item attributes for this item
     *
     * @return The ItemAttributes
     */
    public ItemAttributes getAttributes() {
        if (getType() == Material.AIR) {
            throw new UnsupportedOperationException("AIR cannot have attributes");
        }

        if (attributes == null) {
            NBTTagList list = getNBTList(AttributesNBTKey);
            attributes = new MonoItemAttributes(list);
        }

        return attributes;
    }

    private NBTTagList getNBTList(String key) {
        try {
            NBTTagList list;

            // CraftItemStack just uses the thing directly
            if (craftItemStackClass.isInstance(item)) {
                Object handle = CraftStack_Handle.get(item);
                Object rawTag = NMSStack_Tag.get(handle);

                NBTTagCompound tag = (NBTTagCompound) rawTag;

                if (tag == null) {
                    tag = new NBTTagCompound();
                    NMSStack_Tag.set(handle, tag);
                }

                if (tag.hasKeyOfType(key, 9)) {
                    list = tag.getList(key, 10);
                } else {
                    list = new NBTTagList();
                    tag.set(key, list);
                }
            }
            // Bukkit item stack needs to use the item meta
            else {
                ItemMeta meta = item.getItemMeta();
                @SuppressWarnings("unchecked")
                Map<String, NBTBase> tags = (Map<String, NBTBase>) BukkitStack_UnhandledTags.get(meta);

                if (tags.containsKey(key)) {
                    NBTBase rawTag = tags.get(key);
                    if (rawTag instanceof NBTTagList) {
                        list = (NBTTagList) rawTag;
                    } else {
                        list = new NBTTagList();
                        tags.put(key, list);
                    }
                } else {
                    list = new NBTTagList();
                    tags.put(key, list);
                }

                item.setItemMeta(meta);
            }

            return list;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("This version of Monolith is not compatible with this version of minecraft", e);
        }
    }


    @Override
    public ItemStack clone() {
        MonoItemStack clone = new MonoItemStack(item.clone());

        if (properties != null) {
            clone.properties = properties.clone();
        }

        return clone;
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
     * Wrapper methods
     */

    @Override
    public Material getType() {
        return item.getType();
    }

    @Override
    public void setType(Material type) {
        item.setType(type);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public int getTypeId() {
        return item.getType().getId();
    }

    @Override
    public int getAmount() {
        return item.getAmount();
    }

    @Override
    public void setAmount(int amount) {
        item.setAmount(amount);
    }

    @Override
    public short getDurability() {
        return item.getDurability();
    }

    @Override
    public void setDurability(short durability) {
        item.setDurability(durability);
    }

    @Override
    public ItemMeta getItemMeta() {
        return item.getItemMeta();
    }

    @Override
    public boolean setItemMeta(ItemMeta itemMeta) {
        if (item.setItemMeta(itemMeta)) {
            properties = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasItemMeta() {
        return item.hasItemMeta();
    }

    @Override
    public int getMaxStackSize() {
        return item.getMaxStackSize();
    }

    @Override
    public void addEnchantment(Enchantment ench, int level) {
        item.addEnchantment(ench, level);
    }

    @Override
    public void addEnchantments(Map<Enchantment, Integer> enchantments) {
        item.addEnchantments(enchantments);
    }

    @Override
    public void addUnsafeEnchantment(Enchantment ench, int level) {
        item.addUnsafeEnchantment(ench, level);
    }

    @Override
    public void addUnsafeEnchantments(Map<Enchantment, Integer> enchantments) {
        item.addUnsafeEnchantments(enchantments);
    }

    @Override
    public boolean containsEnchantment(Enchantment ench) {
        return item.containsEnchantment(ench);
    }

    @Override
    public int getEnchantmentLevel(Enchantment ench) {
        return item.getEnchantmentLevel(ench);
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return item.getEnchantments();
    }

    @Override
    public int removeEnchantment(Enchantment ench) {
        return item.removeEnchantment(ench);
    }

    @Override
    public boolean isSimilar(ItemStack stack) {
        return item.isSimilar(stack);
    }

}
