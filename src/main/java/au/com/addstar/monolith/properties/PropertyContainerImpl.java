package au.com.addstar.monolith.properties;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;

public class PropertyContainerImpl implements PropertyContainer
{
	private NBTTagList root;
	
	public PropertyContainerImpl(NBTTagList root)
	{
		this.root = root;
	}
	
	public NBTTagList getRoot()
	{
		return root;
	}
	
	@Override
	public PropertyBase<?> get(String name, UUID owner)
	{
		for (int i = 0; i < root.size(); ++i)
		{
			NBTTagCompound raw = root.get(i);
			PropertyBase<?> property = loadProperty(raw);
			if (property.getName() != null) {
			if (property.getName().equals(name) && property.getOwner().equals(owner))
				return property;
		}
		}
		
		return null;
	}
	
	@Override
	public String getString(String name, UUID owner) throws ClassCastException
	{
		PropertyBase<?> prop = get(name, owner);
		if (prop instanceof StringProperty)
			return ((StringProperty)prop).getValue();
		else if (prop == null)
			return null;
		else
			throw new ClassCastException("Property type does not match");
	}
	
	@Override
	public Integer getInt(String name, UUID owner) throws ClassCastException
	{
		PropertyBase<?> prop = get(name, owner);
		if (prop instanceof IntegerProperty)
			return ((IntegerProperty)prop).getValue();
		else if (prop == null)
			return null;
		else
			throw new ClassCastException("Property type does not match");
	}
	
	@Override
	public Double getFloat(String name, UUID owner) throws ClassCastException
	{
		PropertyBase<?> prop = get(name, owner);
		if (prop instanceof FloatProperty)
			return ((FloatProperty)prop).getValue();
		else if (prop == null)
			return null;
		else
			throw new ClassCastException("Property type does not match");
	}
	
	@Override
	public ConfigurationSerializable getCustom(String name, UUID owner) throws ClassCastException
	{
		PropertyBase<?> prop = get(name, owner);
		if (prop instanceof CustomProperty)
			return ((CustomProperty)prop).getValue();
		else if (prop == null)
			return null;
		else
			throw new ClassCastException("Property type does not match");
	}

	@Override
	public void add(PropertyBase<?> property)
	{
		// Remove any existing one if any
		remove(property.getName(), property.getOwner());
		root.add(property.getTag());
	}

	@Override
	public void remove(String name, UUID owner)
	{
		for (int i = 0; i < root.size(); ++i) {
			NBTTagCompound raw = root.get(i);
			PropertyBase<?> property = loadProperty(raw);
			if (property.getName() != null) {
				if (property.getName().equals(name) && property.getOwner().equals(owner)) {
					root.remove(i);
					break;
				}
			}

		}
	}

	@Override
	public void clear(UUID owner)
	{
		for (int i = 0; i < root.size(); ++i)
		{
			NBTTagCompound raw = root.get(i);
			PropertyBase<?> property = loadProperty(raw);
			if (property.getOwner() != null) {
				if (property.getOwner().equals(owner)) {
					root.remove(i);
					--i;
				}
			}
		}
	}

	@Override
	public void clear()
	{
		while(!root.isEmpty())
			root.remove(0);
	}

	@Override
	public Iterable<PropertyBase<?>> getAllProperties(final UUID owner)
	{
		return () -> Iterators.filter(new PropertyIterator(), property -> property.getOwner().equals(owner));
	}

	@Override
	public Iterable<PropertyBase<?>> getAllProperties()
	{
		return () -> new PropertyIterator();
	}
	
	@Override
	public PropertyContainerImpl clone()
	{
		return new PropertyContainerImpl((NBTTagList)root.clone());
	}
	
	private static PropertyBase<?> loadProperty(NBTTagCompound tag)
	{
		switch (tag.getByte("type"))
		{
		case PropertyBase.TYPE_STRING:
			return new StringProperty(tag);
		case PropertyBase.TYPE_INTEGER:
			return new IntegerProperty(tag);
		case PropertyBase.TYPE_FLOAT:
			return new FloatProperty(tag);
		case PropertyBase.TYPE_CUSTOM:
			return new CustomProperty(tag);
		default:
			return null;
		}
	}
	
	private class PropertyIterator implements Iterator<PropertyBase<?>>
	{
		private int index = 0;
		
		@Override
		public boolean hasNext()
		{
			return index < root.size();
		}

		@Override
		public PropertyBase<?> next()
		{
			NBTTagCompound raw = root.get(index++);
			return loadProperty(raw);
		}
		
		@Override
		public void remove()
		{
			if (index > 0)
				root.remove(--index);
		}
	}
}
