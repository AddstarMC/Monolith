package au.com.addstar.monolith.internal;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;

import au.com.addstar.monolith.internal.messages.Message;

public class MessageWaiter implements PluginMessageListener
{
	private List<WaitFuture<?>> mWaiting;

	public MessageWaiter()
	{
		mWaiting = Lists.newLinkedList();
	}

	public <T> ListenableFuture<T> waitForReply( Message<T> source )
	{
		WaitFuture<T> future = new WaitFuture<T>(source);
		mWaiting.add(future);

		return future;
	}

	private void checkMessage( Message<?> message )
	{
		Iterator<WaitFuture<?>> it = mWaiting.iterator();

		while (it.hasNext())
		{
			WaitFuture<?> future = it.next();

			if (future.isReply(message))
			{
				it.remove();
				future.done(message);
			}
			else if (future.isExpired())
			{
				it.remove();
				future.done(new TimeoutException());
			}
		}
	}

	@Override
	public void onPluginMessageReceived( String channel, Player player, byte[] data )
	{
		Message<?> message = Message.load(data);
		if ( message != null )
			checkMessage(message);
	}

	private class WaitFuture<T> extends AbstractFuture<T>
	{
		private Message<T> mSource;
		private long mExpireTime;

		public WaitFuture( Message<T> source )
		{
			mSource = source;
			mExpireTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
		}

		@SuppressWarnings( "unchecked" )
		public void done( Message<?> message )
		{
			set((T)message.getReply());
		}
		
		public void done( Throwable error )
		{
			setException(error);
		}

		public boolean isReply( Message<?> message )
		{
			return message.isSource(mSource);
		}
		
		public boolean isExpired()
		{
			return System.currentTimeMillis() >= mExpireTime;
		}
	}
}
