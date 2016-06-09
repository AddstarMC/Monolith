package au.com.addstar.monolith;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.IBlockData;
import net.minecraft.server.v1_10_R1.LocaleI18n;

public class StringTranslator
{
	@Deprecated
	public static String translate(String key)
	{
		return LocaleI18n.get(key);
	}

	@Deprecated
	public static String translate(String key, Object... values)
	{
		return LocaleI18n.a(key, values);
	}
	
	public static String getName(ItemStack item)
	{
		net.minecraft.server.v1_10_R1.ItemStack base = CraftItemStack.asNMSCopy(item);
		if(base != null && base.getItem() != null)
			return base.getName();
		return "Unknown";
	}
	
	public static String getName(Entity entity)
	{
		net.minecraft.server.v1_10_R1.Entity base = ((CraftEntity) entity).getHandle();
		return base.getName();
	}
	
	public static String getName(Block block)
	{
		BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
		IBlockData type = ((CraftWorld)block.getWorld()).getHandle().getType(pos);
		return type.getBlock().getName();
	}
}
