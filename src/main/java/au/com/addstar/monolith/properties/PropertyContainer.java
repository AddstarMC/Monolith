package au.com.addstar.monolith.properties;

import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface PropertyContainer extends Cloneable
{
	public PropertyBase<?> get(String name, UUID owner);
	public String getString(String name, UUID owner) throws ClassCastException;
	public Integer getInt(String name, UUID owner) throws ClassCastException;
	public Double getFloat(String name, UUID owner) throws ClassCastException;
	public ConfigurationSerializable getCustom(String name, UUID owner) throws ClassCastException;
	
	public void add(PropertyBase<?> property);
	public void remove(String name, UUID owner);
	public void clear(UUID owner);
	public void clear();
	
	public Iterable<PropertyBase<?>> getAllProperties(UUID owner);
	public Iterable<PropertyBase<?>> getAllProperties();
	
	public PropertyContainer clone();
}
