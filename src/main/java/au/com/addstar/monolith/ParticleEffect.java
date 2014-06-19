package au.com.addstar.monolith;

public enum ParticleEffect
{
	EXPLOSION_HUGE("hugeexplosion"),
    EXPLOSION_LARGE("largeexplode"),
    EXPLOSION("explode"),
    FIREWORKS_SPARK("fireworksSpark"),
    BUBBLE("bubble"),
    SUSPENDED("suspended"),
    DEPTH_SUSPENDED("depthsuspend"),
    MYCEL("townaura"),
    CRITICAL("crit"),
    MAGIC_CRITICAL("magicCrit"),
    MAGIC_WITCH("witchMagic"),
    SMOKE("smoke"),
    SMOKE_LARGE("largesmoke"),
    SPELL_POTION("mobSpell"),
    SPELL_AMBIENT("mobSpellAmbient"),
    SPELL("spell"),
    SPELL_INSTANT("instantSpell"),
    NOTE("note"),
    PORTAL("portal"),
    ENCHANTMENT("enchantmenttable"),
    FLAME("flame"),
    EMBER("lava"),
    FOOTSTEP("footstep"),
    SPLASH("splash"),
    WAKE("wake"),
    CLOUD("cloud"),
    REDSTONE("reddust"),
    SNOWBALL_POOF("snowballpoof"),
    DRIP_WATER("dripWater"),
    DRIP_LAVA("dripLava"),
    SNOW_SHOVEL("snowshovel"),
    SLIME("slime"),
    HEART("heart"),
    VILLAGER_ANGRY("angryVillager"),
    VILLAGER_HAPPY("happyVillager");
	
	private String mId;
	
	private ParticleEffect(String id)
	{
		mId = id;
	}
	
	public String getId()
	{
		return mId;
	}
}
