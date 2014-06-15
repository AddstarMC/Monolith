package au.com.addstar.monolith;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Monolith extends JavaPlugin
{
	private static Monolith mInstance;
	
	public static Monolith getInstance()
	{
		return mInstance;
	}
	
	@Override
	public void onEnable()
	{
		mInstance = this;
		Bukkit.getPluginManager().registerEvents(new Listeners(), this);
	}

}
