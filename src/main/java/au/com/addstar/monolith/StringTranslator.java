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

import org.bukkit.block.Block;

import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.IBlockData;

public class StringTranslator {


    public static String getName(ItemStack item) {
        net.minecraft.server.v1_16_R2.ItemStack base = CraftItemStack.asNMSCopy(item);
        if (base != null && base.getItem() != null)
            return base.getName().getText();
        return "Unknown";
    }

    public static String getName(Entity entity) {
        net.minecraft.server.v1_16_R2.Entity base = ((CraftEntity) entity).getHandle();
        return base.getName();
    }

    /**
     * REturns the minecraft name of a block as per the internal registry
     *
     * @param block the block to test
     * @return the minecraft name of the block
     */
    public static String getName(Block block) {
        BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
        IBlockData type = ((CraftWorld) block.getWorld()).getHandle().getType(pos);
        return type.getBlock().r().getKey();
    }
}
