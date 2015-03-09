package au.com.addstar.monolith.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.HashBiMap;

public class DynamicEnum<T> implements Iterable<T>
{
	private HashBiMap<T, String> mMembers;
	
	public DynamicEnum(Class<T> clazz)
	{
		mMembers = HashBiMap.create();
		registerAll(clazz);
	}
	
	@SuppressWarnings("unchecked")
	protected void registerAll(Class<T> matching)
	{
		for (Field field : getClass().getDeclaredFields())
		{
			if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && matching.isAssignableFrom(field.getType()))
			{
				try
				{
					T value = (T)field.get(null);
					if (value == null)
						continue;
					
					registerMember(value, field.getName());
				}
				catch (IllegalAccessException e)
				{
					// Should not happen
				}
			}
		}
	}
	
	public void registerMember(T member, String name)
	{
		Validate.isTrue(!mMembers.inverse().containsKey(name), "Cannot register member with existing name");
		mMembers.put(member, name);
	}
	
	public boolean isMember(T member)
	{
		return mMembers.containsKey(member);
	}
	
	@Override
	public Iterator<T> iterator()
	{
		return mMembers.keySet().iterator();
	}
	
	public Set<T> values()
	{
		return mMembers.keySet();
	}
	
	public T valueOf(String name)
	{
		T member = mMembers.inverse().get(name);
		if (member == null)
			throw new IllegalArgumentException("No such member " + name);
		
		return member;
	}
	
	public String nameOf(T value)
	{
		String name = mMembers.get(value);
		if (name == null)
			throw new IllegalArgumentException(value + " is not a member of this dyn enum");
		return name;
	}
}
