package au.com.addstar.monolith.flag;

import java.util.Map;

public interface Flaggable
{
	Map<String, Flag<?>> getFlags();
	
	void addFlag(String name, Flag<?> flag) throws IllegalArgumentException;
	
	Flag<?> getFlag(String name);
	
	boolean hasFlag(String name);
	
	<Type> void onFlagChanged(String name, Flag<Type> flag, Type oldValue);
}
