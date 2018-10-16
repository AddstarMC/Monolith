package au.com.addstar.monolith.properties;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.server.v1_13_R1.NBTBase;
import net.minecraft.server.v1_13_R1.NBTTagByte;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import net.minecraft.server.v1_13_R1.NBTTagDouble;
import net.minecraft.server.v1_13_R1.NBTTagFloat;
import net.minecraft.server.v1_13_R1.NBTTagInt;
import net.minecraft.server.v1_13_R1.NBTTagList;
import net.minecraft.server.v1_13_R1.NBTTagLong;
import net.minecraft.server.v1_13_R1.NBTTagShort;
import net.minecraft.server.v1_13_R1.NBTTagString;

/**
 * Represents a property that holds any object that implements the
 * {@link ConfigurationSerializable} interface
 */
public class CustomProperty extends PropertyBase<ConfigurationSerializable>
{
	/**
	 * Creates a new custom valued property
	 * @param name The name of the property
	 * @param owner The UUID of the owner for grouping purposes
	 * @param object The value to hold
	 */
	public CustomProperty(String name, UUID owner, ConfigurationSerializable object)
	{
		super(name, owner);
		tag.setByte("type", TYPE_CUSTOM);
		setValue(object);
	}
	
	CustomProperty(NBTTagCompound tag)
	{
		super(tag);
	}
	
	@Override
	public ConfigurationSerializable getValue()
	{
		NBTTagCompound root = tag.getCompound("value");
		Map<String, ?> values = fromNBTCompound(root);
		
		
		if (values.containsKey("=="))
		{
			try
			{
				Class<?> rawClass = Class.forName((String)values.get("=="));
				if (ConfigurationSerializable.class.isAssignableFrom(rawClass))
				{
					Class<? extends ConfigurationSerializable> clazz = rawClass.asSubclass(ConfigurationSerializable.class);
					return ConfigurationSerialization.deserializeObject(values, clazz);					
				}
				else
				{
					throw new IllegalStateException("This value cannot be deserialized");
				}
			}
			catch (ClassNotFoundException e)
			{
				throw new IllegalStateException("This value cannot be deserialized");
			}
		}
		else
			return ConfigurationSerialization.deserializeObject(values);
	}
	
	@Override
	public void setValue(ConfigurationSerializable value)
	{
		Map<String, Object> serialized = value.serialize();
		
		if (!serialized.containsKey("=="))
			serialized.put("==", value.getClass().getName());

		tag.set("value", toNBT(serialized));
	}
	
	private NBTTagCompound toNBT(Map<String, Object> map)
	{
		NBTTagCompound container = new NBTTagCompound();
		for (String key : map.keySet())
		{
			Object value = map.get(key);
			container.set(key, toNBT(value));
		}
		
		return container;
	}
	
	private NBTTagList toNBT(List<?> list)
	{
		NBTTagList container = new NBTTagList();
		int acceptedType = -1;
		for (Object object : list)
		{
			NBTBase base = toNBT(object);
			if (acceptedType != -1 && base.getTypeId() != acceptedType)
				throw new IllegalArgumentException("This list cannot be saved as it contains multiple types.");
			
			container.add(base);
			acceptedType = base.getTypeId();
		}
		
		return container;
	}
	
	private NBTBase toNBT(Object value)
	{
		if (value == null)
			return null;
		else if (value instanceof ConfigurationSerializable)
			return toNBT(((ConfigurationSerializable)value).serialize());
		else if (value instanceof Byte)
			return new NBTTagByte((Byte)value);
		else if (value instanceof Short)
			return new NBTTagShort((Short)value);
		else if (value instanceof Integer)
			return new NBTTagInt((Integer)value);
		else if (value instanceof Float)
			return new NBTTagFloat((Float)value);
		else if (value instanceof Double)
			return new NBTTagDouble((Double)value);
		else if (value instanceof Long)
			return new NBTTagLong((Long)value);
		else if (value instanceof Boolean)
			return new NBTTagByte((Boolean)value ? (byte)1 : (byte)0);
		else if (value instanceof String)
			return new NBTTagString((String)value);
		else if (value instanceof List<?>)
			return toNBT((List<?>)value);
		else
		{
			System.out.println("Serialize warning!: Unknown serialize type: " + value + " " + value.getClass());
			return new NBTTagString(value.toString()); 
		}
	}
	
	private Map<String, ?> fromNBTCompound(NBTTagCompound tag)
	{
		Map<String, Object> values = Maps.newHashMap();
		
		for (String key : tag.getKeys())
		{
			NBTBase value = tag.get(key);
			values.put(key, fromNBT(value));
		}
		
		return values;
	}
	
	private List<?> fromNBTList(NBTTagList tag)
	{
		List<Object> values = Lists.newArrayList();
		
		for (int i = 0; i < tag.size(); ++i)
		{
			values.add(fromNBT(tag.c(i)));
		}
		
		return values;
	}
	
	private Object fromNBT(NBTBase tag)
	{
		if (tag instanceof NBTTagCompound)
			return fromNBTCompound((NBTTagCompound)tag);
		else if (tag instanceof NBTTagByte)
			return ((NBTTagByte)tag).g();
		else if (tag instanceof NBTTagShort)
			return ((NBTTagShort)tag).f();
		else if (tag instanceof NBTTagInt)
			return ((NBTTagInt)tag).e();
		else if (tag instanceof NBTTagLong)
			return ((NBTTagLong)tag).d();
		else if (tag instanceof NBTTagFloat)
			return ((NBTTagFloat)tag).i();
		else if (tag instanceof NBTTagDouble)
            return ((NBTTagDouble) tag).asDouble();
        else if (tag instanceof NBTTagString)
			return tag.b_();
		else if (tag instanceof NBTTagList)
			return fromNBTList((NBTTagList)tag);
		else
			throw new AssertionError("Unsupported tag");
	}
}
