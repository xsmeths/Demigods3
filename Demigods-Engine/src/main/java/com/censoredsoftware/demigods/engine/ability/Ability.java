package com.censoredsoftware.demigods.engine.ability;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public interface Ability
{
	public String getDeity();

	public String getName();

	public String getCommand();

	public String getPermission();

	public int getCost();

	public int getDelay();

	public int getRepeat();

	public List<String> getDetails();

	public Material getWeapon();

	public boolean hasWeapon();

	public Listener getListener();

	public BukkitRunnable getRunnable();
}
