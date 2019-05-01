package au.com.addstar.monolith.template;

import org.apache.commons.lang.Validate;

public abstract class TemplateSetting<HolderType, Holder, V>
{
	private String[] mNames;
	private V mDefault;
	private Class<V> mClass;
	
	public TemplateSetting(String name, V def, Class<V> type)
	{
		mNames = new String[] {name};
		mDefault = def;
		mClass = type;
	}
	
	public TemplateSetting(String[] names, V def, Class<V> type)
	{
		Validate.isTrue(names.length > 0);
		mNames = names;
		mDefault = def;
		mClass = type;
	}
	
	public TemplateSetting(String name, String[] aliases, V def, Class<V> type)
	{
		mNames = new String[aliases.length+1];
		mNames[0] = name;
		System.arraycopy(aliases, 0, mNames, 1, aliases.length);
		mClass = type;
		mDefault = def;
	}
	
	public String[] getNames()
	{
		return mNames;
	}
	
	public V getDefault()
	{
		return mDefault;
	}
	
	public Class<V> getType()
	{
		return mClass;
	}
	
	public abstract boolean appliesTo(HolderType type);
	
	public abstract void apply(Holder object, V value);
	public void applyDefault(Holder object)
	{
		apply(object, mDefault);
	}
}
