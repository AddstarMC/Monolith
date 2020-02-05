/*
 * Copyright (c) 2020. AddstarMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute,
 *  sublicense, and/or copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

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
