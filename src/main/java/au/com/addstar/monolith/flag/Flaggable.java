package au.com.addstar.monolith.flag;

import java.util.Map;

public interface Flaggable
{
	public Map<String, Flag<?>> getFlags();
	
	public void addFlag(String name, Flag<?> flag) throws IllegalArgumentException;
	
	public Flag<?> getFlag(String name);
	
	public boolean hasFlag(String name);
	
	public <Type> void onFlagChanged(String name, Flag<Type> flag, Type oldValue);
}
