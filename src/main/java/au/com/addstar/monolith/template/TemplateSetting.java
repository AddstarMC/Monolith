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

package au.com.addstar.monolith.template;

import org.apache.commons.lang.Validate;

public abstract class TemplateSetting<HolderType, Holder, V>
{
	private String[] mNames;
	private V mDefault;
	private Class<V> mClass;
	
	public TemplateSetting(String name, V def, Class<V> type)
	{
		mNames = new String[] {name};
		mDefault = def;
		mClass = type;
	}
	
	public TemplateSetting(String[] names, V def, Class<V> type)
	{
		Validate.isTrue(names.length > 0);
		mNames = names;
		mDefault = def;
		mClass = type;
	}
	
	public TemplateSetting(String name, String[] aliases, V def, Class<V> type)
	{
		mNames = new String[aliases.length+1];
		mNames[0] = name;
		System.arraycopy(aliases, 0, mNames, 1, aliases.length);
		mClass = type;
		mDefault = def;
	}
	
	public String[] getNames()
	{
		return mNames;
	}
	
	public V getDefault()
	{
		return mDefault;
	}
	
	public Class<V> getType()
	{
		return mClass;
	}
	
	public abstract boolean appliesTo(HolderType type);
	
	public abstract void apply(Holder object, V value);
	public void applyDefault(Holder object)
	{
		apply(object, mDefault);
	}
}
