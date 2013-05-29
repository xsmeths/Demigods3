package com.censoredsoftware.Demigods.Demo.Data.Quest.Passive;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.censoredsoftware.Demigods.API.AdminAPI;
import com.censoredsoftware.Demigods.API.BlockAPI;
import com.censoredsoftware.Demigods.API.PlayerAPI;
import com.censoredsoftware.Demigods.API.ValueAPI;
import com.censoredsoftware.Demigods.Engine.Block.BlockFactory;
import com.censoredsoftware.Demigods.Engine.Block.Shrine;
import com.censoredsoftware.Demigods.Engine.Deity.Deity;
import com.censoredsoftware.Demigods.Engine.Demigods;
import com.censoredsoftware.Demigods.Engine.DemigodsData;
import com.censoredsoftware.Demigods.Engine.Event.Shrine.ShrineCreateEvent;
import com.censoredsoftware.Demigods.Engine.PlayerCharacter.PlayerCharacter;
import com.censoredsoftware.Demigods.Engine.Quest.Quest;
import com.censoredsoftware.Demigods.Engine.Quest.Task;
import com.censoredsoftware.Demigods.Engine.Quest.TaskInfo;
import com.censoredsoftware.Demigods.Engine.Tracked.TrackedPlayer;

public class ShrineQuest extends Quest
{
	private static String name = "Shrine", permission = "demigods.shrine";
	private static Type type = Type.PASSIVE;

	private static List<String> about = new ArrayList<String>()
	{
		{
			add("Shrines are an integral part of Demigods.");
			add("Right-click a gold block with a book to create one.");
			add("With your Shrine you can tribute to your Deity!");
		}
	}, accepted = new ArrayList<String>()
	{
		{
			add("Accepted.");
		}
	}, complete = new ArrayList<String>()
	{
		{
			add("Complete.");
		}
	}, failed = new ArrayList<String>()
	{
		{
			add("Failed.");
		}
	};

	private static List<Task> tasks = new ArrayList<Task>()
	{
		{
			add(new Tribute(name, permission, about, accepted, complete, failed, type));
		}
	};

	public ShrineQuest()
	{
		super(name, permission, about, accepted, complete, failed, type, tasks);
	}
}

class Tribute extends Task
{
	// Define required task variables
	private static String name = "Tributing";
	private static int order = 0;
	private static double reward = 0.0, penalty = 0.0;

	private static Listener listener = new Listener()
	{
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onShrineInteract(PlayerInteractEvent event)
		{
			// Return if the player is mortal
			if(!PlayerAPI.isImmortal(event.getPlayer())) return;
			if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

			// Define variables
			Location location = event.getClickedBlock().getLocation();
			Player player = event.getPlayer();
			PlayerCharacter character = TrackedPlayer.getTracked(player).getCurrent();
			String charAlliance = character.getAlliance();
			Deity charDeity = character.getDeity();

			if(event.getClickedBlock().getType().equals(Material.GOLD_BLOCK) && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getType() == Material.BOOK)
			{
				try
				{
					// Shrine created!
					ShrineCreateEvent shrineCreateEvent = new ShrineCreateEvent(character, location);
					Bukkit.getServer().getPluginManager().callEvent(shrineCreateEvent);
					if(shrineCreateEvent.isCancelled()) return;

					BlockFactory.createShrine(character, location);
					location.getWorld().strikeLightningEffect(location);

					if(!player.getGameMode().equals(GameMode.CREATIVE))
					{
						if(player.getItemInHand().getAmount() > 1)
						{
							ItemStack books = new ItemStack(player.getItemInHand().getType(), player.getInventory().getItemInHand().getAmount() - 1);
							player.setItemInHand(books);
						}
						else
						{
							player.getInventory().remove(Material.BOOK);
						}
					}

					player.sendMessage(ChatColor.GRAY + "The " + ChatColor.YELLOW + charAlliance + "s" + ChatColor.GRAY + " are pleased...");
					player.sendMessage(ChatColor.GRAY + "You have created a Shrine in the name of " + ChatColor.YELLOW + charDeity + ChatColor.GRAY + "!");
				}
				catch(Exception e)
				{
					// Creation of shrine failed...
					e.printStackTrace();
				}
			}
			else
			{
				// Return if this isn't a shrine
				if(!BlockAPI.isShrine(location)) return;

				// Define the shrine
				Shrine shrine = BlockAPI.getShrine(location);

				// TODO: This should probably be somewhere else entirely.
				// Handle admin wand
				if(AdminAPI.useWand(player)) // TODO TimedObject data.
				{
					// if(API.data.hasTimedData(player, "temp_destroy_shrine"))
					// {
					// Shrine shrine = BlockAPI.getShrine(location);
					//
					// ShrineRemoveEvent shrineRemoveEvent = new ShrineRemoveEvent(shrine.getOfflinePlayer(), location);
					// Bukkit.getServer().getPluginManager().callEvent(shrineRemoveEvent);
					// if(shrineRemoveEvent.isCancelled()) return;
					//
					// // We can destroy the Shrine
					// BlockAPI.getShrine(location).remove();
					// DemigodsData.tempPlayerData.removeData(player, "temp_destroy_shrine");
					//
					// // Drop the block of gold and book
					// location.getWorld().dropItemNaturally(location, new ItemStack(Material.GOLD_BLOCK, 1));
					// location.getWorld().dropItemNaturally(location, new ItemStack(Material.BOOK, 1));
					//
					// player.sendMessage(ChatColor.GREEN + "Shrine removed!");
					// return;
					// }
					// else
					// {
					// API.data.saveTimedData(player, "temp_destroy_shrine", true, 5);
					// player.sendMessage(ChatColor.RED + "Right-click this Shrine again to remove it.");
					// return;
					// }
				}

				// Return if the player is mortal
				if(character == null || !character.isImmortal())
				{
					player.sendMessage(ChatColor.RED + "You must be immortal to use that!");
					return;
				}

				if(character.getDeity().equals(shrine.getDeity()))
				{
					useShrine(character, shrine);
				}
				else
				{
					player.sendMessage(ChatColor.YELLOW + "You must be allied to " + shrine.getDeity().getInfo().getName() + " in order to tribute here.");
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPlayerTribute(InventoryCloseEvent event)
		{
			// Return if it's not a player
			if(!(event.getPlayer() instanceof Player)) return;

			// Define player and character
			Player player = (Player) event.getPlayer();
			PlayerCharacter character = TrackedPlayer.getTracked(player).getCurrent();

			// Make sure they have a character and are immortal
			if(character == null || !character.isImmortal()) return;

			// If it isn't a tribute chest then break the method
			if(!event.getInventory().getName().contains("Shrine") || !BlockAPI.isShrine(player.getTargetBlock(null, 10).getLocation())) return;

			// Get the creator of the shrine
			PlayerCharacter shrineOwner = PlayerCharacter.getCharacterByName(DemigodsData.getValueTemp(player.getName(), character.getName()).toString());
			DemigodsData.removeTemp(player.getName(), character.getName());

			// Calculate value of chest
			int tributeValue = 0, items = 0;
			for(ItemStack ii : event.getInventory().getContents())
			{
				if(ii != null)
				{
					tributeValue += ValueAPI.getTributeValue(ii);
					items++;
				}
			}

			tributeValue *= Demigods.config.getSettingDouble("multipliers.favor");

			// Process tributes and send messages
			int favorBefore = character.getMeta().getMaxFavor();
			int devotionBefore = character.getMeta().getDevotion();

			// Update the character's favor and devotion
			character.getMeta().addMaxFavor(tributeValue / 5);
			character.getMeta().addDevotion(tributeValue);

			if(character.getMeta().getDevotion() > devotionBefore) player.sendMessage(ChatColor.GRAY + "Your devotion to " + ChatColor.YELLOW + character.getDeity().getInfo().getName() + ChatColor.GRAY + " has increased to " + ChatColor.GREEN + character.getMeta().getDevotion() + ChatColor.GRAY + ".");
			if(character.getMeta().getMaxFavor() > favorBefore) player.sendMessage(ChatColor.GRAY + "Your favor cap has increased to " + ChatColor.GREEN + character.getMeta().getMaxFavor() + ChatColor.GRAY + ".");

			if(favorBefore != character.getMeta().getMaxFavor() && devotionBefore != character.getMeta().getDevotion() && items > 0)
			{
				// Update the shrine owner's devotion and let them know
				OfflinePlayer shrineOwnerPlayer = shrineOwner.getOfflinePlayer();
				if(!character.equals(shrineOwnerPlayer))
				{
					// TODO: DCharUtil.addDevotion(shrineOwner, tributeValue / 7);
					if(shrineOwnerPlayer.isOnline())
					{
						((Player) shrineOwnerPlayer).sendMessage(ChatColor.YELLOW + "Someone just tributed at your shrine!");
						((Player) shrineOwnerPlayer).sendMessage(ChatColor.GRAY + "Your devotion has increased to " + shrineOwner.getMeta().getDevotion() + "!");
					}
				}
			}
			else
			{
				// If they aren't good enough let them know
				if(items > 0) player.sendMessage(ChatColor.RED + "Your tributes were insufficient for " + character.getDeity().getInfo().getName() + "'s blessings.");
			}

			// Clear the tribute case
			event.getInventory().clear();
		}
	};

	private static void useShrine(PlayerCharacter character, Shrine shrine)
	{
		Player player = character.getOfflinePlayer().getPlayer();
		String shrineOwner = shrine.getOwner().getName();
		Deity shrineDeity = shrine.getDeity();

		// Open the tribute inventory
		Inventory ii = Bukkit.getServer().createInventory(player, 27, "Shrine of " + shrineDeity);
		player.openInventory(ii);
		DemigodsData.saveTemp(player.getName(), character.getName(), shrineOwner);
	}

	public Tribute(String quest, String permission, List<String> about, List<String> accepted, List<String> complete, List<String> failed, Quest.Type type)
	{
		super(new TaskInfo(name, quest, permission, order, reward, penalty, about, accepted, complete, failed, type, TaskInfo.Subtype.TECHNICAL), listener);
	}
}
