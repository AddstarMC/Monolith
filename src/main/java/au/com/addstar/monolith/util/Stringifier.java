package au.com.addstar.monolith.util;

import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/**
 * Provides methods for converting objects into strings that can be read by the methods in {@link Parser}
 */
public final class Stringifier
{
	private Stringifier() {}
	
	public static String toString(EulerAngle angle)
	{
		return String.format("%.1f:%.1f:%.1f", angle.getX(), angle.getY(), angle.getZ());
	}
	
	public static String toString(Vector vector)
	{
		return String.format("%.1f:%.1f:%.1f", vector.getX(), vector.getY(), vector.getZ());
	}
	
	public static String toString(Object object)
	{
		if (object instanceof EulerAngle)
			return toString((EulerAngle)object);
		else if (object instanceof Vector)
			return toString((Vector)object);
		else
			return String.valueOf(object);
	}
}
