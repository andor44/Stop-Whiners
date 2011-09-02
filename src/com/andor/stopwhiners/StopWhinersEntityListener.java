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

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class StopWhinersEntityListener extends EntityListener {
	private final StopWhinersPlugin plugin;
	
	public StopWhinersEntityListener(StopWhinersPlugin instance)
	{
		plugin = instance;
	}
	
	@Override
	public void onEntityDeath(EntityDeathEvent evnt)
	{
		if (evnt.getEntity() instanceof Player)
		{
			Player player = (Player)evnt.getEntity();
			plugin.getLastDrops().put(player, evnt.getDrops());
			plugin.getLogger().info("Player '" + player.getName() +"' was killed. Death cause: " + player.getLastDamageCause().getEntity().toString() + " (" + player.getLastDamageCause().getCause().name() + ")");
		}
		else return;
	}
}
