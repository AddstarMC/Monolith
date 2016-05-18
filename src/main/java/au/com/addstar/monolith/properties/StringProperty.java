package au.com.addstar.monolith.properties;

import java.util.UUID;

import net.minecraft.server.v1_9_R2.NBTTagCompound;

/**
 * Represents a property that holds a String
 */
public class StringProperty extends PropertyBase<String>
{
	/**
	 * Creates a new string based property
	 * @param name The name of the property
	 * @param owner The UUID of the owner for grouping purposes
	 * @param value The value to hold
	 */
	public StringProperty(String name, UUID owner, String value)
	{
		super(name, owner);
		tag.setByte("type", TYPE_STRING);
		setValue(value);
	}
	
	StringProperty(NBTTagCompound tag)
	{
		super(tag);
	}
	
	@Override
	public String getValue()
	{
		return tag.getString("value");
	}
	
	@Override
	public void setValue(String value)
	{
		tag.setString("value", value);
	}
}
