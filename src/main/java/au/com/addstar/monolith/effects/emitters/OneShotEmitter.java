package au.com.addstar.monolith.effects.emitters;

import au.com.addstar.monolith.attachments.Attachment;

public class OneShotEmitter extends Emitter
{
	public OneShotEmitter( Attachment attachment )
	{
		super(attachment);
	}

	@Override
	protected void onStart()
	{
		emit();
	}

	@Override
	protected void onStop()
	{
		// Do nothing
	}

	@Override
	protected void onTick()
	{
		// Do nothing
	}

	@Override
	public boolean isRunning()
	{
		return false;
	}
}
