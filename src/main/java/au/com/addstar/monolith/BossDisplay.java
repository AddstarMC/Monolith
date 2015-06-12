package au.com.addstar.monolith;

import java.util.WeakHashMap;

import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BossDisplay
{
	private String mText = "";
	private float mValue = 1;
	private boolean mHasChanged = false;
	
	private WeakHashMap<MonoPlayer, WitherDisplay> mDragons = new WeakHashMap<MonoPlayer, WitherDisplay>();
	
	public BossDisplay()
	{
		
	}
	
	public BossDisplay(String text, float value)
	{
		Validate.notNull(text);
		Validate.isTrue(value >= 0 && value <= 1);
		mText = text;
		mValue = value;
	}
	
	public void setText(String text)
	{
		Validate.notNull(text);
		
		if(text.length() > 64)
			text = text.substring(0,64);
		mText = text;
		mHasChanged = true;
		updateAll();
	}
	
	public String getText()
	{
		return mText;
	}
	
	public float getPercent()
	{
		return mValue;
	}
	
	public void setPercent(float value)
	{
		Validate.isTrue(value >= 0 && value <= 1);
		mValue = value;
		mHasChanged = true;
		updateAll();
	}
	
	void onPlayerAdd(MonoPlayer player)
	{
		WitherDisplay display = new WitherDisplay(player.getPlayer());
		display.spawn();
		mDragons.put(player, display);
	}
	
	void onPlayerRemove(MonoPlayer player)
	{
		WitherDisplay display = mDragons.remove(player);
		if(display != null)
			display.remove();
	}
	
	public void refresh(MonoPlayer player)
	{
		WitherDisplay display = mDragons.remove(player);
		if(display != null)
			display.spawn();
	}
	
	
	public void updateAll()
	{
		for(WitherDisplay display : mDragons.values())
			display.update();
		
		mHasChanged = false;
	}
	
	public void update(MonoPlayer player)
	{
		WitherDisplay display = mDragons.get(player);
		if(display != null)
			display.update();
	}
	
	private class WitherDisplay
	{
		private static final int ENTITY_WITHER_ID = 9999999;
		
		private Player mPlayer;
		private Location mLocation;
		private EntityWither mWither;
		
		public WitherDisplay(Player player)
		{
			mPlayer = player;
			mLocation = player.getLocation();
		}
		
		public void update()
		{
			Location loc = getIntendedPosition();
			
			if(mHasChanged)
				updateStats();
			
			if(mLocation.getWorld() != loc.getWorld())
				spawn();
			else
			{
				double dist = mLocation.distanceSquared(loc);
				
				if(dist >= 10000)
					spawn();
				else if(dist > 30)
				{
					mLocation = loc;
					positionEntity();
				}
			}
		}
		
		private int toHealth()
		{
			// NOTE: For the time being, this is forced to be max health due to withers still showing the surrounding effect when they have less than half health
			return (int)mWither.getMaxHealth();
			//return (int)Math.min(Math.max(mValue * 200, 1), 200);
		}
		
		private Location getIntendedPosition()
		{
			Location loc = mPlayer.getLocation();
			loc.add(loc.getDirection().multiply(30));
			
			return loc;
		}
		
		public void remove()
		{
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(ENTITY_WITHER_ID);
			((CraftPlayer)mPlayer).getHandle().playerConnection.sendPacket(packet);
		}
		
		public void spawn()
		{
			mLocation = getIntendedPosition();
			
			mWither = new EntityWither(null);
			mWither.setCustomName(mText);
			mWither.setCustomNameVisible(true);
			mWither.setHealth(toHealth());
			mWither.setLocation(mLocation.getX(), mLocation.getY(), mLocation.getZ(), 0, 0);
			mWither.getDataWatcher().watch(0, (byte)(0x20)); // Invisible
			mWither.d(ENTITY_WITHER_ID); // Set entityId
			PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(mWither);
			((CraftPlayer)mPlayer).getHandle().playerConnection.sendPacket(packet);
		}
		
		private void positionEntity()
		{
			PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(ENTITY_WITHER_ID, mLocation.getBlockX() * 32, mLocation.getBlockY() * 32, mLocation.getBlockZ() * 32, (byte)0, (byte)0, false);
			((CraftPlayer)mPlayer).getHandle().playerConnection.sendPacket(packet);
		}
		
		private void updateStats()
		{
			mWither.setCustomName(mText);
			mWither.setCustomNameVisible(true);
			mWither.setHealth(toHealth());
			PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(ENTITY_WITHER_ID, mWither.getDataWatcher(), true);
			((CraftPlayer)mPlayer).getHandle().playerConnection.sendPacket(packet);
		}
		
		@Override
		public String toString()
		{
			return "WDisplay{" + mPlayer.getName() + "}";
		}
	}
}
