/*
 * StopWhiners CraftBukkit plugin
 * 
 * Licensed under the zlib/png license
 * 
 * Copyright (c) 2011 Andor Uhl�r
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any damages arising from the use of this software.
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter it and redistribute it freely, subject to the following restrictions:
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software. If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 * 
 */

package com.andor.stopwhiners;

import java.util.HashMap;
import java.util.logging.Logger;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import com.iConomy.iConomy;
import com.iConomy.system.Account;

public class StopWhinersPlugin extends JavaPlugin {
	private final Logger logger = Logger.getLogger("Minecraft");
	private final StopWhinersEntityListener entityListener = new StopWhinersEntityListener(this);
	private iConomy iconomy;
	private boolean iconomyEnabled = false;
	
	private final HashMap<Player, List<ItemStack>> lastDrops = new HashMap<Player, List<ItemStack>>();
	
	@Override
	public void onDisable() {
		getLogger().info("StopWhiners was disabled. Goodbye cruel world.");
		
	}

	@Override
	public void onEnable() {
		getLogger().info("StopWhiners was enabled. Yayifications!.");
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Normal, this);
		iconomy = (iConomy) pm.getPlugin("iConomy");
		if (iconomy != null)
		{
			if (iconomy.isEnabled() && iconomy.getClass().getName().equals("com.iConomy.iConomy")) {
                logger.info("iConomy support enabled in StopWhiners!"); iconomyEnabled = true;
            }
		}
	}
	
	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("giveback"))
		{
			if (!sender.isOp() || !sender.hasPermission("stopwhiners.giveback"))
			{
				sender.sendMessage("nope.avi");
				return true;
			}
			
			if (args.length < 1)
				return false;
			
			for (int i = 0; i < args.length; i++) {
				if (lastDrops.containsKey(getServer().getPlayer(args[i])))
				{
					// getServer().getPlayer(args[i]).getInventory().setContents(lastDrops.get(getServer().getPlayer(args[i]))); // dongs, this should work but does not
					for (int j = 0; j < lastDrops.get(getServer().getPlayer(args[i])).size(); ++j)
						getServer().getPlayer(args[i]).getInventory().addItem(lastDrops.get(getServer().getPlayer(args[i])).get(j));
					
					logger.info("Restoring items to: " + args[i]);
					sender.sendMessage("Restoring items to: " + args[i]);
				}
				else sender.sendMessage("I have no record of player '" + args[i] + "'");
			}
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("getback"))
		{
			if (!sender.isOp() || !sender.hasPermission("stopwhiners.getback"))
			{
				sender.sendMessage("nope.avi");
				return true;
			}
			if (sender instanceof Player)
			{
				Player player = (Player)sender;
				boolean canGetback = false;
				if (iconomyEnabled)
				{
					if (iconomy.hasAccount(player.getName()))
					{
						Account acc = iconomy.getAccount(player.getName()); // i don't know a lot about this iconomy stuff, so i'm rather nesting ifs, instead of getting gajillions of nullref exceptions
						if (acc.getHoldings().hasEnough(5.0))               // can't debug this stuff live until the weekend :(
						{
							canGetback = true;
							acc.getHoldings().subtract(5.0);
						}
					}
					
				}
				else canGetback = true;
				if (canGetback)
				{
					for (int i = 0; i < lastDrops.get(player).size(); i++)
						player.getInventory().addItem(lastDrops.get(player).get(i));
				}
				else player.sendMessage("You either do not have sufficient funds (5.0) to buy back your stuff or you don't have an account.");
			}
			return true;
		}
		
		return true;
	}

	/**
	 * @return the lastDrops
	 */
	public HashMap<Player, List<ItemStack>> getLastDrops() {
		return lastDrops;
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}

}


