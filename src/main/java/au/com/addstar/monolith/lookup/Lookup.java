package au.com.addstar.monolith.lookup;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import net.minecraft.server.v1_7_R4.Item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.addstar.monolith.Monolith;
import au.com.addstar.monolith.internal.lookup.ItemDB;

public class Lookup
{
	private static ItemDB mNameDB;

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
	}
	
	@SuppressWarnings( "deprecation" )
	/**
	 * Gets a material using the minecraft name.
	 * @param name The id of the object eg. minecraft:stone
	 * @return The material or null
	 */
	public static Material findByMinecraftName(String name)
	{
		Item item = (Item)Item.REGISTRY.get(name);
		if(item == null)
			return null;
		
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
}
