package com.censoredsoftware.Demigods.Engine.PlayerCharacter;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.censoredsoftware.Demigods.Engine.Ability.AbilityFactory;
import com.censoredsoftware.Demigods.Engine.Ability.Devotion;
import com.censoredsoftware.Demigods.Engine.Deity.Deity;
import com.censoredsoftware.Demigods.Engine.Demigods;

public class PlayerCharacterFactory
{
	// TODO: Make a createCharacter method that can set the meta values on creation as it was before. The way it was no longer worked as expected because I fixed the PlayerCharacterMeta issues so favor and everything actually works and I was too lazy to clean this all up so there are a ton of extra variables and all and whoa this is a really long TODO so I'm just going to keep going for a bit. Nice day, isn't it? I wouldn't know. I'm just stuck in here programming like some sort of animal. Damn. That's what I am. An animal. WHAT HAVE YOU DONE TO ME WORLD?
	public static PlayerCharacter createCharacter(final OfflinePlayer player, final String charName, final Deity deity, final boolean immortal)
	{
		PlayerCharacter character = new PlayerCharacter();
		character.setPlayer(player);
		character.setName(charName);
		character.setDeity(deity);
		character.setImmortal(immortal);
		character.setHealth(20);
		character.setHunger(20);
		character.setExperience(0);
		character.setLevel(0);
		character.setKills(0);
		character.setDeaths(0);
		character.setLocation(player.getPlayer().getLocation());
		character.setMeta(createCharacterMeta());
		PlayerCharacter.save(character);
		return character;
	}

	public static PlayerCharacter createCharacter(OfflinePlayer player, String charName, String charDeity)
	{
		if(PlayerCharacter.getCharByName(charName) == null)
		{
			// Create the Character
			return createCharacter(player, charName, Deity.getDeity(charDeity), true);
		}
		return null;
	}

	public static PlayerCharacterMeta createCharacterMeta()
	{
		PlayerCharacterMeta charMeta = new PlayerCharacterMeta();
		charMeta.initializeMaps();
		charMeta.setAscensions(Demigods.config.getSettingInt("character.defaults.ascensions"));
		charMeta.setFavor(Demigods.config.getSettingInt("character.defaults.favor"));
		charMeta.setMaxFavor(Demigods.config.getSettingInt("character.defaults.max_favor"));
		charMeta.addDevotion(AbilityFactory.createDevotion(Devotion.Type.OFFENSE));
		charMeta.addDevotion(AbilityFactory.createDevotion(Devotion.Type.DEFENSE));
		charMeta.addDevotion(AbilityFactory.createDevotion(Devotion.Type.PASSIVE));
		charMeta.addDevotion(AbilityFactory.createDevotion(Devotion.Type.STEALTH));
		charMeta.addDevotion(AbilityFactory.createDevotion(Devotion.Type.SUPPORT));
		charMeta.addDevotion(AbilityFactory.createDevotion(Devotion.Type.ULTIMATE));
		PlayerCharacterMeta.save(charMeta);
		return charMeta;
	}

	public static PlayerCharacterInventory createPlayerCharacterInventory(PlayerCharacter character)
	{
		PlayerInventory inventory = character.getOfflinePlayer().getPlayer().getInventory();
		PlayerCharacterInventory charInventory = new PlayerCharacterInventory();
		charInventory.setOwner(character.getId());
		if(inventory.getHelmet() != null) charInventory.setHelmet(inventory.getHelmet());
		if(inventory.getChestplate() != null) charInventory.setChestplate(inventory.getChestplate());
		if(inventory.getLeggings() != null) charInventory.setLeggings(inventory.getLeggings());
		if(inventory.getBoots() != null) charInventory.setBoots(inventory.getBoots());
		charInventory.setItems(inventory);
		PlayerCharacterInventory.save(charInventory);
		return charInventory;
	}

	public static PlayerCharacterInventory createEmptyCharacterInventory()
	{
		PlayerCharacterInventory charInventory = new PlayerCharacterInventory();
		charInventory.setHelmet(new ItemStack(Material.AIR));
		charInventory.setChestplate(new ItemStack(Material.AIR));
		charInventory.setLeggings(new ItemStack(Material.AIR));
		charInventory.setBoots(new ItemStack(Material.AIR));
		PlayerCharacterInventory.save(charInventory);
		return charInventory;
	}
}