package com.legit2.Demigods;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.legit2.Demigods.Libraries.ReflectCommand;

public class DCommandExecutor
{
	static Demigods plugin;
	
	public DCommandExecutor(Demigods instance)
	{
		plugin = instance;
	}
	
	/*
	 *  Command: "dg"
	 */
	@ReflectCommand.Command(name = "dg", sender = ReflectCommand.Sender.EVERYONE, permission = "demigods.basic")
	public static boolean dg(CommandSender sender, String arg1)
	{
		if(arg1 != "noargs") dg_info(sender, arg1);
				
		// Define Player
		Player player = DUtil.definePlayer(sender);
		
		// Check Permissions
		if(!DUtil.hasPermissionOrOP(player, "demigods.basic")) return DUtil.noPermission(player);
		
		DUtil.taggedMessage(sender, "Information Directory");
		sender.sendMessage(ChatColor.GRAY + "/dg god");
		sender.sendMessage(ChatColor.GRAY + "/dg titan");
		sender.sendMessage(ChatColor.GRAY + "/dg claim");
		sender.sendMessage(ChatColor.GRAY + "/dg shrine");
		sender.sendMessage(ChatColor.GRAY + "/dg tribute");
		sender.sendMessage(ChatColor.GRAY + "/dg player");
		sender.sendMessage(ChatColor.GRAY + "/dg pvp");
		sender.sendMessage(ChatColor.GRAY + "/dg rankings");
		sender.sendMessage("Use " + ChatColor.YELLOW + "/check" + ChatColor.WHITE + " to see your player information.");
		return true;
	}
	
	/*
	 *  Command: "dg_info"
	 */
	public static boolean dg_info(CommandSender sender, String category)
	{
		// Define Player
		Player player = DUtil.definePlayer(sender);
		
		// Check Permissions
		if(!DUtil.hasPermissionOrOP(player, "demigods.basic")) return DUtil.noPermission(player);
		
		if(category.equalsIgnoreCase("god"))
		{
			DUtil.taggedMessage(sender, "Gods");
			sender.sendMessage(ChatColor.GRAY + " This is some info about Gods.");
		}
		else if(category.equalsIgnoreCase("titan"))
		{
			DUtil.taggedMessage(sender, "Titans");
			sender.sendMessage(ChatColor.GRAY + " This is some info about Titans.");
		}
		else if(category.equalsIgnoreCase("claim"))
		{
			DUtil.taggedMessage(sender, "Claiming");
			sender.sendMessage(ChatColor.GRAY + " This is some info about Claiming.");
		}
		else if(category.equalsIgnoreCase("shrine"))
		{
			DUtil.taggedMessage(sender, "Shrines");
			sender.sendMessage(ChatColor.GRAY + " This is some info about Shrines.");
		}
		else if(category.equalsIgnoreCase("tribute"))
		{
			DUtil.taggedMessage(sender, "Tributes");
			sender.sendMessage(ChatColor.GRAY + " This is some info about Tributes.");
		}
		else if(category.equalsIgnoreCase("player"))
		{
			DUtil.taggedMessage(sender, "Players");
			sender.sendMessage(ChatColor.GRAY + " This is some info about Players.");
		}
		else if(category.equalsIgnoreCase("pvp"))
		{
			DUtil.taggedMessage(sender, "PVP");
			sender.sendMessage(ChatColor.GRAY + " This is some info about PVP.");
		}
		else if(category.equalsIgnoreCase("stats"))
		{
			DUtil.taggedMessage(sender, "Stats");
			sender.sendMessage(ChatColor.GRAY + " These are some stats for Demigods.");
		}
		else if(category.equalsIgnoreCase("rankings"))
		{
			DUtil.taggedMessage(sender, "Rankings");
			sender.sendMessage(ChatColor.GRAY + " This is some ranking info about Demigods.");
		}
		
		return true;
	}
	
	/*
	 *  Command: "check"
	 */
	@ReflectCommand.Command(name = "check", sender = ReflectCommand.Sender.EVERYONE, usage = "/check", permission = "demigods.basic")
	public static boolean check(CommandSender sender)
	{
		// Define Player and Username
		Player player = DUtil.definePlayer(sender);
		String username = player.getName();
				
		try
		{
			ResultSet player_info = DDatabase.getPlayerInfo(username);
			
			// Send the user their info via chat
			DUtil.taggedMessage(sender, "Player check: " + ChatColor.YELLOW + username);
			//sender.sendMessage("com.legit2.Demigods.Deities: " + ChatColor.GREEN + player_info.getString("deities"));
			sender.sendMessage("Deities: " + ChatColor.DARK_GREEN + player_info.getString("deities"));
			sender.sendMessage("Favor: " + ChatColor.GREEN + player_info.getString("favor"));
			sender.sendMessage("Ascensions: " + ChatColor.GREEN + player_info.getString("ascensions"));
			sender.sendMessage(" ");
			sender.sendMessage("Kills: " + ChatColor.GREEN + player_info.getString("kills") + ChatColor.WHITE + " / Deaths: " + ChatColor.RED + player_info.getString("deaths"));
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}