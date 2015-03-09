package au.com.addstar.monolith.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumSet;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import au.com.addstar.monolith.lookup.EntityDefinition;
import au.com.addstar.monolith.lookup.Lookup;
import au.com.addstar.monolith.lookup.MaterialDefinition;

public class Parser
{
	public static Long parseInteger(String value) throws IllegalArgumentException
	{
		int radix = 10;
		if (value.toLowerCase().startsWith("0x"))
		{
			radix = 16;
			value = value.substring(2);
		}
		else if (value.startsWith("0"))
		{
			radix = 8;
			value = value.substring(1);
		}
		
		return Long.valueOf(value, radix);
	}
	
	public static <T extends Enum<T>> T parseEnum(Class<T> type, String value) throws IllegalArgumentException
	{
		EnumSet<T> set = EnumSet.allOf(type);
		for (T e : set)
		{
			if (e.name().equalsIgnoreCase(value))
				return e;
		}
		
		throw new IllegalArgumentException("Unknown value " + value + " in enum " + type.getName());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T parse(Class<T> type, String value) throws IllegalArgumentException
	{
		if (type.equals(Float.class))
			return (T)Float.valueOf(value);
		else if (type.equals(Double.class))
			return (T)Double.valueOf(value);
		else if (type.equals(BigDecimal.class))
			return (T)new BigDecimal(value);
		else if (type.equals(Integer.class))
			return (T)Integer.valueOf(parseInteger(value).intValue());
		else if (type.equals(Short.class))
			return (T)Short.valueOf(parseInteger(value).shortValue());
		else if (type.equals(Byte.class))
			return (T)Byte.valueOf(parseInteger(value).byteValue());
		else if (type.equals(Long.class))
			return (T)parseInteger(value);
		else if (type.equals(BigInteger.class))
			return (T)new BigInteger(value);
		else if (type.equals(Boolean.class))
		{
			if (value.equalsIgnoreCase("true"))
				return (T)Boolean.TRUE;
			else if (value.equalsIgnoreCase("false"))
				return (T)Boolean.FALSE;
			else
			{
				if (value.matches("[0-9]+"))
					return (T)Boolean.valueOf(Integer.parseInt(value) != 0);
			}
			return (T)Boolean.FALSE;
		}
		else if (type.equals(String.class))
			return (T)value;
		else if (type.equals(Material.class))
		{
			MaterialDefinition def = Lookup.findItemByName(value);
			if (def == null)
				throw new IllegalArgumentException("Unknown material " + value);
			
			return (T)def.getMaterial();
		}
		else if (type.equals(MaterialData.class))
		{
			MaterialDefinition def = Lookup.findItemByName(value);
			if (def == null)
				throw new IllegalArgumentException("Unknown material " + value);
			
			return (T)def.asMaterialData();
		}
		else if (type.equals(MaterialDefinition.class))
		{
			MaterialDefinition def = Lookup.findItemByName(value);
			if (def == null)
				throw new IllegalArgumentException("Unknown material " + value);
			
			return (T)def;
		}
		else if (type.equals(ItemStack.class))
		{
			int count;
			if (value.matches("^[0-9]+[xX].+$"))
			{
				int pos = value.toLowerCase().indexOf("x");
				count = Integer.parseInt(value.substring(0,pos));
				value = value.substring(pos);
			}
			else
				count = -1;
			
			MaterialDefinition def = Lookup.findItemByName(value);
			if (def == null)
				throw new IllegalArgumentException("Unknown material " + value);
			
			if (count == -1)
				return (T)def.asItemStack(def.asItemStack(1).getMaxStackSize());
			else
				return (T)def.asItemStack(count);
		}
		else if (type.equals(EntityType.class))
		{
			EntityDefinition def = Lookup.findEntityByName(value);
			if (def == null)
				throw new IllegalArgumentException("Unknown entity " + value);
			
			return (T)def.getType();
		}
		else if (type.equals(EntityDefinition.class))
		{
			EntityDefinition def = Lookup.findEntityByName(value);
			if (def == null)
				throw new IllegalArgumentException("Unknown entity " + value);
			
			return (T)def;
		}
		else if (Enum.class.isAssignableFrom(type))
		{
			return (T)parseEnum(type.asSubclass(Enum.class), value);
		}
		
		throw new UnsupportedOperationException("Unable to parse a " + type.getName());
	}
}
