package com.censoredsoftware.Demigods.Engine.Object;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import redis.clients.johm.*;

import com.google.common.collect.Sets;

@Model
public class DLocation
{
	@Id
	private Long id;
	@Attribute
	@Indexed
	String world;
	@Attribute
	Double X;
	@Attribute
	Double Y;
	@Attribute
	Double Z;
	@Attribute
	Float pitch;
	@Attribute
	Float yaw;
	@Indexed
	@Attribute
	Integer regionX;
	@Indexed
	@Attribute
	Integer regionZ;

	void setWorld(String world)
	{
		this.world = world;
	}

	void setX(Double X)
	{
		this.X = X;
	}

	void setY(Double Y)
	{
		this.Y = Y;
	}

	void setZ(Double Z)
	{
		this.Z = Z;
	}

	void setYaw(Float yaw)
	{
		this.yaw = yaw;
	}

	void setPitch(Float pitch)
	{
		this.pitch = pitch;
	}

	void setRegion(Region region)
	{
		this.regionX = region.getX();
		this.regionZ = region.getZ();
	}

	public Location toLocation() throws NullPointerException
	{
		return new Location(Bukkit.getServer().getWorld(this.world), this.X, this.Y, this.Z, this.yaw, this.pitch);
	}

	public Long getId()
	{
		return this.id;
	}

	public Integer getRegionX()
	{
		return this.regionX;
	}

	public Integer getRegionZ()
	{
		return this.regionZ;
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

	public static class Util
	{
		public static DLocation create(String world, double X, double Y, double Z, float yaw, float pitch)
		{
			DLocation trackedLocation = new DLocation();
			trackedLocation.setWorld(world);
			trackedLocation.setX(X);
			trackedLocation.setY(Y);
			trackedLocation.setZ(Z);
			trackedLocation.setYaw(yaw);
			trackedLocation.setPitch(pitch);
			trackedLocation.setRegion(Region.Util.getRegion((int) X, (int) Z, world));
			save(trackedLocation);
			return trackedLocation;
		}

		public static DLocation create(Location location)
		{
			return create(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		}

		public static void save(DLocation location)
		{
			JOhm.save(location);
		}

		public static DLocation load(long id)
		{
			return JOhm.get(DLocation.class, id);
		}

		public static Set<DLocation> loadAll()
		{
			return JOhm.getAll(DLocation.class);
		}

		public static Set<DLocation> find(String attribute, Object value)
		{
			return Sets.newHashSet((List) JOhm.find(DLocation.class, attribute, value));
		}

		public static DLocation get(Location location)
		{
			for(DLocation tracked : loadAll())
			{
				if(location.equals(tracked)) return tracked;
			}
			return create(location);
		}
	}
}
