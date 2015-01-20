package au.com.addstar.monolith.internal.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import au.com.addstar.monolith.lookup.PlayerDefinition;

import com.google.common.collect.Lists;

public class MessageResolvePlayer extends Message<List<PlayerDefinition>>
{
	private int id;
	private Iterable<String> names;
	private List<PlayerDefinition> result;

	public MessageResolvePlayer( int id, Iterable<String> names )
	{
		this.id = id;
		this.names = names;
	}

	public MessageResolvePlayer() {}

	@Override
	public void read( DataInputStream in ) throws IOException
	{
		id = in.readInt();
		String strList = in.readUTF();

		result = Lists.newArrayList();

		if ( strList.isEmpty() )
			return;

		String[] pairList = strList.split(";");

		for (String pair : pairList)
		{
			String[] parts = pair.split(":");
			UUID id = UUID.fromString(parts[1]);
			result.add(new PlayerDefinition(id, parts[0]));
		}
	}

	@Override
	public void write( DataOutputStream out ) throws IOException
	{
		out.writeInt(id);
		out.writeUTF(StringUtils.join(names.iterator(), ';'));
	}

	@Override
	public boolean isSource( Message<?> message )
	{
		if ( message instanceof MessageResolvePlayer )
			return ((MessageResolvePlayer) message).id == id;
		return false;
	}

	@Override
	public List<PlayerDefinition> getReply()
	{
		return result;
	}
}
