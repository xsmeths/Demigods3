package com.censoredsoftware.demigods.greek.item.armor;

import com.censoredsoftware.censoredlib.util.Items;
import com.censoredsoftware.demigods.engine.item.DivineItem;
import com.censoredsoftware.demigods.engine.util.Zones;
import com.google.common.collect.DiscreteDomains;
import com.google.common.collect.Ranges;
import com.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.Set;

public class BootsOfPagos
{
	public final static String name = "Boots of Pagos";
	public final static String description = "Boots this cold have their advantages... and disadvantages.";
	public final static DivineItem.Category category = DivineItem.Category.ARMOR;
	public final static ItemStack item = Items.create(Material.IRON_BOOTS, ChatColor.AQUA + name, new ArrayList<String>()
	{
		{
			add(ChatColor.BLUE + "" + ChatColor.ITALIC + description);
		}
	}, null);
	public final static Recipe recipe = new ShapedRecipe(item)
	{
		{
			shape("AAA", "ABA", "AAA");
			setIngredient('A', Material.PACKED_ICE);
			setIngredient('B', Material.IRON_BOOTS);
		}
	};
	public final static Listener listener = new Listener()
	{
		@EventHandler(priority = EventPriority.NORMAL)
		private void onPlayerMove(PlayerMoveEvent event)
		{
			if(Zones.inNoDemigodsZone(event.getPlayer().getLocation())) return;

			// Define variables
			Player player = event.getPlayer();

			if(player.getInventory().getBoots() != null && Items.areEqualIgnoreEnchantments(item, player.getInventory().getBoots()))
			{
				Location location = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();

				if(location.getBlock().getType().equals(Material.WATER) || location.getBlock().getType().equals(Material.STATIONARY_WATER) && surroundingSquareIsAir(player.getLocation()))
				{
					sendIce(player, getSquare(location));

					for(Entity entity : player.getNearbyEntities(30, 30, 30))
						if(entity instanceof Player) sendIce((Player) entity, getSquare(entity.getLocation().add(0, -1, 0)));
				}
				else if(!location.getBlock().isLiquid() && location.getBlock().getType().isSolid() && location.getBlock().getType() != Material.ICE && location.getBlock().getType() != Material.PACKED_ICE && location.getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR))
				{
					player.sendBlockChange(location.clone().add(0, 1, 0), Material.SNOW, (byte) 0);

					for(Entity entity : player.getNearbyEntities(30, 30, 30))
						if(entity instanceof Player) ((Player) entity).sendBlockChange(location.clone().add(0, 1, 0), Material.SNOW, (byte) 0);
				}
			}
		}
	};

	public static boolean surroundingSquareIsAir(Location center)
	{
		for(Location location : getSquare(center))
			if(!location.equals(center) && !location.getBlock().getType().equals(Material.AIR)) return false;
		return true;
	}

	public static Set<Location> getSquare(Location center)
	{
		Set<Location> set = Sets.newHashSet();
		Set<Integer> range = Ranges.closed(-1, 1).asSet(DiscreteDomains.integers());
		for(int x : range)
			for(int z : range)
				set.add(center.clone().add(x, 0, z));
		return set;
	}

	public static void sendIce(Player player, Set<Location> locations)
	{
		for(Location location : locations)
			if(location.getBlock().isLiquid()) player.sendBlockChange(location, Material.ICE, (byte) 0);
	}
}