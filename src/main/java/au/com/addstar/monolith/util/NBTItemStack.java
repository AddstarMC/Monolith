package au.com.addstar.monolith.util;

/**
 * Borrowed from https://github.com/tr7zw/Item-NBT-API for the AddstarMC Project.
 * Updated by Narimm on 21/04/2016.
 */

import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTItemStack extends ItemStack{

    private ItemStack bukkitItem;
    private NBTTagCompound nbtTag;

    public NBTItemStack(ItemStack item) {
        bukkitItem = item.clone();
        this.nbtTag = CraftItemStack.asNMSCopy(item).getTag();

    }

    public ItemStack getItem() {
        return bukkitItem;
    }

    public void setString(String key, String value) {
        bukkitItem = NBTReflectionUtil.setString(bukkitItem, key, value);
    }

    public String getString(String key) {
        return nbtTag.getString(key);
    }

    public void setInteger(String key, int value) {
        bukkitItem = NBTReflectionUtil.setInt(bukkitItem, key, value);
    }

    public Integer getInteger(String key) {
        return nbtTag.getInt(key);
    }

    public void setDouble(String key, double value) {
        bukkitItem = NBTReflectionUtil.setDouble(bukkitItem, key, value);
    }

    public double getDouble(String key) {
        return nbtTag.getDouble(key);
    }

    public void setBoolean(String key, boolean value) {
        bukkitItem = NBTReflectionUtil.setBoolean(bukkitItem, key, value);
    }

    public boolean getBoolean(String key) {
        return nbtTag.getBoolean(key);
    }

    public boolean hasKey(String key) {
        return nbtTag.hasKey(key);
    }

    public String listNBT(){
        if (hasNBTData()) {
            return nbtTag.toString();
        } else {
            return "Empty";
        }
     }

    public boolean hasNBTData() {
        return nbtTag != null;
    }


}
