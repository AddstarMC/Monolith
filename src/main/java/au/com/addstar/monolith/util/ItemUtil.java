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

package au.com.addstar.monolith.util;

import au.com.addstar.monolith.Monolith;
import org.bukkit.inventory.ItemStack;

import java.lang.invoke.MethodHandle;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 30/08/2020.
 */
public class ItemUtil {
    /**
     * Convert {@link ItemStack} to string or null}
     * @param item Itemstack
     * @return
     */
    public static String getItemNbtString(ItemStack item) {
        try {
            Class<? extends ItemStack> craftItemStack = Crafty.findCraftClass("inventory.CraftItemStack", ItemStack.class);
            Class<?> nmsItemClass = Crafty.findNmsClass("ItemStack");
            Class<?> nbtTagCompoundClass = Crafty.findNmsClass("NBTTagCompound");
            MethodHandle asNMSCopy = Crafty.findStaticMethod(craftItemStack, "asNMSCopy", nmsItemClass,ItemStack.class);
            MethodHandle getTag = Crafty.findMethod(nmsItemClass, "getTag", nbtTagCompoundClass);
            Object nmsStack = asNMSCopy.invokeWithArguments(item);
            return getTag.invokeWithArguments(nmsStack).toString();
        } catch (Throwable t) {
            Monolith.getInstance().getLogger().warning("[MONOLITH] " + t.getMessage());
            return null;
        }
    }
}