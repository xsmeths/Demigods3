package com.censoredsoftware.Demigods.Engine.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.censoredsoftware.Demigods.Engine.Demigods;
import com.censoredsoftware.Demigods.Engine.Object.Structure.StructureInfo;
import com.censoredsoftware.Demigods.Engine.Utility.DataUtility;
import com.censoredsoftware.Demigods.Engine.Utility.StructureUtility;
import com.censoredsoftware.Demigods.Engine.Utility.TextUtility;

public class StructureListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Location location = event.getBlock().getLocation();
		if(StructureUtility.partOfStructureWithFlag(location, StructureInfo.Flag.PROTECTED_BLOCKS))
		{
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.YELLOW + Demigods.text.getText(TextUtility.Text.PROTECTED_BLOCK));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Location location = event.getBlock().getLocation();
		if(StructureUtility.partOfStructureWithFlag(location, StructureInfo.Flag.PROTECTED_BLOCKS))
		{
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.YELLOW + Demigods.text.getText(TextUtility.Text.PROTECTED_BLOCK));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		Location location = event.getBlock().getLocation();
		if(StructureUtility.partOfStructureWithFlag(location, StructureInfo.Flag.PROTECTED_BLOCKS)) event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockDamage(BlockDamageEvent event)
	{
		Location location = event.getBlock().getLocation();
		if(StructureUtility.partOfStructureWithFlag(location, StructureInfo.Flag.PROTECTED_BLOCKS)) event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonExtend(BlockPistonExtendEvent event)
	{
		for(Block block : event.getBlocks())
		{
			if(StructureUtility.partOfStructureWithFlag(block.getLocation(), StructureInfo.Flag.PROTECTED_BLOCKS))
			{
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonRetract(BlockPistonRetractEvent event)
	{
		if(StructureUtility.partOfStructureWithFlag(event.getBlock().getRelative(event.getDirection(), 2).getLocation(), StructureInfo.Flag.PROTECTED_BLOCKS) && event.isSticky()) event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		if(StructureUtility.getInRadiusWithFlag(event.getLocation(), StructureInfo.Flag.PROTECTED_BLOCKS) == null) return;

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Demigods.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				// Remove all drops from explosion zone
				for(Item drop : event.getLocation().getWorld().getEntitiesByClass(Item.class))
				{
					if(StructureUtility.getInRadiusWithFlag(drop.getLocation(), StructureInfo.Flag.PROTECTED_BLOCKS) != null)
					{
						drop.remove();
						continue;
					}
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
				if(StructureUtility.getInRadiusWithFlag(event.getLocation(), StructureInfo.Flag.PROTECTED_BLOCKS) != null) StructureUtility.getInRadiusWithFlag(event.getLocation(), StructureInfo.Flag.PROTECTED_BLOCKS).generate();
			}
		}, 30);
	}
}
