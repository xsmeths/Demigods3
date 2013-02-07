/*
	Copyright (c) 2013 The Demigods Team
	
	Demigods License v1
	
	This plugin is provided "as is" and without any warranty.  Any express or
	implied warranties, including, but not limited to, the implied warranties
	of merchantability and fitness for a particular purpose are disclaimed.
	In no event shall the authors be liable to any party for any direct,
	indirect, incidental, special, exemplary, or consequential damages arising
	in any way out of the use or misuse of this plugin.
	
	Definitions
	
	 1. This Plugin is defined as all of the files within any archive
	    file or any group of files released in conjunction by the Demigods Team,
	    the Demigods Team, or a derived or modified work based on such files.
	
	 2. A Modification, or a Mod, is defined as this Plugin or a derivative of
	    it with one or more Modification applied to it, or as any program that
	    depends on this Plugin.
	
	 3. Distribution is defined as allowing one or more other people to in
	    any way download or receive a copy of this Plugin, a Modified
	    Plugin, or a derivative of this Plugin.
	
	 4. The Software is defined as an installed copy of this Plugin, a
	    Modified Plugin, or a derivative of this Plugin.
	
	 5. The Demigods Team is defined as Alexander Chauncey and Alex Bennett
	    of http://www.clashnia.com/.
	
	Agreement
	
	 1. Permission is hereby granted to use, copy, modify and/or
	    distribute this Plugin, provided that:
	
	    a. All copyright notices within source files and as generated by
	       the Software as output are retained, unchanged.
	
	    b. Any Distribution of this Plugin, whether as a Modified Plugin
	       or not, includes this license and is released under the terms
	       of this Agreement. This clause is not dependant upon any
	       measure of changes made to this Plugin.
	
	    c. This Plugin, Modified Plugins, and derivative works may not
	       be sold or released under any paid license without explicit 
	       permission from the Demigods Team. Copying fees for the 
	       transport of this Plugin, support fees for installation or
	       other services, and hosting fees for hosting the Software may,
	       however, be imposed.
	
	    d. Any Distribution of this Plugin, whether as a Modified
	       Plugin or not, requires express written consent from the
	       Demigods Team.
	
	 2. You may make Modifications to this Plugin or a derivative of it,
	    and distribute your Modifications in a form that is separate from
	    the Plugin. The following restrictions apply to this type of
	    Modification:
	
	    a. A Modification must not alter or remove any copyright notices
	       in the Software or Plugin, generated or otherwise.
	
	    b. When a Modification to the Plugin is released, a
	       non-exclusive royalty-free right is granted to the Demigods Team
	       to distribute the Modification in future versions of the
	       Plugin provided such versions remain available under the
	       terms of this Agreement in addition to any other license(s) of
	       the initial developer.
	
	    c. Any Distribution of a Modified Plugin or derivative requires
	       express written consent from the Demigods Team.
	
	 3. Permission is hereby also granted to distribute programs which
	    depend on this Plugin, provided that you do not distribute any
	    Modified Plugin without express written consent.
	
	 4. The Demigods Team reserves the right to change the terms of this
	    Agreement at any time, although those changes are not retroactive
	    to past releases, unless redefining the Demigods Team. Failure to
	    receive notification of a change does not make those changes invalid.
	    A current copy of this Agreement can be found included with the Plugin.
	
	 5. This Agreement will terminate automatically if you fail to comply
	    with the limitations described herein. Upon termination, you must
	    destroy all copies of this Plugin, the Software, and any
	    derivatives within 48 hours.
 */

package com.legit2.Demigods.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.legit2.Demigods.DDivineBlocks;
import com.legit2.Demigods.Demigods;
import com.legit2.Demigods.DTributeValue;
import com.legit2.Demigods.Database.DDatabase;
import com.legit2.Demigods.Events.DivineBlock.AltarCreateEvent;
import com.legit2.Demigods.Events.DivineBlock.ShrineCreateEvent;
import com.legit2.Demigods.Events.DivineBlock.AltarCreateEvent.AltarCreateCause;
import com.legit2.Demigods.Libraries.DCharacter;
import com.legit2.Demigods.Libraries.DivineBlock;
import com.legit2.Demigods.Utilities.DCharUtil;
import com.legit2.Demigods.Utilities.DConfigUtil;
import com.legit2.Demigods.Utilities.DDataUtil;
import com.legit2.Demigods.Utilities.DObjUtil;
import com.legit2.Demigods.Utilities.DPlayerUtil;
import com.legit2.Demigods.Utilities.DMiscUtil;
import com.legit2.Demigods.Utilities.DZoneUtil;

public class DDivineBlockListener implements Listener
{
	static Demigods plugin;
	public static double FAVOR_MULTIPLIER = DConfigUtil.getSettingDouble("global_favor_multiplier");
	
	public DDivineBlockListener(Demigods instance)
	{
		plugin = instance;
	}
	
	/* --------------------------------------------
	 *  Handle DivineBlock Interactions
	 * --------------------------------------------
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void shrineBlockInteract(PlayerInteractEvent event)
	{
		// Return if the player is mortal
		if(!DPlayerUtil.isImmortal(event.getPlayer())) return;
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		// Define variables
		Location location = event.getClickedBlock().getLocation();
		Player player = event.getPlayer();
		DCharacter character = DPlayerUtil.getCurrentChar(player);
		String charAlliance = character.getAlliance();
		String charDeity = character.getDeity();
		
		removeShrine(player, location);
		
		if(event.getClickedBlock().getType().equals(Material.GOLD_BLOCK) && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getType() == Material.BOOK)
		{						
			try
			{
				// Shrine created!
				ShrineCreateEvent shrineEvent = new ShrineCreateEvent(character, location);
				DMiscUtil.getPlugin().getServer().getPluginManager().callEvent(shrineEvent);
				
				if(player.getItemInHand().getAmount() > 1)
				{
					ItemStack books = new ItemStack(player.getItemInHand().getType(), player.getInventory().getItemInHand().getAmount() - 1);
					player.setItemInHand(books);
				}
				else player.getInventory().remove(Material.BOOK);

				player.sendMessage(ChatColor.GRAY + "The " + ChatColor.YELLOW + charAlliance + "s" + ChatColor.GRAY + " are pleased...");
				player.sendMessage(ChatColor.GRAY + "You have created a Shrine in the name of " + ChatColor.YELLOW + charDeity + ChatColor.GRAY + "!");
			}
			catch(Exception e)
			{
				// Creation of shrine failed...
				e.printStackTrace();
			}
		}
		
		useShrine(player, location);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void shrineEntityInteract(PlayerInteractEntityEvent event)
	{
		// Define variables
		Location location = event.getRightClicked().getLocation().subtract(0.5, 1.0, 0.5);
		Player player = event.getPlayer();
		DCharacter character = DPlayerUtil.getCurrentChar(player);

		removeShrine(player, location);
		
		// Return if the player is mortal
		if(!character.isImmortal())
		{
			event.getPlayer().sendMessage(ChatColor.RED + "You must be immortal to use that!");
			return;
		}
		
		useShrine(player, location);
	}
	
	private void removeShrine(Player player, Location location)
	{
		// First handle admin wand
		if(DMiscUtil.hasPermissionOrOP(player, "demigods.admin") && DDataUtil.hasPlayerData(player, "temp_admin_wand") && DDataUtil.getPlayerData(player, "temp_admin_wand").equals(true) && player.getItemInHand().getTypeId() == DConfigUtil.getSettingInt("admin_wand_tool"))
		{
			if(DDataUtil.hasPlayerData(player, "temp_destroy_shrine") && System.currentTimeMillis() < DObjUtil.toLong(DDataUtil.getPlayerData(player, "temp_destroy_shrine")))
			{
				// We can destroy the Shrine
				DDivineBlocks.removeShrine(location);
				
				// Drop the block of gold and book
				location.getWorld().dropItemNaturally(location, new ItemStack(Material.GOLD_BLOCK, 1));
				location.getWorld().dropItemNaturally(location, new ItemStack(Material.BOOK, 1));
				
				// Save Divine Blocks
				DDatabase.saveDivineBlocks();
				player.sendMessage(ChatColor.GREEN + "Shrine removed!");
				return;
			}
			else
			{
				DDataUtil.savePlayerData(player, "temp_destroy_shrine", System.currentTimeMillis() + 5000);
				player.sendMessage(ChatColor.RED + "Right-click this Shrine again to remove it.");
				return;
			}
		}
	}
	
	private void useShrine(Player player, Location location)
	{
		DCharacter character = DPlayerUtil.getCurrentChar(player);
		try
		{
			// Check if block is divine
			int shrineOwner = DDivineBlocks.getShrineOwner(location);
			String shrineDeity = DDivineBlocks.getShrineDeity(location);
			if(shrineDeity == null) return;
						
			if(DDivineBlocks.isShrineBlock(location))
			{
				// Check if character has deity
				if(character.hasDeity(shrineDeity))
				{
					// Open the tribute inventory
					Inventory ii = DMiscUtil.getPlugin().getServer().createInventory(player, 27, "Shrine of " + shrineDeity);
					player.openInventory(ii);
					DDataUtil.saveCharData(character.getID(), "temp_tributing", shrineOwner);
					return;
				}
				player.sendMessage(ChatColor.YELLOW + "You must be allied to " + shrineDeity + " in order to tribute here.");
			}
		}
		catch(Exception e)
		{
			// Print error for debugging
			e.printStackTrace();
		}
	}
	
	/* --------------------------------------------
	 *  Handle Player Tributing
	 * --------------------------------------------
	 */	
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerTribute(InventoryCloseEvent event)
	{
		try
		{
			if(!(event.getPlayer() instanceof Player)) return;
			Player player = (Player)event.getPlayer();
			DCharacter character = DPlayerUtil.getCurrentChar(player);
			if(character == null || !character.isImmortal()) return;
			
			String charDeity = character.getDeity();
			int charID = character.getID();
			
			// If it isn't a tribute chest then break the method
			if(!event.getInventory().getName().contains("Shrine")) return;
			
			// Get the creator of the shrine
			int shrineOwnerID = DObjUtil.toInteger(DDataUtil.getCharData(character.getID(), "temp_tributing"));
			DDataUtil.removeCharData(charID, "temp_tributing"); 
			
			// Calculate value of chest
			int tributeValue = 0, items = 0;
			for(ItemStack ii : event.getInventory().getContents())
			{
				if(ii != null)
				{
					tributeValue += DTributeValue.getTributeValue(ii);
					items++;
				}
			}
			
			tributeValue *= FAVOR_MULTIPLIER;
			
			// Process tributes and send messages
			int favorBefore = character.getMaxFavor();
			int devotionBefore = character.getDevotion();
			
			// Update the character's favor and devotion
			character.addMaxFavor(tributeValue / 5);
			character.giveDevotion(tributeValue);
			
			if(character.getDevotion() > devotionBefore) player.sendMessage(ChatColor.GRAY + "Your devotion to " + ChatColor.YELLOW +  charDeity + ChatColor.GRAY + " has increased to " + ChatColor.GREEN +  character.getDevotion() + ChatColor.GRAY + ".");
			if(character.getMaxFavor() > favorBefore) player.sendMessage(ChatColor.GRAY + "Your favor cap has increased to " + ChatColor.GREEN +  character.getMaxFavor() + ChatColor.GRAY + ".");
			
			if(favorBefore != character.getMaxFavor() && devotionBefore != character.getDevotion() && items > 0)
			{
				// Update the shrine owner's devotion and let them know
				OfflinePlayer shrineOwnerPlayer = DCharUtil.getOwner(shrineOwnerID);
				if(!DCharUtil.getOwner(charID).equals(shrineOwnerPlayer))
				{
					// TODO: FIX THIS
					//DCharUtil.giveDevotion(shrineOwner, tributeValue / 7);
					if(shrineOwnerPlayer.isOnline())
					{
						((Player) shrineOwnerPlayer).sendMessage(ChatColor.YELLOW + "Someone just tributed at your shrine!");
						((Player) shrineOwnerPlayer).sendMessage(ChatColor.GRAY + "Your devotion has increased to " + DCharUtil.getChar(shrineOwnerID).getDevotion() + "!");
					}
				}
			}
			else
			{
				// If they aren't good enough let them know
				if(items > 0) player.sendMessage(ChatColor.RED + "Your tributes were insufficient for " + charDeity + "'s blessings.");
			}
			
			// Clear the tribute case
			event.getInventory().clear();
		}
		catch(Exception e)
		{
			// Print error for debugging
			e.printStackTrace();
		}
	}
	
	/* --------------------------------------------
	 *  Handle Miscellaneous Divine Block Events
	 * --------------------------------------------
	 */	
	@EventHandler(priority = EventPriority.HIGH)
	public void demigodsAdminWand(PlayerInteractEvent event)
	{
		if(event.getClickedBlock() == null) return;
		
		// Define variables
		Block clickedBlock = event.getClickedBlock();
		Location location = clickedBlock.getLocation();
		Player player = event.getPlayer();

		// Return if the player does not qualify for use of the admin wand
		if(!DMiscUtil.hasPermissionOrOP(player, "demigods.admin") || !DDataUtil.hasPlayerData(player, "temp_admin_wand") || DDataUtil.getPlayerData(player, "temp_admin_wand").equals(false) || player.getItemInHand().getTypeId() != DConfigUtil.getSettingInt("admin_wand_tool")) return;
		
		if(clickedBlock.getType().equals(Material.EMERALD_BLOCK))
		{
			player.sendMessage(ChatColor.GRAY + "Generating new Altar...");			
			AltarCreateEvent altarEvent = new AltarCreateEvent(location.add(0, 2, 0), AltarCreateCause.ADMIN_WAND);
			DMiscUtil.getPlugin().getServer().getPluginManager().callEvent(altarEvent);
			
			player.sendMessage(ChatColor.GREEN + "Altar created!");
		}
		
		if(DDivineBlocks.isAltarBlock(location) && DDivineBlocks.isDivineBlock(location))
		{
			if(DDataUtil.hasPlayerData(player, "temp_destroy_altar") && System.currentTimeMillis() < DObjUtil.toLong(DDataUtil.getPlayerData(player, "temp_destroy_altar")))
			{
				// We can destroy the Shrine
				DDivineBlocks.removeAltar(location);
				
				// Save Divine Blocks
				DDatabase.saveDivineBlocks();
				player.sendMessage(ChatColor.GREEN + "Altar removed!");
				return;
			}
			else
			{
				DDataUtil.savePlayerData(player, "temp_destroy_altar", System.currentTimeMillis() + 5000);
				player.sendMessage(ChatColor.RED + "Right-click this Altar again to remove it.");
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void divineBlockAlerts(PlayerMoveEvent event)
	{
		if(event.getFrom().distance(event.getTo()) < 0.1) return;
		
		// Define variables
		Player player = event.getPlayer();
		Location to = event.getTo();
		Location from = event.getFrom();
		DivineBlock divineBlock = null;
		OfflinePlayer charOwner = null;
		
		/* ------------------------------------
		 * Altar Zone Messages
		 * -----------------------------------
		 * -> Entering Altar
		 */
		if(DZoneUtil.enterZoneAltar(to, from))
		{
			player.sendMessage(ChatColor.GRAY + "You have entered an Altar.");
			return;
		}
		
		// Leaving Altar
		else if(DZoneUtil.exitZoneAltar(to, from))
		{
			player.sendMessage(ChatColor.GRAY + "You have left an Altar.");
			return;
		}
		
		/* ------------------------------------
		 * Shrine Zone Messages
		 * -----------------------------------
		 * -> Entering Shrine
		 */
		if(DZoneUtil.enterZoneShrine(to, from) && DZoneUtil.zoneShrineOwner(to) != -1)
		{
			divineBlock = DZoneUtil.zoneShrine(to);
			charOwner = DCharUtil.getOwner(DZoneUtil.zoneShrineOwner(to));
			player.sendMessage(ChatColor.GRAY + "You have entered " + charOwner.getName() + "'s shrine to " + ChatColor.YELLOW + DDivineBlocks.getShrineDeity(divineBlock.getLocation()) + ChatColor.GRAY + ".");
			return;
		}
		
		// Leaving Shrine
		else if(DZoneUtil.exitZoneShrine(to, from))
		{
			player.sendMessage(ChatColor.GRAY + "You have left a holy area.");
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void stopDestroyEnderCrystal(EntityDamageEvent event)
	{
		try
		{
			for(Location divineBlock : DDivineBlocks.getAllDivineBlocks())
			{
				if(event.getEntity().getLocation().subtract(0.5, 1.0, 0.5).equals(divineBlock))
				{
					 event.setDamage(0);
					 event.setCancelled(true);
					 return;
				}
			}
		}
		catch(Exception e) {}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void stopDestroyDivineBlock(BlockBreakEvent event)
	{
		try
		{
			for(Location divineBlock : DDivineBlocks.getAllDivineBlocks())
			{
				if(event.getBlock().getLocation().equals(divineBlock))
				{
					event.getPlayer().sendMessage(ChatColor.YELLOW + "Divine blocks cannot be broken by hand.");
					event.setCancelled(true);
					return;
				}
			}
		}
		catch(Exception e) {}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void stopDivineBlockDamage(BlockDamageEvent event)
	{
		try
		{
			for(Location divineBlock : DDivineBlocks.getAllDivineBlocks())
			{
				if(event.getBlock().getLocation().equals(divineBlock))
				{
					event.setCancelled(true);
				}
			}
		}
		catch(Exception e) {}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void stopDivineBlockIgnite(BlockIgniteEvent event)
	{
		try
		{
			for(Location divineBlock : DDivineBlocks.getAllDivineBlocks())
			{
				if(event.getBlock().getLocation().equals(divineBlock))
				{
					event.setCancelled(true);
				}
			}
		}
		catch(Exception e) {}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void stopDivineBlockBurn(BlockBurnEvent event)
	{
		try
		{
			for(Location divineBlock : DDivineBlocks.getAllDivineBlocks())
			{
				if(event.getBlock().getLocation().equals(divineBlock))
				{
					event.setCancelled(true);
				}
			}
		}
		catch(Exception e) {}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void stopDivineBlockPistonExtend(BlockPistonExtendEvent event)
	{		
		List<Block> blocks = event.getBlocks();
		
		CHECKBLOCKS:
		for(Block block : blocks)
		{
			try
			{
				for(Location divineBlock : DDivineBlocks.getAllDivineBlocks())
				{
					if(block.getLocation().equals(divineBlock))
					{
						event.setCancelled(true);
						break CHECKBLOCKS;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void stopDivineBlockPistonRetract(BlockPistonRetractEvent event)
	{
		// Define variables
		final Block block = event.getBlock().getRelative(event.getDirection(), 2);
		
		try
		{
			for(Location divineBlock : DDivineBlocks.getAllDivineBlocks())
			{
				if(block.getLocation().equals((divineBlock)) && event.isSticky())
				{
					event.setCancelled(true);
				}
			}
		}
		catch(Exception e) {}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void divineBlockExplode(final EntityExplodeEvent event)
	{
		// Remove divineBlock blocks from explosions
		final ArrayList<Block> savedBlocks = new ArrayList<Block>();
		final ArrayList<Material> savedMaterials = new ArrayList<Material>();
		final ArrayList<Byte> savedBytes = new ArrayList<Byte>();
		
		List<Block> blocks = event.blockList();
		for(Block block : blocks)
		{
			if(block.getType() == Material.TNT) continue;
			if(DZoneUtil.zoneNoPVP(block.getLocation()))
			{
				savedBlocks.add(block);
				savedMaterials.add(block.getType());
				savedBytes.add(block.getData());
				continue;
			}
			for(Location divineBlock : DDivineBlocks.getAllDivineBlocks())
			{
				if(block.getLocation().equals(divineBlock))
				{
					savedBlocks.add(block);
					savedMaterials.add(block.getType());
					savedBytes.add(block.getData());
					break;
				}
			}
		}
		
		DMiscUtil.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(DMiscUtil.getPlugin(), new Runnable()
		{
			@Override
			public void run()
			{
				// Regenerate blocks
				int i = 0;
				for(Block block : savedBlocks)
				{
						block.setTypeIdAndData(savedMaterials.get(i).getId(), savedBytes.get(i), true);
						i++;
				}
				
				// Remove all drops from explosion zone
				for(Item drop : event.getLocation().getWorld().getEntitiesByClass(Item.class))
				{
				    Location location = drop.getLocation();
				    if(DZoneUtil.zoneAltar(location) != null)
					{
						drop.remove();
						continue;
					}
					
					if(DZoneUtil.zoneShrine(location) != null)
					{
						drop.remove();
						continue;
					}
				}
			}
		}, 1);
	}
}
