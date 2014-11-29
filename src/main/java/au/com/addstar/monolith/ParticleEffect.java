package au.com.addstar.monolith;

import net.minecraft.server.v1_8_R1.EnumParticle;

public enum ParticleEffect
{
	EXPLOSION_HUGE(EnumParticle.EXPLOSION_HUGE),
    EXPLOSION_LARGE(EnumParticle.EXPLOSION_LARGE),
    EXPLOSION(EnumParticle.EXPLOSION_NORMAL),
    FIREWORKS_SPARK(EnumParticle.FIREWORKS_SPARK),
    BUBBLE(EnumParticle.WATER_BUBBLE),
    SUSPENDED(EnumParticle.SUSPENDED),
    DEPTH_SUSPENDED(EnumParticle.SUSPENDED_DEPTH),
    MYCEL(EnumParticle.TOWN_AURA),
    CRITICAL(EnumParticle.CRIT),
    MAGIC_CRITICAL(EnumParticle.CRIT_MAGIC),
    MAGIC_WITCH(EnumParticle.SPELL_WITCH),
    SMOKE(EnumParticle.SMOKE_NORMAL),
    SMOKE_LARGE(EnumParticle.SMOKE_LARGE),
    SPELL_POTION(EnumParticle.SPELL_MOB),
    SPELL_AMBIENT(EnumParticle.SPELL_MOB_AMBIENT),
    SPELL(EnumParticle.SPELL),
    SPELL_INSTANT(EnumParticle.SPELL_INSTANT),
    NOTE(EnumParticle.NOTE),
    PORTAL(EnumParticle.PORTAL),
    ENCHANTMENT(EnumParticle.ENCHANTMENT_TABLE),
    FLAME(EnumParticle.FLAME),
    EMBER(EnumParticle.LAVA),
    FOOTSTEP(EnumParticle.FOOTSTEP),
    SPLASH(EnumParticle.WATER_SPLASH),
    WAKE(EnumParticle.WATER_WAKE),
    CLOUD(EnumParticle.CLOUD),
    REDSTONE(EnumParticle.REDSTONE),
    SNOWBALL_POOF(EnumParticle.SNOWBALL),
    DRIP_WATER(EnumParticle.DRIP_WATER),
    DRIP_LAVA(EnumParticle.DRIP_LAVA),
    SNOW_SHOVEL(EnumParticle.SNOW_SHOVEL),
    SLIME(EnumParticle.SLIME),
    HEART(EnumParticle.HEART),
    VILLAGER_ANGRY(EnumParticle.VILLAGER_ANGRY),
    VILLAGER_HAPPY(EnumParticle.VILLAGER_HAPPY),
	BARRIER(EnumParticle.BARRIER);
	
	private EnumParticle mInternal;
	private ParticleEffect(EnumParticle internal)
	{
		mInternal = internal;
	}
	
	EnumParticle getEffect()
	{
		return mInternal;
	}
}
