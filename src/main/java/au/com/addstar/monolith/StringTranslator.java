package au.com.addstar.monolith;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_7_R3.LocaleI18n;

public class StringTranslator
{
	public static String translate(String key)
	{
		return LocaleI18n.get(key);
	}
	
	public static String translate(String key, Object... values)
	{
		return LocaleI18n.get(key, values);
	}
	
	public static String getName(ItemStack item)
	{
		net.minecraft.server.v1_7_R3.ItemStack base = CraftItemStack.asNMSCopy(item);
		return base.getName();
	}
	
	public static String getName(Entity entity)
	{
		net.minecraft.server.v1_7_R3.Entity base = ((CraftEntity)entity).getHandle();
		return base.getName();
	}
	
	public static String getName(Block block)
	{
		net.minecraft.server.v1_7_R3.Block base = ((CraftWorld)block.getWorld()).getHandle().getType(block.getX(), block.getY(), block.getZ());
		return base.getName();
	}
}
