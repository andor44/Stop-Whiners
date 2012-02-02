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

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class StopWhinersEntityListener implements Listener {
	private final StopWhinersPlugin plugin;
	
	public StopWhinersEntityListener(StopWhinersPlugin instance)
	{
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(EntityDeathEvent evnt)
	{
		if (evnt.getEntity() instanceof Player)
		{
			Player player = (Player)evnt.getEntity();
			plugin.getLastDrops().put(player, evnt.getDrops());
			try {
				plugin.getLogger().info("Player '" + player.getName() + "' was killed. Death cause: " + player.getLastDamageCause().getEntity().toString() + " (" + player.getLastDamageCause().getCause().name() + ")");
			} catch (Exception e) {
				//plugin.getLogger().info("Player '" + player.getName() + "' suicided?");
			}
		}
		else return;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerSpawn(CreatureSpawnEvent evnt)
	{
		if (evnt.getEntity() instanceof Player)
		{
			Player player = (Player)evnt.getEntity();
			if (player.hasPermission("stopwhiners.auto") && plugin.getLastDrops().containsKey(player.getName()))
			{
				player.getInventory().setContents((ItemStack[]) plugin.getLastDrops().get(player).toArray());
			}
		}
	}
}
