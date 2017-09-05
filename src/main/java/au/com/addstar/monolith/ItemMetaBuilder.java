package au.com.addstar.monolith;

import java.util.ArrayList;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffectType;

import au.com.addstar.monolith.lookup.Lookup;

public class ItemMetaBuilder
{
	private ItemMeta mMeta;
	private ItemStack item;
	
	public ItemMetaBuilder(ItemStack item)
	{
		this.item = item;
		mMeta = item.getItemMeta();
	}
	
	public void accept(String definition) throws IllegalArgumentException
	{
		String[] parts = definition.split(":", 2);
		
		if(parts.length != 2)
			throw new IllegalArgumentException("Invalid definition '" + definition + "'. Must be in the format of <name>:<value>");
		
		String name = parts[0];
		String content = parts[1];
		
		accept(name, content);
	}
	
	public void accept(String name, String value) throws IllegalArgumentException
	{
		if(decodeDefault(name, value))
			return;
		
		if(decodeBook(name, value))
			return;
		
		if(decodeFirework(name, value))
			return;
		
		if(decodeLeatherArmor(name, value))
			return;
		
		if(decodeMap(name, value))
			return;
		
		if(decodePotion(name, value))
			return;
		
		if(decodeSkull(name, value))
			return;
		
		if(decodeStoredEnchants(name, value))
			return;
		
		if(decodeEnchants(name, value))
			return;
		if(decodeSpawnEgg(name,value))return;
		throw new IllegalArgumentException("Unknown meta id: " + name);
	}
	
	public ItemMeta build()
	{
		if(mMeta instanceof FireworkMeta)
			completeFirework(null);
		
		return mMeta;
	}
	
	private int getInt(String string, int def)
	{
		try
		{
			return Integer.parseInt(string);
		}
		catch(NumberFormatException e)
		{
			return def;
		}
	}
	
	private int getPosInt(String string, int def)
	{
		Validate.isTrue(def >= 0);
		int val = getInt(string, def);
		if(val < 0)
			val = def;
		
		return def;
	}
	
	private boolean decodeDefault(String name, String content)
	{
		if(name.equalsIgnoreCase("name"))
		{
			mMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', content));
			return true;
		}
		else if(name.equalsIgnoreCase("lore"))
		{
			ArrayList<String> lore = new ArrayList<String>();
			for(String line : content.split("\\|"))
				lore.add(ChatColor.translateAlternateColorCodes('&', line));
			
			mMeta.setLore(lore);
			return true;
		}
		
		return false;
	}
	
	private boolean decodeEnchants(String name, String content)
	{
		Enchantment enchant = Lookup.findEnchantmentByName(name);
		if(enchant == null)
			return false;
		
		int level;
		
		try
		{
			level = Integer.parseInt(content);
			if(level <= 0 || level > 127)
				throw new IllegalArgumentException("Invalid level for enchantment " + name + ". Level must be between 1 and 127");
		}
		catch(NumberFormatException e)
		{
			throw new IllegalArgumentException("Invalid level for enchantment " + name + ". Expected number between 1 and 127");
		}
		
		mMeta.addEnchant(enchant, level, true);
		return true;
	}
	
	private boolean decodeBook(String name, String content)
	{
		if(!(mMeta instanceof BookMeta))
			return false;
		
		if(name.equalsIgnoreCase("author"))
		{
			((BookMeta)mMeta).setAuthor(ChatColor.translateAlternateColorCodes('&', content));
			return true;
		}
		else if(name.equalsIgnoreCase("title"))
		{
			((BookMeta)mMeta).setTitle(ChatColor.translateAlternateColorCodes('&', content));
			return true;
		}
		else if(name.equalsIgnoreCase("book"))
		{
			((BookMeta)mMeta).setPages(content.split("\\|"));
			return true;
		}
		
		return false;
	}
	
	private boolean decodeStoredEnchants(String name, String content)
	{
		if(!(mMeta instanceof EnchantmentStorageMeta))
			return false;
		
		Enchantment enchant = Lookup.findEnchantmentByName(name);
		if(enchant == null)
			return false;
		
		int level;
		
		try
		{
			level = Integer.parseInt(content);
			if(level <= 0 || level > 127)
				throw new IllegalArgumentException("Invalid level for enchantment " + name + ". Level must be between 1 and 127");
		}
		catch(NumberFormatException e)
		{
			throw new IllegalArgumentException("Invalid level for enchantment " + name + ". Expected number between 1 and 127");
		}
		
		((EnchantmentStorageMeta)mMeta).addStoredEnchant(enchant, level, true);
		return true;
	}
	
	private FireworkEffect.Builder mFireworkBuilder;
	
	private FireworkEffect.Builder completeFirework(FireworkEffect.Builder current)
	{
		if(mFireworkBuilder != null)
		{
			((FireworkMeta)mMeta).addEffect(mFireworkBuilder.build());
			mFireworkBuilder = null;
			return FireworkEffect.builder();
		}
		
		return current;
	}
	
	private boolean decodeFirework(String name, String content)
	{
		if(!(mMeta instanceof FireworkMeta))
			return false;
		
		FireworkEffect.Builder builder = mFireworkBuilder;
		if(builder == null)
			builder = FireworkEffect.builder();
		
		if(name.equalsIgnoreCase("color") || name.equalsIgnoreCase("colour"))
		{
			builder = completeFirework(builder);
			
			String[] colours = content.split(",");
			for(String colour : colours)
			{
				if(colour.startsWith("#")) // Hex colour
				{
					if (colour.length() != 4 && colour.length() != 7)
						throw new IllegalArgumentException("Invalid value for " + name + ". Hex colour value " + colour + " must be 3 or 6 characters long");
					
					try
					{
						int val = Integer.parseInt(colour.substring(1), 16);
						builder.withColor(Color.fromRGB(val));
					}
					catch(NumberFormatException e)
					{
						throw new IllegalArgumentException("Invalid value for " + name + ". Hex colour value " + colour + " must be 3 or 6 characters long");
					}
				}
				else
				{
					boolean ok = false;
					for(DyeColor dye : DyeColor.values())
					{
						if(colour.equalsIgnoreCase(dye.name()))
						{
							builder.withColor(dye.getFireworkColor());
							ok = true;
							break;
						}
					}
					
					if(!ok)
						throw new IllegalArgumentException("Invalid value for " + name + ". Unknown named colour value " + colour);
				}
			}
		}
		else if(name.equalsIgnoreCase("fade"))
		{
			String[] colours = content.split(",");
			for(String colour : colours)
			{
				if(colour.startsWith("#")) // Hex colour
				{
					if (colour.length() != 4 && colour.length() != 7)
						throw new IllegalArgumentException("Invalid value for " + name + ". Hex colour value " + colour + " must be 3 or 6 characters long");
					
					try
					{
						int val = Integer.parseInt(colour.substring(1), 16);
						builder.withFade(Color.fromRGB(val));
					}
					catch(NumberFormatException e)
					{
						throw new IllegalArgumentException("Invalid value for " + name + ". Hex colour value " + colour + " must be 3 or 6 characters long");
					}
				}
				else
				{
					boolean ok = false;
					for(DyeColor dye : DyeColor.values())
					{
						if(colour.equalsIgnoreCase(dye.name()))
						{
							builder.withFade(dye.getFireworkColor());
							ok = true;
							break;
						}
					}
					
					if(!ok)
						throw new IllegalArgumentException("Invalid value for " + name + ". Unknown named colour value " + colour);
				}
			}
		}
		else if(name.equalsIgnoreCase("shape") || name.equalsIgnoreCase("type"))
		{
			boolean ok = false;
			for(FireworkEffect.Type type : FireworkEffect.Type.values())
			{
				if(content.equalsIgnoreCase(type.name()))
				{
					builder.with(type);
					ok = true;
					break;
				}
			}
			
			if(content.equalsIgnoreCase("large"))
			{
				builder.with(FireworkEffect.Type.BALL_LARGE);
				ok = true;
			}
			
			if(!ok)
				throw new IllegalArgumentException("Invalid value for " + name + ". Unknown firework type value " + content);
		}
		else if(name.equalsIgnoreCase("effect"))
		{
			for(String effect : content.split(","))
			{
				if(effect.equalsIgnoreCase("twinkle") || effect.equalsIgnoreCase("flicker"))
					builder.flicker(true);
				else if(effect.equalsIgnoreCase("trail"))
					builder.trail(true);
				else
					throw new IllegalArgumentException("Invalid value for " + name + ". Unknown firework effect " + effect);
			}
		}
		else
			return false;
		
		mFireworkBuilder = builder;
		return true;
	}
	
	private boolean decodeLeatherArmor(String name, String content)
	{
		if(!(mMeta instanceof LeatherArmorMeta))
			return false;
		
		if(name.equalsIgnoreCase("color") || name.equalsIgnoreCase("colour"))
		{
			String[] parts = content.split("(\\||,)");
			
			if(parts.length != 3)
				throw new IllegalArgumentException("Invalid value for " + name + ". Should be <red>,<green>,<blue>");

			((LeatherArmorMeta)mMeta).setColor(Color.fromRGB(getPosInt(parts[0], 0), getPosInt(parts[1], 0), getPosInt(parts[2], 0)));
			return true;
		}
		
		return false;
	}
	
	private boolean decodeMap(String name, String content)
	{
		if(!(mMeta instanceof MapMeta))
			return false;
		
		if(name.equalsIgnoreCase("scaling"))
		{
			((MapMeta)mMeta).setScaling(Boolean.valueOf(content));
			return true;
		}
		
		return false;
	}
	
	private PotionEffectType mPotionEffect = null;
	private int mPotionPower = -1;
	private int mPotionDuration = -1;
	
	private boolean decodePotion(String name, String content)
	{
		if(!(mMeta instanceof PotionMeta))
			return false;
		
		if(name.equalsIgnoreCase("effect"))
		{
			mPotionEffect = Lookup.findPotionEffectByName(content);
			if(mPotionEffect == null)
				throw new IllegalArgumentException("Invalid value for '" + name + "'. Should be a potion effect");
			
			return true;
		}
		else if(name.equalsIgnoreCase("power"))
		{
			try
			{
				mPotionPower = Integer.parseInt(content);
				if(mPotionPower <= 0)
					throw new IllegalArgumentException("Invalid value for '" + name + "'. Expected integer greater than 0 for Potion Power");
				mPotionPower--;
			}
			catch(NumberFormatException e)
			{
				throw new IllegalArgumentException("Invalid value for '" + name + "'. Expected integer greater than 0 for Potion Power");
			}
		}
		else if(name.equalsIgnoreCase("duration"))
		{
			boolean ticks = false;
			if (content.endsWith("t"))
			{
				ticks = true;
				content = content.substring(0, content.length()-1);
			}
			else if (content.endsWith("ticks"))
			{
				ticks = true;
				content = content.substring(0, content.length()-5);
			}
			
			try
			{
				mPotionDuration = Integer.parseInt(content);
				if(mPotionDuration <= 0)
					throw new IllegalArgumentException("Invalid value for '" + name + "'. Expected integer greater than 0 for Potion Duration");
				
				if(!ticks)
					mPotionDuration *= 20;
			}
			catch(NumberFormatException e)
			{
				throw new IllegalArgumentException("Invalid value for '" + name + "'. Expected integer greater than 0 for Potion Power");
			}
		}
		else
			return false;
		
		if(mPotionEffect != null && mPotionDuration > 0 && mPotionPower >= 0)
		{
			PotionMeta meta = (PotionMeta)mMeta;
			meta.addCustomEffect(mPotionEffect.createEffect(mPotionDuration, mPotionPower), true);
			mPotionEffect = null;
			mPotionDuration = mPotionPower = -1;
		}
		
		return true;
	}
	
	private boolean decodeSkull(String name, String content)
	{
		if(!(mMeta instanceof SkullMeta))
			return false;
		
		if(name.equalsIgnoreCase("player") || name.equalsIgnoreCase("owner"))
		{
			((SkullMeta)mMeta).setOwner(content);
			return true;
		}
		
		return false;
	}

    /**
     * This method needs to be watched is it used magic values to determine an entityType
     *
     * @param name
     * @param content
     * @return boolean if is a spawnEgg
     */
    @SuppressWarnings("deprecation")
	private boolean decodeSpawnEgg(String name, String content){
		if(mMeta instanceof SpawnEggMeta){
			if(item.getData() !=null){
				Byte data = item.getData().getData();
				if(data!=null){
                    EntityType type = EntityType.fromId(data);
                    if(type != null){
                        ((SpawnEggMeta) mMeta).setSpawnedType(type);
                    }

				}
			}
			return true;
		}else{
            return false;
        }
	}
}
