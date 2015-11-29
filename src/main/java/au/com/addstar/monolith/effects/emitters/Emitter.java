package au.com.addstar.monolith.effects.emitters;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import au.com.addstar.monolith.attachments.Attachment;
import au.com.addstar.monolith.effects.BaseEffect;

public abstract class Emitter
{
	private EmitterManager manager;

	private BaseEffect effect;
	private Attachment attachment;

	private Set<Player> viewers;

	public Emitter(Attachment attachment)
	{
		this.attachment = attachment;
		viewers = Sets.newHashSet();
	}

	public BaseEffect getEffect()
	{
		return effect;
	}

	public void setEffect(BaseEffect effect)
	{
		Preconditions.checkNotNull(effect);
		this.effect = effect;
	}

	public Attachment getAttachment()
	{
		return attachment;
	}

	public void setAttachment(Attachment attachment)
	{
		this.attachment = attachment;
	}

	public boolean isPrivate()
	{
		return !viewers.isEmpty();
	}

	/**
	 * Gets the viewers of this emitter.
	 * If this is empty, everyone will be able to see this effect if in range.
	 * If not, only the players in here will be able to see it.
	 * 
	 * @return A modifiable set of players
	 */
	public Set<Player> getViewers()
	{
		return viewers;
	}

	protected void emit()
	{
		if (effect == null)
			return;
		
		Location location = attachment.getLocation();
		if ( viewers.isEmpty() )
			effect.spawn(location);
		else
		{
			for (Player player : viewers)
				effect.spawn(player, location);
		}
	}

	void setManager(EmitterManager manager)
	{
		this.manager = manager;
	}

	/**
	 * Starts this emitter
	 */
	public final void start()
	{
		Preconditions.checkState(manager != null, "This emitter does not belong to a manager");

		onStart();
		if (isRunning())
			manager.setActive(this);
	}

	/**
	 * Stops this emitter
	 */
	public final void stop()
	{
		Preconditions.checkState(manager != null, "This emitter does not belong to a manager");

		onStop();
		manager.removeActive(this);
	}

	protected abstract void onStart();

	protected abstract void onStop();

	protected abstract void onTick();

	/**
	 * Checks if the emitter is running
	 * 
	 * @return True if it is running
	 */
	public abstract boolean isRunning();
}
