package com.censoredsoftware.demigods.greek.ability.offense;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import com.censoredsoftware.demigods.engine.data.serializable.Battle;
import com.censoredsoftware.demigods.engine.data.serializable.DCharacter;
import com.censoredsoftware.demigods.engine.data.serializable.DPlayer;
import com.censoredsoftware.demigods.engine.data.serializable.Skill;
import com.censoredsoftware.demigods.engine.mythos.Ability;
import com.censoredsoftware.demigods.greek.ability.GreekAbility;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

public class Lightning extends GreekAbility
{
	private static final String NAME = "Lighting", COMMAND = "lightning";
	private static final int COST = 140, DELAY = 2, REPEAT = 0;
	private static final List<String> DETAILS = Lists.newArrayList("Strike lightning upon your enemies.");
	private static final Skill.Type TYPE = Skill.Type.OFFENSE;

	public Lightning(String deity)
	{
		super(NAME, COMMAND, deity, COST, DELAY, REPEAT, DETAILS, TYPE, null, new Predicate<Player>()
		{
			@Override
			public boolean apply(Player player)
			{
				// Define variables
				Location target;
				LivingEntity entity = Ability.Util.autoTarget(player);
				boolean notify;

				if(entity != null)
				{
					target = entity.getLocation();
					notify = true;
				}
				else
				{
					target = Ability.Util.directTarget(player);
					notify = false;
				}

				strikeLightning(player, target, notify);

				return true;
			}
		}, null, null);
	}

	public static boolean strikeLightning(Player player, LivingEntity target)
	{
		return Battle.Util.canTarget(target) && strikeLightning(player, target.getLocation(), true);
	}

	public static boolean strikeLightning(Player player, Location target, boolean notify)
	{
		// Set variables
		DCharacter character = DPlayer.Util.getPlayer(player).getCurrent();

		if(!player.getWorld().equals(target.getWorld())) return false;
		Location toHit = Ability.Util.adjustedAimLocation(character, target);

		player.getWorld().strikeLightningEffect(toHit);

		for(Entity entity : toHit.getBlock().getChunk().getEntities())
		{
			if(entity instanceof LivingEntity)
			{
				if(!Battle.Util.canTarget(entity)) continue;
				LivingEntity livingEntity = (LivingEntity) entity;
				if(livingEntity.equals(player)) continue;
				if((toHit.getBlock().getType().equals(Material.WATER) || toHit.getBlock().getType().equals(Material.STATIONARY_WATER)) && livingEntity.getLocation().distance(toHit) < 8) Ability.Util.dealDamage(player, livingEntity, character.getMeta().getAscensions() * 6, EntityDamageEvent.DamageCause.LIGHTNING);
				else if(livingEntity.getLocation().distance(toHit) < 2) Ability.Util.dealDamage(player, livingEntity, character.getMeta().getAscensions() * 4, EntityDamageEvent.DamageCause.LIGHTNING);
			}
		}

		if(!Ability.Util.isHit(target, toHit) && notify) player.sendMessage(ChatColor.RED + "Missed...");

		return true;
	}
}