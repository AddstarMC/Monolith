package au.com.addstar.monolith.effects.emitters;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;

public class EmitterManager
{
	private final Plugin plugin;
	private BukkitTask task;

	private List<Emitter> allEmitters;
	private List<Emitter> activeEmitters;

	public EmitterManager(Plugin plugin)
	{
		this.plugin = plugin;

		allEmitters = Lists.newArrayList();
		activeEmitters = Lists.newArrayList();
	}

	public void launchTickTask()
	{
		task = Bukkit.getScheduler().runTaskTimer(plugin, this::doTick, 1, 1);
	}

	public void stopTickTask()
	{
		task.cancel();
	}

	/**
	 * Adds an emitter
	 * 
	 * @param emitter the emitter
	 */
	public void addEmitter(Emitter emitter)
	{
		allEmitters.add(emitter);
		emitter.setManager(this);
	}

	/**
	 * Removes an emitter
	 * 
	 * @param emitter the emitter
	 */
	public void removeEmitter(Emitter emitter)
	{
		allEmitters.remove(emitter);
		activeEmitters.remove(emitter);
		emitter.setManager(null);
	}

	/**
	 * Gets all the added emitters.
	 * 
	 * @return An unmodifiable list of emitters
	 */
	public List<Emitter> getEmitters()
	{
		return Collections.unmodifiableList(allEmitters);
	}

	void setActive(Emitter emitter)
	{
		if (!activeEmitters.contains(emitter))
			activeEmitters.add(emitter);
	}

	void removeActive(Emitter emitter)
	{
		activeEmitters.remove(emitter);
	}

	private void doTick()
	{
		Iterator<Emitter> it = activeEmitters.iterator();

		while (it.hasNext())
		{
			Emitter emitter = it.next();

			emitter.onTick();

			if (!emitter.isRunning())
				it.remove();
		}
	}
}
