package au.com.addstar.monolith.template;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;

import com.google.common.collect.Maps;

public abstract class AbstractTemplate<HolderType, Holder>
{
	private Map<TemplateSetting<HolderType, Holder, Object>, Object> mSettings;
	
	public AbstractTemplate()
	{
		mSettings = Maps.newIdentityHashMap();
	}
	
	public abstract HolderType getType();
	
	@SuppressWarnings("unchecked")
	public <T> AbstractTemplate<HolderType, Holder> set(TemplateSetting<HolderType, Holder, T> setting, T value)
	{
		Validate.notNull(setting);
		if (!setting.appliesTo(getType()))
			throw new IllegalArgumentException("Setting " + setting.getNames()[0] + " does not apply to " + getType());
		
		mSettings.put((TemplateSetting<HolderType, Holder, Object>)setting, value);
		return this;
	}
	
	public AbstractTemplate<HolderType, Holder> clear(TemplateSetting<HolderType, Holder, ?> setting)
	{
		Validate.notNull(setting);
		
		mSettings.remove(setting);
		
		return this;
	}
	
	public boolean isSet(TemplateSetting<HolderType, Holder, ?> setting)
	{
		return mSettings.containsKey(setting);
	}
	
	protected void applyTemplate(Holder object)
	{
		// Apply specified settings
		for (Entry<TemplateSetting<HolderType, Holder, Object>, Object> entry : mSettings.entrySet())
			entry.getKey().apply(object, entry.getValue());
	}
}
