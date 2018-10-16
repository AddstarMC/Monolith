package au.com.addstar.monolith.properties;

import java.util.UUID;

import net.minecraft.server.v1_13_R1.NBTTagCompound;

/**
 * Represents a property that holds a double
 */
public class FloatProperty extends PropertyBase<Double>
{
	/**
	 * Creates a new double based property
	 * @param name The name of the property
	 * @param owner The UUID of the owner for grouping purposes
	 * @param value The value to hold
	 */
	public FloatProperty(String name, UUID owner, double value)
	{
		super(name, owner);
		tag.setByte("type", TYPE_FLOAT);
		setValue(value);
	}
	
	FloatProperty(NBTTagCompound tag)
	{
		super(tag);
	}
	
	@Override
	public Double getValue()
	{
		return tag.getDouble("value");
	}
	
	@Override
	public void setValue(Double value)
	{
		tag.setDouble("value", value);
	}
}
