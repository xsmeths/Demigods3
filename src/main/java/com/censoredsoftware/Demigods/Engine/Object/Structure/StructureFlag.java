package com.censoredsoftware.Demigods.Engine.Object.Structure;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.censoredsoftware.Demigods.Engine.Demigods;
import com.censoredsoftware.Demigods.Engine.Utility.DataUtility;

public enum StructureFlag implements Demigods.ListedStructureFlag
{
	PROTECTED_BLOCKS(new ProtectedBlocks()), NO_GRIEFING(new NoGrief()), NO_PVP(new NoPvp()), PRAYER_LOCATION(null), TRIBUTE_LOCATION(null);

	private Flag flag;

	private StructureFlag(Flag flag)
	{
		this.flag = flag;
	}

	public Flag getFlag()
	{
		return this.flag;
	}
}

class ProtectedBlocks implements Flag
{
	@Override
	public Listener getUniqueListener()
	{
		return new Listener()
		{
			@EventHandler(priority = EventPriority.HIGHEST)
			private void onBlockBreak(BlockBreakEvent event)
			{
				if(Structure.partOfStructureWithFlag(event.getBlock().getLocation(), StructureFlag.PROTECTED_BLOCKS)) event.setCancelled(true);
			}
		};
	}
}

class NoGrief implements Flag
{
	@Override
	public Listener getUniqueListener()
	{
		return new Listener()
		{
			@EventHandler(priority = EventPriority.HIGHEST)
			private void onBlockBreak(BlockBreakEvent event)
			{
				if(Structure.isInRadiusWithFlag(event.getBlock().getLocation(), StructureFlag.NO_GRIEFING)) event.setCancelled(true);
			}

			@EventHandler(priority = EventPriority.HIGHEST)
			public void onEntityExplode(final EntityExplodeEvent event)
			{
				final StructureSave save = Structure.getInRadiusWithFlag(event.getLocation(), StructureFlag.NO_GRIEFING);
				if(save == null) return;

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Demigods.plugin, new Runnable()
				{
					@Override
					public void run()
					{
						// Remove all drops from explosion zone
						for(Item drop : event.getLocation().getWorld().getEntitiesByClass(Item.class))
						{
							drop.remove();
							continue;
						}
					}
				}, 1);

				if(DataUtility.hasTimed("explode", "structure")) return;
				DataUtility.saveTimed("explode", "structure", true, 3);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Demigods.plugin, new Runnable()
				{
					@Override
					public void run()
					{
						save.generate();
					}
				}, 30);
			}
		};
	}
}

class NoPvp implements Flag
{
	@Override
	public Listener getUniqueListener()
	{
		return new Listener()
		{
			@EventHandler(priority = EventPriority.HIGHEST)
			private void onBlockBreak(BlockBreakEvent event)
			{
				// TODO
			}
		};
	}
}