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
