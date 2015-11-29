package au.com.addstar.monolith.effects.emitters;

import java.util.concurrent.TimeUnit;

import au.com.addstar.monolith.attachments.Attachment;

public class ContinuousEmitter extends Emitter
{
	private boolean isRunning;
	private long emitTime;

	private long delay;
	private long interval;

	public ContinuousEmitter( Attachment attachment )
	{
		super(attachment);
	}

	/**
	 * Sets the time between the start and the first
	 * emission
	 * 
	 * @param delay The delay in whatever unit specified
	 * @param unit The time unit for the delay
	 */
	public void setDelay( long delay, TimeUnit unit )
	{
		this.delay = unit.toMillis(delay);
	}

	/**
	 * Gets the time between the start and the first
	 * emission
	 * 
	 * @param unit The time unit for the delay
	 * @return The delay time in {@code unit}
	 */
	public long getDelay( TimeUnit unit )
	{
		return TimeUnit.MILLISECONDS.convert(delay, unit);
	}

	/**
	 * Sets the time between emissions
	 * 
	 * @param interval The delay in whatever unit specified
	 * @param unit The time unit for the delay
	 */
	public void setInterval( long interval, TimeUnit unit )
	{
		this.interval = unit.toMillis(interval);
	}

	/**
	 * Gets the time between emissions
	 * 
	 * @param unit The time unit for the delay
	 * @return The delay time in {@code unit}
	 */
	public long getInterval( TimeUnit unit )
	{
		return TimeUnit.MILLISECONDS.convert(interval, unit);
	}

	@Override
	protected void onStart()
	{
		emitTime = System.currentTimeMillis() + delay;
		isRunning = true;
	}

	@Override
	protected void onStop()
	{
		isRunning = false;
	}

	@Override
	public boolean isRunning()
	{
		return isRunning;
	}

	@Override
	protected void onTick()
	{
		if ( System.currentTimeMillis() >= emitTime )
		{
			emit();
			emitTime = System.currentTimeMillis() + interval;
		}
	}
}
