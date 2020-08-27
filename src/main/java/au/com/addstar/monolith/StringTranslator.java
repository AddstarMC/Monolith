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

import au.com.addstar.monolith.util.Crafty;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StringTranslator {
    private final static Class ichatBaseComponentClass = Crafty.findNmsClass("IChatBaseComponent");

    /**
     * Grabs the internal Minecraft name of an item or empty string.
     *
     * @param item ItemStack
     * @return String
     */
    public static String getName(ItemStack item) {
        //net.minecraft.server.v1_16_R2.ItemStack base = CraftItemStack.asNMSCopy(item);
        //if (base != null && base.getItem() != null)
        //    return base.getName().getText();
        try {
            Class nmsItemStackClass = Crafty.findNmsClass("ItemStack");
            Class cbCraftItemStack = Crafty.findCraftClass("CraftItemStack");
            Method asNMSCopy = cbCraftItemStack.getMethod("asNMSCopy", ItemStack.class);
            Method getName = nmsItemStackClass.getMethod("getName");
            Method getText = ichatBaseComponentClass.getMethod("getText");
            Object nmsItemStack = asNMSCopy.invoke(null, item);
            Object ichatBaseComponent = getName.invoke(nmsItemStack);
            Object name = getText.invoke(ichatBaseComponent);
            return String.valueOf(name);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Monolith.getInstance().getLogger().warning(e.getMessage());
            return "";
        }
    }

    /**
     * Grabs the internal Minecraft name of an item or empty string.
     *
     * @param entity Entity
     * @return String
     */
    public static String getName(Entity entity) {
        try {
            Class craftEntity = Crafty.findCraftClass("CraftEntity");
            Method getHandle = craftEntity.getMethod("getHandle");
            Object nmsEntity = getHandle.invoke(entity);
            Class nmsEntityClass = Crafty.findNmsClass("Entity");
            Method getName = nmsEntityClass.getMethod("getName");
            Object name = getName.invoke(nmsEntity);
            return String.valueOf(name);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Monolith.getInstance().getLogger().warning(e.getMessage());
            return "";
        }
    }

    /**
     * Returns the minecraft name of a block as per the internal registry.
     *
     * @param block the block to test
     * @return the minecraft name of the block
     */
    public static String getName(Block block) {
        //BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
        //IBlockData type = ((CraftWorld) block.getWorld()).getHandle().getType(pos);
        //return type.getBlock().r().getKey();
        try {
            Class blockPositionClass = Crafty.findNmsClass("BlockPosition");
            Constructor cons = blockPositionClass.getConstructor(int.class, int.class, int.class);
            Object blockPostion = cons.newInstance(block.getX(), block.getY(), block.getZ());
            Class nmsWorldClass = Crafty.findNmsClass("WorldServer");
            Method getType = nmsWorldClass.getMethod("getType", blockPositionClass);
            Object iblockData = getType.invoke(blockPostion);
            Class IBlockDataClass = Crafty.findNmsClass("IBlockData");
            Method getBlock = IBlockDataClass.getMethod("getBlock");
            Object nmsBlock = getBlock.invoke(iblockData);
            Class nmsBlockClass = Crafty.findNmsClass("Block");
            Method getMKey = nmsBlockClass.getMethod("r");
            Object nmsMineCraftKey = getMKey.invoke(nmsBlock);
            Class nmsMineCraftKeyClass = Crafty.findNmsClass("MinecraftKey");
            Method getKey = nmsMineCraftKeyClass.getMethod("getKey");
            Object result = getKey.invoke(nmsMineCraftKey);
            return String.valueOf(result);
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Monolith.getInstance().getLogger().warning(e.getMessage());
            return "";
        }
    }
}
