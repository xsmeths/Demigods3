package com.censoredsoftware.Demigods.Engine.Object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.censoredsoftware.Demigods.Engine.Demigods;

public abstract class DCommand implements TabExecutor
{
	public abstract List<String> getCommands();

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, final String[] args)
	{
		return new ArrayList<String>()
		{
			{
				for(Player online : Bukkit.getOnlinePlayers())
				{
					DPlayer wrapper = DPlayer.Util.getPlayer(online);
					if(wrapper.canUseCurrent() && wrapper.getCurrent() != null && wrapper.getCurrent().getName().toLowerCase().startsWith(args[0].toLowerCase())) add(wrapper.getCurrent().getName());
					else if(online.getName().toLowerCase().startsWith(args[0].toLowerCase())) add(online.getName());
				}
			}
		};
	}

	public static class Util
	{
		public static void registerCommand(DCommand dgCommand)
		{
			for(String command : dgCommand.getCommands())
			{
				Demigods.plugin.getCommand(command).setExecutor(dgCommand);
				Demigods.plugin.getCommand(command).setTabCompleter(dgCommand);
			}
		}
	}
}