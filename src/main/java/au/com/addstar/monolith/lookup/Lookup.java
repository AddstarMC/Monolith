package au.com.addstar.monolith.lookup;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import net.minecraft.server.v1_8_R1.Block;
import net.minecraft.server.v1_8_R1.Item;
import net.minecraft.server.v1_8_R1.MinecraftKey;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import au.com.addstar.monolith.Monolith;
import au.com.addstar.monolith.internal.lookup.EnchantDB;
import au.com.addstar.monolith.internal.lookup.ItemDB;
import au.com.addstar.monolith.internal.lookup.PotionsDB;

public class Lookup
{
	private static ItemDB mNameDB;
	private static EnchantDB mEnchantDB;
	private static PotionsDB mPotionDB;

	/**
	 * Initializes lookup systems. This should not be called by users of this API
	 */
	public static void initialize(Monolith plugin)
	{
		mNameDB = new ItemDB();
		File nameFile = new File(plugin.getDataFolder(), "items.csv");
		
		try
		{
			if(!nameFile.exists())
				mNameDB.load(plugin.getResource("items.csv"));
			else
				mNameDB.load(nameFile);
		}
		catch(IOException e)
		{
			plugin.getLogger().severe("Unable to load item name database:");
			e.printStackTrace();
		}
		
		mEnchantDB = new EnchantDB();
		nameFile = new File(plugin.getDataFolder(), "enchantments.csv");
		
		try
		{
			if(!nameFile.exists())
				mEnchantDB.load(plugin.getResource("enchantments.csv"));
			else
				mEnchantDB.load(nameFile);
		}
		catch(IOException e)
		{
			plugin.getLogger().severe("Unable to load enchantment name database:");
			e.printStackTrace();
		}
		
		mPotionDB = new PotionsDB();
		nameFile = new File(plugin.getDataFolder(), "potions.csv");
		
		try
		{
			if(!nameFile.exists())
				mPotionDB.load(plugin.getResource("potions.csv"));
			else
				mPotionDB.load(nameFile);
		}
		catch(IOException e)
		{
			plugin.getLogger().severe("Unable to load potion effect name database:");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings( "deprecation" )
	/**
	 * Gets a material using the minecraft name.
	 * @param name The id of the object eg. minecraft:stone
	 * @return The material or null
	 */
	public static Material findByMinecraftName(String name)
	{
		MinecraftKey key = new MinecraftKey(name);
		Item item = (Item)Item.REGISTRY.get(key);
		if(item == null)
		{
			// Attempt blocks that dont have items
			Block block = (Block)Block.REGISTRY.get(key);
			if (block == null)
				return null;
			
			return Material.getMaterial(Block.getId(block));
		}
		
		return Material.getMaterial(Item.getId(item));
	}
	
	/**
	 * Finds a matching ItemStack that is known by the specified name.
	 * @param name The name to search for
	 * @return An MaterialDefinition object, or null
	 */
	public static MaterialDefinition findItemByName(String name)
	{
		return mNameDB.getByName(name);
	}
	
	/**
	 * Finds the names registered against this item.
	 * The returned names can all be used to lookup this item using {@link #findItemByName(String)}.
	 * If the actual translated name is wanted, use {@link StringTranslator#getName(ItemStack)}.
	 * @param material The material to look for
	 * @return The names this item is registered against, or an empty set
	 */
	public static Set<String> findNameByItem(MaterialDefinition material)
	{
		Set<String> names = mNameDB.getById(material);
		if(names == null)
			return Collections.emptySet();
		else
			return names;
	}
	
	/**
	 * Finds the minecraft name of the specified material
	 * @param material The material to look for
	 * @return The minecraft name, or null of none was found
	 */
	public static String findMinecraftNameByItem(Material material)
	{
		@SuppressWarnings( "deprecation" )
        Item item = Item.getById(material.getId());
		if (item == null)
			return null;
		
		MinecraftKey key = (MinecraftKey)Item.REGISTRY.c(item);
		return key.toString();
	}
	
	/**
	 * Finds the {@link PotionEffectType} that is known by the specified name.
	 * @param name The name of the potion effect
	 * @return The PotionEffectType, or null
	 */
	public static PotionEffectType findPotionEffectByName(String name)
	{
		return mPotionDB.getByName(name);
	}
	
	/**
	 * Finds the names registered against this potion effect.
	 * The returned names can all be used to lookup this potion effect using {@link #findPotionEffectByName(String)}.
	 * @param type The potion effect to look for
	 * @return The names this potion effect is registered against, or an empty set
	 */
	public static Set<String> findNameByPotionEffect(PotionEffectType type)
	{
		return mPotionDB.getByEffect(type);
	}
	
	/**
	 * Finds the {@link Enchantment} that is known by the specified name.
	 * @param name The name of the enchantment
	 * @return The Enchantment, or null
	 */
	public static Enchantment findEnchantmentByName(String name)
	{
		return mEnchantDB.getByName(name);
	}
	
	/**
	 * Finds the names registered against this enchantment
	 * The returned names can all be used to lookup this enchantment using {@link #findEnchantmentByName(String)}.
	 * @param type The enchantment to look for
	 * @return The names this enchantment is registered against, or an empty set
	 */
	public static Set<String> findNameByEnchantment(Enchantment enchant)
	{
		return mEnchantDB.getByEnchant(enchant);
	}
}
