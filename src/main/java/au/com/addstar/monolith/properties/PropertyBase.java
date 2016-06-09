package au.com.addstar.monolith.properties;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import net.minecraft.server.v1_10_R1.NBTTagCompound;

/**
 * Properties are pieces of data that you can attach onto various objects.
 * They can store various primitive types, and any type that implements
 * {@link ConfigurationSerializable}
 * 
 * @param <T> The type of value contained within the property
 */
public abstract class PropertyBase<T>
{
	static final byte TYPE_STRING = 0;
	static final byte TYPE_INTEGER = 1;
	static final byte TYPE_FLOAT = 2;
	static final byte TYPE_CUSTOM = 10;
	
	protected NBTTagCompound tag;
	
	protected PropertyBase(String name, UUID owner)
	{
		tag = new NBTTagCompound();
		tag.setString("name", name);
		tag.setLong("UUIDLeast", owner.getLeastSignificantBits());
		tag.setLong("UUIDMost", owner.getMostSignificantBits());
	}
	
	protected PropertyBase(NBTTagCompound tag)
	{
		this.tag = tag;
	}
	
	protected NBTTagCompound getTag()
	{
		return tag;
	}
	
	/**
	 * Gets the name of this property
	 * @return A name
	 */
	public final String getName()
	{
		return tag.getString("name");
	}
	
	/**
	 * Gets the UUID of the owner of the property.
	 * The owner is for grouping purposes
	 * @return A UUID
	 */
	public final UUID getOwner()
	{
		return new UUID(tag.getLong("UUIDMost"), tag.getLong("UUIDLeast"));
	}
	
	/**
	 * Gets the value of this property
	 * @return The stored value
	 */
	public abstract T getValue();
	
	/**
	 * Sets the value of this property
	 * @param value The new value to store
	 */
	public abstract void setValue(T value);
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof PropertyBase<?>))
			return false;
		
		PropertyBase<?> other = (PropertyBase<?>)obj;
		
		return getName().equals(other.getName()) && getOwner().equals(other.getOwner()) && Objects.equals(getValue(), other.getValue());
	}
}
