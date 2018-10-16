package au.com.addstar.monolith.template.internal;

import java.lang.invoke.MethodHandle;

import au.com.addstar.monolith.lookup.EntityDefinition;

public class EntityTemplateSetting<V> extends MethodLinkedTemplateSetting<EntityDefinition, Object, V>
{
	private Class<?> mClass;
	
	private EntityTemplateSetting(String name, String[] aliases, V def, Class<Object> clazz, Class<V> type, MethodHandle handle)
	{
		super(name, aliases, def, type, handle);
		mClass = clazz;
	}
	
	@SuppressWarnings("unchecked")
	public static <V> EntityTemplateSetting<V> createWithDefault(String name, Class<?> forClass, String setter, Class<V> type, V def, String... aliases)
	{
		MethodHandle handle = getHandle(forClass, setter, type);
		
		return new EntityTemplateSetting<>(name, aliases, def, (Class<Object>) forClass, type, handle);
	}
	
	@SuppressWarnings("unchecked")
	public static <V> EntityTemplateSetting<V> createWithDefault(String name, Class<?> forClass, String setter, V def, String... aliases)
	{
		if (def == null)
			throw new IllegalArgumentException("Unable to create template setting " + name + ": This method can only be used with a non null default value. Use the other create method");
		
		return createWithDefault(name, forClass, setter, (Class<V>)def.getClass(), def, aliases);
	}
	
	public static <V> EntityTemplateSetting<V> create(String name, Class<?> forClass, String setter, Class<V> type, String... aliases)
	{
		return createWithDefault(name, forClass, setter, type, null, aliases);
	}

	@Override
	public boolean appliesTo(EntityDefinition type)
	{
		return (mClass.isAssignableFrom(type.getType().getEntityClass()));
	}
}
