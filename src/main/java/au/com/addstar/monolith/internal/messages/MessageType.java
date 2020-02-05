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

package au.com.addstar.monolith.internal.messages;

import com.google.common.collect.HashBiMap;

public class MessageType
{
	private static HashBiMap<String, Class<?>> mTypeMap;
	
	public static void addType(Class<? extends Message<?>> classType, String id)
	{
		mTypeMap.put(id, classType);
	}
	
	public static Message<?> newMessage(String id)
	{
		try
		{
			Class<?> clazz = mTypeMap.get(id);
			if (clazz == null)
				return null;
			
			return (Message<?>)clazz.newInstance();
		}
		catch(IllegalAccessException e)
		{
			throw new AssertionError(e);
		}
		catch ( InstantiationException e )
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getId(Message<?> message)
	{
		return mTypeMap.inverse().get(message.getClass());
	}
	
	static
	{
		mTypeMap = HashBiMap.create();
		addType(MessageResolvePlayer.class, "PlayerNameToUUID");
		addType(MessageResolveUUID.class, "UUIDToPlayerName");
	}
}
