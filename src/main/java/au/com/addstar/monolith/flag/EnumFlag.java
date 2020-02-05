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

package au.com.addstar.monolith.flag;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import au.com.addstar.monolith.Monolith;
import au.com.addstar.monolith.command.BadArgumentException;

public class EnumFlag<T extends Enum<T>> extends Flag<T>
{
	private Class<T> mEnumClass;
	private EnumSet<T> mEnum;
	private ArrayList<String> mNameList;
	
	public EnumFlag(Class<T> clazz)
	{
		mEnumClass = clazz;
		mEnum = EnumSet.allOf(clazz);
		mNameList = new ArrayList<>(mEnum.size());
		for(T e : mEnum)
			mNameList.add(e.name());
	}
	
	public EnumFlag()
	{
	}
	
	@Override
	public T parse( Player sender, String[] args ) throws IllegalArgumentException, BadArgumentException
	{
		if(args.length != 1)
			throw new IllegalArgumentException("<value>");
	
		for(T e : mEnum)
		{
			if(e.name().equalsIgnoreCase(args[0]))
				return e;
		}
		
		throw new BadArgumentException(0, "Unknown value");
	}

	@Override
	public List<String> tabComplete( Player sender, String[] args )
	{
		if(args.length == 1)
			return Monolith.matchStrings(args[0], mNameList);
		
		return null;
	}

	@Override
	public void save( ConfigurationSection section )
	{
		section.set("value", value.name());
		section.set("enum", mEnumClass.getName());
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public void read( ConfigurationSection section ) throws InvalidConfigurationException
	{
		String name = section.getString("value");
		
		try
		{
			mEnumClass = (Class<T>)Class.forName(section.getString("enum"));
			mEnum = EnumSet.allOf(mEnumClass);
			mNameList = new ArrayList<>(mEnum.size());
			for(T e : mEnum)
				mNameList.add(e.name());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new InvalidConfigurationException("Could not find enum " + section.getString("enum"));
		}
		
		for(T e : mEnum)
		{
			if(e.name().equals(name))
			{
				value = e;
				break;
			}
		}
	}

	@Override
	public String getValueString()
	{
		return value.name();
	}

}
