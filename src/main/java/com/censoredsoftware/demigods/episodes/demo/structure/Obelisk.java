package com.censoredsoftware.demigods.episodes.demo.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.censoredsoftware.demigods.engine.Demigods;
import com.censoredsoftware.demigods.engine.data.DataManager;
import com.censoredsoftware.demigods.engine.element.Structure;
import com.censoredsoftware.demigods.engine.language.TranslationManager;
import com.censoredsoftware.demigods.engine.player.DCharacter;
import com.censoredsoftware.demigods.engine.player.DPlayer;
import com.censoredsoftware.demigods.engine.util.Admins;
import com.censoredsoftware.demigods.episodes.demo.EpisodeDemo;

public class Obelisk extends Structure
{
	private final static List<BlockData> specialStoneBrick = new ArrayList<BlockData>(1)
	{
		{
			add(new BlockData(Material.SMOOTH_BRICK, (byte) 3));
		}
	};
	private final static List<BlockData> specialSandstone = new ArrayList<BlockData>(1)
	{
		{
			add(new BlockData(Material.SANDSTONE, (byte) 1));
		}
	};
	private final static List<BlockData> sandstone = new ArrayList<BlockData>(1)
	{
		{
			add(new BlockData(Material.SANDSTONE));
		}
	};
	private final static List<BlockData> stoneBrick = new ArrayList<BlockData>(2)
	{
		{
			add(new BlockData(Material.SMOOTH_BRICK, 9));
			add(new BlockData(Material.SMOOTH_BRICK, (byte) 2, 1));
		}
	};
	private final static List<BlockData> redstoneBlock = new ArrayList<BlockData>(1)
	{
		{
			add(new BlockData(Material.REDSTONE_BLOCK));
		}
	};
	private final static List<BlockData> redstoneLamp = new ArrayList<BlockData>(1)
	{
		{
			add(new BlockData(Material.REDSTONE_LAMP_ON));
		}
	};
	private final static List<BlockData> vine1 = new ArrayList<BlockData>(2)
	{
		{
			add(new BlockData(Material.VINE, (byte) 1, 4));
			add(new BlockData(Material.AIR, 6));
		}
	};
	private final static List<BlockData> vine4 = new ArrayList<BlockData>(2)
	{
		{
			add(new BlockData(Material.VINE, (byte) 4, 4));
			add(new BlockData(Material.AIR, 6));
		}
	};
	private final static Schematic general = new Schematic("general", "HmmmQuestionMark", 3)
	{
		{
			// Clickable block.
			add(new Cuboid(0, 0, 2, specialStoneBrick));

			// Everything else.
			add(new Cuboid(0, 0, -1, 0, 2, -1, stoneBrick));
			add(new Cuboid(0, 0, 1, 0, 2, 1, stoneBrick));
			add(new Cuboid(1, 0, 0, 1, 2, 0, stoneBrick));
			add(new Cuboid(-1, 0, 0, -1, 2, 0, stoneBrick));
			add(new Cuboid(0, 4, -1, 0, 5, -1, stoneBrick));
			add(new Cuboid(0, 4, 1, 0, 5, 1, stoneBrick));
			add(new Cuboid(1, 4, 0, 1, 5, 0, stoneBrick));
			add(new Cuboid(-1, 4, 0, -1, 5, 0, stoneBrick));
			add(new Cuboid(0, 3, 0, redstoneBlock));
			add(new Cuboid(0, 4, 0, redstoneBlock));
			add(new Cuboid(0, 3, -1, redstoneLamp));
			add(new Cuboid(0, 3, 1, redstoneLamp));
			add(new Cuboid(1, 3, 0, redstoneLamp));
			add(new Cuboid(-1, 3, 0, redstoneLamp));
			add(new Cuboid(0, 5, 0, redstoneLamp));
			add(new Cuboid(1, 5, -1, vine1));
			add(new Cuboid(-1, 5, -1, vine1));
			add(new Cuboid(1, 5, 1, vine4));
			add(new Cuboid(-1, 5, 1, vine4));
		}
	};
	private final static Schematic desert = new Schematic("desert", "HmmmQuestionMark", 3)
	{
		{
			// Clickable block.
			add(new Cuboid(0, 0, 2, specialSandstone));

			// Everything else.
			add(new Cuboid(0, 0, -1, 0, 2, -1, sandstone));
			add(new Cuboid(0, 0, 1, 0, 2, 1, sandstone));
			add(new Cuboid(1, 0, 0, 1, 2, 0, sandstone));
			add(new Cuboid(-1, 0, 0, -1, 2, 0, sandstone));
			add(new Cuboid(0, 4, -1, 0, 5, -1, sandstone));
			add(new Cuboid(0, 4, 1, 0, 5, 1, sandstone));
			add(new Cuboid(1, 4, 0, 1, 5, 0, sandstone));
			add(new Cuboid(-1, 4, 0, -1, 5, 0, sandstone));
			add(new Cuboid(0, 3, 0, redstoneBlock));
			add(new Cuboid(0, 4, 0, redstoneBlock));
			add(new Cuboid(0, 3, -1, redstoneLamp));
			add(new Cuboid(0, 3, 1, redstoneLamp));
			add(new Cuboid(1, 3, 0, redstoneLamp));
			add(new Cuboid(-1, 3, 0, redstoneLamp));
			add(new Cuboid(0, 5, 0, redstoneLamp));
		}
	};

	public static enum ObeliskDesign implements Design
	{
		GENERAL("general", general), DESERT("desert", desert);

		private final String name;
		private final Schematic schematic;

		private ObeliskDesign(String name, Schematic schematic)
		{
			this.name = name;
			this.schematic = schematic;
		}

		@Override
		public String getName()
		{
			return name;
		}

		@Override
		public Schematic getSchematic()
		{
			return schematic;
		}
	}

	@Override
	public Set<Flag> getFlags()
	{
		return new HashSet<Flag>()
		{
			{
				add(Flag.PROTECTED_BLOCKS);
				add(Flag.NO_GRIEFING);
			}
		};
	}

	@Override
	public String getStructureType()
	{
		return "Obelisk";
	}

	@Override
	public Schematic get(String name)
	{
		if(name.equals(general.toString())) return general;
		return desert;
	}

	@Override
	public int getRadius()
	{
		return Demigods.config.getSettingInt("zones.obelisk_radius");
	}

	@Override
	public Location getClickableBlock(Location reference)
	{
		return reference.clone().add(0, 0, 2);
	}

	@Override
	public Listener getUniqueListener()
	{
		return new ObeliskListener();
	}

	@Override
	public Set<Save> getAll()
	{
		return Util.findAll("type", getStructureType());
	}

	@Override
	public Save createNew(Location reference, boolean generate)
	{
		Save save = new Save();
		save.setReferenceLocation(reference);
		save.setType(getStructureType());
		save.setDesign(getDesign(reference).getName());
		save.addFlags(getFlags());
		save.save();
		if(generate) save.generate();
		return save;
	}

	public Design getDesign(Location reference)
	{
		switch(reference.getBlock().getBiome())
		{
			case BEACH:
			case DESERT:
			case DESERT_HILLS:
				return ObeliskDesign.DESERT;
			default:
				return ObeliskDesign.GENERAL;
		}
	}

	public static boolean validBlockConfiguration(Block block)
	{
		if(!block.getType().equals(Material.REDSTONE_BLOCK)) return false;
		if(!block.getRelative(1, 0, 0).getType().equals(Material.COBBLESTONE)) return false;
		if(!block.getRelative(-1, 0, 0).getType().equals(Material.COBBLESTONE)) return false;
		if(!block.getRelative(0, 0, 1).getType().equals(Material.COBBLESTONE)) return false;
		if(!block.getRelative(0, 0, -1).getType().equals(Material.COBBLESTONE)) return false;
		if(block.getRelative(1, 0, 1).getType().isSolid()) return false;
		return !block.getRelative(1, 0, -1).getType().isSolid() && !block.getRelative(-1, 0, 1).getType().isSolid() && !block.getRelative(-1, 0, -1).getType().isSolid();
	}

	public static boolean noPvPStructureNearby(Location location)
	{
		for(Save structureSave : Util.loadAll())
		{
			if(structureSave.getStructure().getFlags().contains(Flag.NO_PVP) && structureSave.getReferenceLocation().distance(location) <= (Demigods.config.getSettingInt("altar_radius") + Demigods.config.getSettingInt("obelisk_radius") + 6)) return true;
		}
		return false;
	}
}

class ObeliskListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGH)
	public void createAndRemove(PlayerInteractEvent event)
	{
		if(event.getClickedBlock() == null) return;

		// Define variables
		Block clickedBlock = event.getClickedBlock();
		Location location = clickedBlock.getLocation();
		Player player = event.getPlayer();

		if(DPlayer.Util.isImmortal(player))
		{
			DCharacter character = DPlayer.Util.getPlayer(player).getCurrent();

			if(event.getAction() == Action.RIGHT_CLICK_BLOCK && character.getDeity().getInfo().getClaimItems().contains(event.getPlayer().getItemInHand().getType()) && Obelisk.validBlockConfiguration(event.getClickedBlock()))
			{
				if(Obelisk.noPvPStructureNearby(location))
				{
					player.sendMessage(ChatColor.YELLOW + "This location is too close to a no-pvp zone, please try again.");
					return;
				}

				try
				{
					// Obelisk created!
					Admins.sendDebug(ChatColor.RED + "Obelisk created by " + character.getName() + " at: " + ChatColor.GRAY + "(" + location.getWorld().getName() + ") " + location.getX() + ", " + location.getY() + ", " + location.getZ());
					Structure.Save save = EpisodeDemo.Structures.OBELISK.getStructure().createNew(location, true);
					save.setOwner(character);
					location.getWorld().strikeLightningEffect(location);

					player.sendMessage(ChatColor.GRAY + Demigods.text.getText(TranslationManager.Text.CREATE_OBELISK));
				}
				catch(Exception e)
				{
					// Creation of shrine failed...
					e.printStackTrace();
				}
			}
		}

		if(Admins.useWand(player) && Structure.Util.partOfStructureWithType(location, "Obelisk", true))
		{
			event.setCancelled(true);

			Structure.Save save = Structure.Util.getStructureSave(location, true);
			// DCharacter owner = save.getOwner();

			if(DataManager.hasTimed(player.getName(), "destroy_obelisk"))
			{
				// Remove the Obelisk
				save.remove();
				DataManager.removeTimed(player.getName(), "destroy_obelisk");

				// Admins.sendDebug(ChatColor.RED + "Obelisk owned by (" + owner.getName() + ") at: " + ChatColor.GRAY + "(" + location.getWorld().getName() + ") " + location.getX() + ", " + location.getY() + ", " + location.getZ() + " removed.");

				player.sendMessage(ChatColor.GREEN + Demigods.text.getText(TranslationManager.Text.ADMIN_WAND_REMOVE_SHRINE_COMPLETE));
			}
			else
			{
				DataManager.saveTimed(player.getName(), "destroy_obelisk", true, 5);
				player.sendMessage(ChatColor.RED + Demigods.text.getText(TranslationManager.Text.ADMIN_WAND_REMOVE_SHRINE));
			}
		}
	}
}