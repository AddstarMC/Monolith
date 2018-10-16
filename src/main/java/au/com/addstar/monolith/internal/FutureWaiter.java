package au.com.addstar.monolith.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.Bukkit;

import au.com.addstar.monolith.Monolith;
import au.com.addstar.monolith.lookup.LookupCallback;

public class FutureWaiter<V> implements Runnable
{
	private Future<V> mFuture;
	private LookupCallback<V> mCallback;
	private long mMaxTime;
	public FutureWaiter(Future<V> future, LookupCallback<V> callback)
	{
		mFuture = future;
		mCallback = callback;
		mMaxTime = -1;
	}
	
	public FutureWaiter(Future<V> future, LookupCallback<V> callback, int maxTime, TimeUnit unit)
	{
		this(future, callback);
		mMaxTime = unit.toMillis(maxTime);
	}
	
	@Override
	public void run()
	{
		try
		{
			V value;
			if (mMaxTime < 0)
				value = mFuture.get();
			else
				value = mFuture.get(mMaxTime, TimeUnit.MILLISECONDS);
			
			if (value == null)
				onFailure(null);
			else
				onSuccess(value);
		}
		catch (ExecutionException | InterruptedException | TimeoutException e)
		{
			onFailure(e);
		}
	}

	private void onSuccess(final V value)
	{
		Bukkit.getScheduler().runTask(Monolith.getInstance(), () -> mCallback.onResult(true, value, null));
	}
	
	private void onFailure(final Throwable error)
	{
		Bukkit.getScheduler().runTask(Monolith.getInstance(), () -> mCallback.onResult(false, null, error));
	}
}
