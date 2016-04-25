package au.com.addstar.monolith;

import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import au.com.addstar.monolith.properties.PropertyContainer;
import au.com.addstar.monolith.properties.PropertyContainerImpl;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.NBTTagList;

/**
 * This class is a type of ItemStack that provides extra
 * useful functionality 
 */
public class MonoItemStack extends ItemStack implements Attributable
{
	private final CraftItemStack item;
	private PropertyContainer properties;
	
	/**
	 * Creates a new MonoItemStack.
	 * This item stack is backed by an actual low level item stack.
	 * If you are going to modify anything within this ItemStack, you MUST
	 * override the stored item stack (that you provided to this constructor)
	 * with this instance otherwise you might lose the modifications.
	 * <br>
	 * Example:
	 * <pre>
	 * ItemStack item = player.getInventory().getItemInMainHand();
	 * MonoItemStack monoItem = new MonoItemStack(item);
	 * player.getInventory().setItemInMainHand(monoItem);
	 * 
	 * // ... Now it is safe to modify monoItem 
	 * </pre>
	 * 
	 * @param item The item to base this on
	 */
	public MonoItemStack(ItemStack item)
	{
		if (item instanceof CraftItemStack)
			this.item = (CraftItemStack)item;
		else
			this.item = CraftItemStack.asCraftCopy(item);
	}
	
	private MonoItemStack(CraftItemStack craftItem)
	{
		item = craftItem;
	}
	
	/**
	 * Gets the properties stored in this item
	 * @return The properties
	 */
	public PropertyContainer getProperties()
	{
		// Create the properties if needed
		if (properties == null)
		{
			try
			{
				// Load the NBT Tag for the item
				Field handleField = CraftItemStack.class.getDeclaredField("handle");
				Field tagField = handleField.getType().getDeclaredField("tag");
				handleField.setAccessible(true);
				tagField.setAccessible(true);
				
				Object handle = handleField.get(item);
				Object rawTag = tagField.get(handle);
				
				NBTTagCompound tag = (NBTTagCompound)rawTag;
				
				if (tag == null)
				{
					tag = new NBTTagCompound();
					tagField.set(handle, tag);
				}
				
				// Read the properties
				if (tag.hasKeyOfType("-mono-properties", 9))
				{
					NBTTagList list = tag.getList("-mono-properties", 10);
					properties = new PropertyContainerImpl(list);
				}
				else
				{
					NBTTagList list = new NBTTagList();
					tag.set("-mono-properties", list);
					properties = new PropertyContainerImpl(list);
				}
			}
			catch (NoSuchFieldException e)
			{
				throw new IllegalStateException("This version of Monolith is not compatible with this version of minecraft", e);
			}
			catch (IllegalAccessException e)
			{
				throw new IllegalStateException("This version of Monolith is not compatible with this version of minecraft", e);
			}
		}
		
		return properties;
	}
	
	@Override
	public AttributeInstance getAttribute(Attribute attribute)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	
	@Override
	public ItemStack clone()
	{
		MonoItemStack clone = new MonoItemStack(item.clone());
		
		if (properties != null)
			clone.properties = properties.clone();
		
		return clone;
	}
	
	@Override
	public Map<String, Object> serialize()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/*
	 * Wrapper methods 
	 */
	
	@Override
	public Material getType()
	{
		return item.getType();
	}
	
	@Override
	@Deprecated
	public int getTypeId()
	{
		return item.getTypeId();
	}
	
	@Override
	public int getAmount()
	{
		return item.getAmount();
	}
	
	@Override
	public short getDurability()
	{
		return item.getDurability();
	}
	
	@Override
	public ItemMeta getItemMeta()
	{
		return item.getItemMeta();
	}
	
	@Override
	public boolean setItemMeta(ItemMeta itemMeta)
	{
		return item.setItemMeta(itemMeta);
	}
	
	@Override
	public boolean hasItemMeta()
	{
		return item.hasItemMeta();
	}
	
	@Override
	public int getMaxStackSize()
	{
		return item.getMaxStackSize();
	}
	
	@Override
	public void setAmount(int amount)
	{
		item.setAmount(amount);
	}
	
	@Override
	public void setDurability(short durability)
	{
		item.setDurability(durability);
	}
	
	@Override
	public void setType(Material type)
	{
		item.setType(type);
	}
	
	@Override
	@Deprecated
	public void setTypeId(int type)
	{
		item.setTypeId(type);
	}
	
	@Override
	public void addEnchantment(Enchantment ench, int level)
	{
		item.addEnchantment(ench, level);
	}
	
	@Override
	public void addEnchantments(Map<Enchantment, Integer> enchantments)
	{
		item.addEnchantments(enchantments);
	}
	
	@Override
	public void addUnsafeEnchantment(Enchantment ench, int level)
	{
		item.addUnsafeEnchantment(ench, level);
	}
	
	@Override
	public void addUnsafeEnchantments(Map<Enchantment, Integer> enchantments)
	{
		item.addUnsafeEnchantments(enchantments);
	}
	
	@Override
	public boolean containsEnchantment(Enchantment ench)
	{
		return item.containsEnchantment(ench);
	}
	
	@Override
	public int getEnchantmentLevel(Enchantment ench)
	{
		return item.getEnchantmentLevel(ench);
	}
	
	@Override
	public Map<Enchantment, Integer> getEnchantments()
	{
		return item.getEnchantments();
	}
	
	@Override
	public int removeEnchantment(Enchantment ench)
	{
		return item.removeEnchantment(ench);
	}
	
	@Override
	public boolean isSimilar(ItemStack stack)
	{
		return item.isSimilar(stack);
	}
	
	@Override
	public void setData(MaterialData data)
	{
		item.setData(data);
	}
}
