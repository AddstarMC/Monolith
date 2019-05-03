package au.com.addstar.monolith;

import org.bukkit.block.Block;

import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.IBlockData;

public class StringTranslator
{

	
	public static String getName(ItemStack item)
	{
		net.minecraft.server.v1_14_R1.ItemStack base = CraftItemStack.asNMSCopy(item);
		if(base != null && base.getItem() != null)
			return base.getName().getText();
		return "Unknown";
	}
	
	public static String getName(Entity entity)
	{
		net.minecraft.server.v1_14_R1.Entity base = ((CraftEntity) entity).getHandle();
		return base.getName();
	}

	/**
	 * REturns the minecraft name of a block as per the interal registry
	 * @param block the block to test
	 * @return the minecraft name of the block
	 */
	public static String getName(Block block)
	{
		BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
		IBlockData type = ((CraftWorld)block.getWorld()).getHandle().getType(pos);
		return type.getBlock().l();
	}
}
