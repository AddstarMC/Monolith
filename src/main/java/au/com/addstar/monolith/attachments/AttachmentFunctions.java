package au.com.addstar.monolith.attachments;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.google.common.base.Function;

public final class AttachmentFunctions
{
	private AttachmentFunctions() {}
	
	/**
	 * Provides a function that computes the offset based on the entities look vector
	 * @param offset The offset relative to the look direction
	 * @return The Function
	 */
	public static <T extends LivingEntity> Function<T, Vector> lookRelativeOffset(Vector offset)
	{
		return new LookRelativeOffset<T>(offset);
	}
	
	private static class LookRelativeOffset<T extends LivingEntity> implements Function<T, Vector>
	{
		private Location location;
		private Vector offset;
		private Vector staging;
		
		public LookRelativeOffset(Vector offset)
		{
			this.offset = offset;
			staging = new Vector();
		}
		
		@Override
		public Vector apply( T entity )
		{
			if (location == null)
				location = entity.getLocation();
			else
				entity.getLocation(location);
			
			staging.setX(0);
			staging.setY(0);
			staging.setZ(0);
			
			// Location should be facing the entities facing dir
			Vector forward = location.getDirection();
			
			// Compute other axis
			Vector up = new Vector(0, 1, 0);
			Vector left = up.crossProduct(forward).normalize();
			up = forward.clone().crossProduct(left).normalize();
			
			// Final compute
			staging.setX(
				(forward.getX() * offset.getZ()) +
				(left.getX() * -offset.getX()) +
				(up.getX() * offset.getY())
				);
			staging.setY(
				(forward.getY() * offset.getZ()) +
				(left.getY() * -offset.getX()) +
				(up.getY() * offset.getY())
				);
			staging.setZ(
				(forward.getZ() * offset.getZ()) +
				(left.getZ() * -offset.getX()) +
				(up.getZ() * offset.getY())
				);

			return staging;
		}
	}
}
