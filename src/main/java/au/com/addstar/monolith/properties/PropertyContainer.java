package au.com.addstar.monolith.properties;

import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface PropertyContainer extends Cloneable
{
	 PropertyBase<?> get(String name, UUID owner) throws PropertyClassException;
	 String getString(String name, UUID owner) throws ClassCastException;
	 Integer getInt(String name, UUID owner) throws ClassCastException;
	 Double getFloat(String name, UUID owner) throws ClassCastException;
	 ConfigurationSerializable getCustom(String name, UUID owner) throws ClassCastException;
	
	 void add(PropertyBase<?> property);
	 void remove(String name, UUID owner);
	 void clear(UUID owner);
	 void clear();
	
	 Iterable<PropertyBase<?>> getAllProperties(UUID owner);
	 Iterable<PropertyBase<?>> getAllProperties();
	
	 PropertyContainer clone();
}
