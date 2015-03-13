package au.com.addstar.monolith.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumSet;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.BlockVector;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import au.com.addstar.monolith.lookup.EntityDefinition;
import au.com.addstar.monolith.lookup.Lookup;
import au.com.addstar.monolith.lookup.MaterialDefinition;

/**
 * A utility class for parsing many different values from strings
 */
public class Parser
{
	/**
	 * Parses longs from the provided string. Accepts octal and hex if the appropriate prefix is found (0 for octal, 0x for hex)
	 * @param value The value to parse
	 * @return A long containing the parsed value 
	 * @throws IllegalArgumentException Thrown if the value cannot be an integer
	 */
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
	
	/**
	 * Parses booleans including numerical booleans. In the case of numerical booleans, a value of 0 is false, all others are true
	 * @param value A value to parse
	 * @return The parsed value
	 * @throws IllegalArgumentException Thrown if the value cannot be parsed as a boolean
	 */
	public static Boolean parseBoolean(String value) throws IllegalArgumentException
	{
		if (value.equalsIgnoreCase("true"))
			return Boolean.TRUE;
		else if (value.equalsIgnoreCase("false"))
			return Boolean.FALSE;
		else
		{
			if (value.matches("[0-9]+"))
				return Boolean.valueOf(Integer.parseInt(value) != 0);
		}
		throw new IllegalArgumentException("Expected true, false, 1, or 0");
	}
	
	/**
	 * Parses enums from a string. Parsing is case insensitive and will try with and without underscores
	 * @param type The enum class
	 * @param value The value to parse
	 * @return The parsed value
	 * @throws IllegalArgumentException Thrown if the value cannot be parsed as one of the enum class
	 */
	public static <T extends Enum<T>> T parseEnum(Class<T> type, String value) throws IllegalArgumentException
	{
		EnumSet<T> set = EnumSet.allOf(type);
		for (T e : set)
		{
			if (e.name().equalsIgnoreCase(value))
				return e;
			if (e.name().contains("_"))
			{
				if (e.name().replace("_", "").equalsIgnoreCase(value))
					return e;
			}
		}
		
		throw new IllegalArgumentException("Unknown value " + value + " in enum " + type.getName());
	}
	
	/**
	 * Parses EulerAngle objects from a string.
	 * Expected format is 'x:y:z'
	 * @param value The value to parse
	 * @return The parsed EulerAngle
	 * @throws IllegalArgumentException Thrown if the value cannot be parsed
	 */
	public static EulerAngle parseEulerAngle(String value) throws IllegalArgumentException
	{
		String[] parts = value.split(":");
		if (parts.length != 3)
			throw new IllegalArgumentException("Expected EulerAngle in format 'x:y:z'");
		
		try
		{
			double x = Double.parseDouble(parts[0]);
			double y = Double.parseDouble(parts[1]);
			double z = Double.parseDouble(parts[2]);
			
			return new EulerAngle(x, y, z);
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("Expected EulerAngle in format 'x:y:z'"); 
		}
	}
	
	/**
	 * Parses MaterialDefinitions from a string.<br>
	 * Accepts the following:
	 * <ul>
	 * <li>Names defined in items.csv</li>
	 * <li>minecraft id. eg. minecraft:stone</li>
	 * <li>bukkit material. eg. STONE</li>
	 * </ul>
	 * 
	 * Except when using a name from items.csv, you may include a data value after
	 * the id. eg 'STONE:2' or 'minecraft:stone:2'
	 * @param value The string to parse
	 * @return The parsed MaterialDefinition
	 * @throws IllegalArgumentException Thrown if the value cannot be parsed as a material
	 */
	public static MaterialDefinition parseMaterialDefinition(String value) throws IllegalArgumentException
	{
		MaterialDefinition def = Lookup.findItemByName(value);
		
		if (def != null)
			return def;
		
		String[] parts = value.split(":");
		
		// Try by MC name
		short data = 0;
		Material mat;
		
		try
		{
			// With data value and prefix
			if (parts.length == 3)
			{
				mat = Lookup.findByMinecraftName(parts[0] + ":" + parts[1]);
				data = Short.parseShort(parts[2]);
			// Either data value, or prefix
			}
			else if (parts.length == 2)
			{
				if (parts[1].matches("[0-9]+"))
				{
					mat = Lookup.findByMinecraftName(parts[0]);
					data = Short.parseShort(parts[1]);
				}
				else
					mat = Lookup.findByMinecraftName(parts[0] + ":" + parts[1]);
			// No data value or prefix
			}
			else
			{
				mat = Lookup.findByMinecraftName(parts[0]);
				data = 0;
			}
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("Unknown data value. Expected an integer 0 or more");
		}
		
		// MC name lookup success
		if (mat != null)
			return new MaterialDefinition(mat, data);
		
		// Try by Bukkit name
		mat = Material.getMaterial(parts[0].toUpperCase());
		if (parts.length != 1)
		{
			try
			{
				data = Short.parseShort(parts[1]);
				if (data < 0)
					throw new IllegalArgumentException("Unknown data value " + parts[1] + ". Expected an integer 0 or more");
			}
			catch (NumberFormatException e)
			{
				throw new IllegalArgumentException("Unknown data value " + parts[1] + ". Expected an integer 0 or more");
			}
		}
		
		// Bukkit lookup success
		if (mat != null)
			return new MaterialDefinition(mat, data);
		
		throw new IllegalArgumentException("Unknown material " + value);
	}
	
	/**
	 * Parse an ItemStack from a string. <br>
	 * The expected format is '{count}x{type}' <br>
	 * Count must be a positive integer, type will be parsed with {@link #parseMaterialDefinition(String)}
	 * 
	 * @param value The string to parse
	 * @return The parsed ItemStack
	 * @throws IllegalArgumentException Thrown if the value could not be parsed
	 */
	public static ItemStack parseItemStack(String value) throws IllegalArgumentException
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
		
		MaterialDefinition def = parseMaterialDefinition(value);
		
		if (count == -1)
			return def.asItemStack(def.asItemStack(1).getMaxStackSize());
		else
			return def.asItemStack(count);
	}
	
	/**
	 * Parses Vector objects from a string.
	 * Expected format is 'x:y:z'
	 * @param value The value to parse
	 * @return The parsed Vector
	 * @throws IllegalArgumentException Thrown if the value cannot be parsed
	 */
	public static Vector parseVector(String value) throws IllegalArgumentException
	{
		String[] parts = value.split(":");
		if (parts.length != 3)
			throw new IllegalArgumentException("Expected Vector in format 'x:y:z'");
		
		try
		{
			double x = Double.parseDouble(parts[0]);
			double y = Double.parseDouble(parts[1]);
			double z = Double.parseDouble(parts[2]);
			
			return new Vector(x, y, z);
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("Expected Vector in format 'x:y:z'"); 
		}
	}
	
	/**
	 * A generic parse method which attempts to parse the value based on what type you want out.
	 * 
	 * @param type The type you want to get
	 * @param value The value to parse
	 * @return An instance of the specified type with a value equivalent to the input string
	 * @throws IllegalArgumentException Thrown if an error occurs while parsing the value
	 * @throws UnsupportedOperationException Thrown if the specified type cant be parsed by this method
	 */
	@SuppressWarnings("unchecked")
	public static <T> T parse(Class<T> type, String value) throws IllegalArgumentException, UnsupportedOperationException
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
			return (T)parseBoolean(value);
		else if (type.equals(String.class))
			return (T)value;
		else if (type.equals(Material.class))
			return (T)(parseMaterialDefinition(value).getMaterial());
		else if (type.equals(MaterialData.class))
			return (T)(parseMaterialDefinition(value).asMaterialData());
		else if (type.equals(MaterialDefinition.class))
			return (T)(parseMaterialDefinition(value));
		else if (type.equals(ItemStack.class))
			return (T)parseItemStack(value);
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
			return (T)parseEnum(type.asSubclass(Enum.class), value);
		else if (type.equals(Vector.class))
			return (T)parseVector(value);
		else if (type.equals(BlockVector.class))
			return (T)(parseVector(value).toBlockVector());
		else if (type.equals(EulerAngle.class))
			return (T)parseEulerAngle(value);
		
		throw new UnsupportedOperationException("Unable to parse a " + type.getName());
	}
}
