package au.com.addstar.monolith.internal.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import au.com.addstar.monolith.lookup.PlayerDefinition;

import com.google.common.collect.Lists;

public class MessageResolveUUID extends Message<List<PlayerDefinition>>
{
	private int id;
	private Iterable<UUID> ids;
	private List<PlayerDefinition> result;

	public MessageResolveUUID( int id, Iterable<UUID> names )
	{
		this.id = id;
		this.ids = names;
	}

	public MessageResolveUUID() {}

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
			UUID id = UUID.fromString(parts[0]);
			result.add(new PlayerDefinition(id, parts[1]));
		}
	}

	@Override
	public void write( DataOutputStream out ) throws IOException
	{
		out.writeInt(id);
		out.writeUTF(StringUtils.join(ids.iterator(), ';'));
	}

	@Override
	public boolean isSource( Message<?> message )
	{
		if ( message instanceof MessageResolveUUID )
		{
			return ((MessageResolveUUID) message).id == id;
		}
		return false;
	}

	@Override
	public List<PlayerDefinition> getReply()
	{
		return result;
	}
}
