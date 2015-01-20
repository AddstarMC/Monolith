package au.com.addstar.monolith.internal.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import au.com.addstar.monolith.Monolith;

public abstract class Message<T>
{

	public abstract void read( DataInputStream in ) throws IOException;

	public abstract void write( DataOutputStream out ) throws IOException;

	public boolean isSource( Message<?> message )
	{
		return false;
	}

	public T getReply()
	{
		return null;
	}

	public static Message<?> load( byte[] data )
	{
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(stream);

		try
		{
			Message<?> message = MessageType.newMessage(in.readUTF());
			if (message == null)
				return null;

			message.read(in);
			return message;
		}
		catch ( IOException e )
		{
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] save( Message<?> message )
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try
		{
			String id = MessageType.getId(message);
			if (id == null)
			{
				Monolith.getInstance().getLogger().warning("Attempted to send unregistered plugin message type " + message.getClass());
				return null;
			}
			
			out.writeUTF(id);
			message.write(out);
		}
		catch ( IOException e )
		{
			// Should not happen
			throw new AssertionError(e);
		}

		return stream.toByteArray();
	}
}
