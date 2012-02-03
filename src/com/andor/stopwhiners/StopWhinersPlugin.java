/*
 * StopWhiners CraftBukkit plugin
 * 
 * Licensed under the zlib/png license
 * 
 * Copyright (c) 2011 Andor Uhlár
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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;

public class StopWhinersPlugin extends JavaPlugin {
	private final Logger logger = Logger.getLogger("Minecraft");
	private final StopWhinersEntityListener entityListener = new StopWhinersEntityListener(this);
	private final HashMap<Player, List<ItemStack>> lastDrops = new HashMap<Player, List<ItemStack>>();
	
	private Method paymentMethod;
	private boolean paymentEnabled = false;
	
	@Override
	public void onDisable() {
		getLogger().info("StopWhiners was disabled. Goodbye cruel world.");
		
	}

	@Override
	public void onEnable() {
		getLogger().info("StopWhiners was enabled. Yayifications!.");
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(entityListener, this);
		
		Methods.setMethod(getServer().getPluginManager());
		Methods.setPreferred("iConomy");
		if(Methods.hasMethod())
		{
			paymentMethod = Methods.getMethod();
			paymentEnabled = true;			
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("giveback"))
		{
			if (!sender.isOp() || !sender.hasPermission("stopwhiners.giveback"))
			{
				sender.sendMessage("Unauthorized");
				return true;
			}
			
			if (args.length < 1)
				return false;
			
			for (int i = 0; i < args.length; i++) {
				if (lastDrops.containsKey(getServer().getPlayer(args[i])))
				{
					//getServer().getPlayer(args[i]).getInventory().setContents((ItemStack[]) lastDrops.get(getServer().getPlayer(args[i])).toArray() ); // dongs, this should work but does not
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
				sender.sendMessage("Unauthorized");
				return true;
			}
			if (sender instanceof Player)
			{
				Player player = (Player)sender;
				boolean canGetback = false;
				if (paymentEnabled && getConfig().isDouble("stopwhiners.cost"))
				{
					if(paymentMethod.hasAccount(player.getName()))
					{
						if(paymentMethod.getAccount(player.getName()).hasEnough(getConfig().getDouble("stopwhiners.cost")))
						{
							canGetback = true;
							paymentMethod.getAccount(player.getName()).subtract(getConfig().getDouble("stopwhiners.cost"));
						}
					}
					else canGetback = false;
				}
				else canGetback = true;
				if (canGetback)
				{
					for (int i = 0; i < lastDrops.get(player).size(); i++)
						player.getInventory().addItem(lastDrops.get(player).get(i));
				}
				else player.sendMessage("You either do not have sufficient funds (" + getConfig().getDouble("stopwhiners.cost") + ") to buy back your stuff or you don't have an account.");
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


