package au.com.addstar.monolith.properties;

import java.util.UUID;

import net.minecraft.server.v1_13_R1.NBTTagCompound;

/**
 * Represents a property that holds an integer
 */
public class IntegerProperty extends PropertyBase<Integer>
{
	/**
	 * Creates a new int based property
	 * @param name The name of the property
	 * @param owner The UUID of the owner for grouping purposes
	 * @param value The value to hold
	 */
	public IntegerProperty(String name, UUID owner, int value)
	{
		super(name, owner);
		tag.setByte("type", TYPE_INTEGER);
		setValue(value);
	}
	
	IntegerProperty(NBTTagCompound tag)
	{
		super(tag);
	}
	
	@Override
	public Integer getValue()
	{
		return tag.getInt("value");
	}
	
	@Override
	public void setValue(Integer value)
	{
		tag.setInt("value", value);
	}
}
