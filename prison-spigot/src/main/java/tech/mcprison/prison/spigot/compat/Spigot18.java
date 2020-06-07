/*
 *  Prison is a Minecraft plugin for the prison game mode.
 *  Copyright (C) 2017 The Prison Team
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package tech.mcprison.prison.spigot.compat;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Faizaan A. Datoo
 */
public class Spigot18 implements Compatibility {

    @Override 
    public EquipmentSlot getHand(PlayerInteractEvent e) {
        return EquipmentSlot.HAND; // Spigot 1.8 only has one hand
    }

    @SuppressWarnings( "deprecation" )
	@Override 
	public ItemStack getItemInMainHand(PlayerInteractEvent e) {
        return e.getPlayer().getItemInHand();
    }

    @SuppressWarnings( "deprecation" )
    @Override 
    public ItemStack getItemInMainHand(Player player ) {
    	return player.getItemInHand();
    }
    
    @Override 
    public void playIronDoorSound(Location loc) {
        loc.getWorld().playEffect(loc, Effect.DOOR_TOGGLE, null);
    }

	@SuppressWarnings( "deprecation" )
	@Override
	public void breakItemInMainHand( Player player ) {
		player.setItemInHand( null );
		
		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 0.85F); 
	}

}
