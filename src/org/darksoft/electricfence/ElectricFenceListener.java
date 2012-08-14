package org.darksoft.electricfence;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Redstone;

public class ElectricFenceListener implements Listener{
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event){
		Player player = event.getPlayer();
		if (ElectricFence.isElectricFence(event.getBlock())) {
			Block b = event.getBlock();
			if (isBlockIndirectlyPowered(b)) {
				Location location = player.getLocation();
				if (ElectricFence.canBeStruck(player)) {
					player.damage(ElectricFence.damage);
					if(ElectricFence.isMessaging)
						player.sendMessage(ChatColor.YELLOW + "You tried to break through an electric fence!!");
					if(ElectricFence.isUsingLightning)
						player.getWorld().strikeLightningEffect(location);
					event.setCancelled(true);
				}
			}
		}
	}

	public static boolean isBlockIndirectlyPowered(Block b) {
		int x = b.getX();
		int y = b.getY();
		int z = b.getZ();

		return (isBlockPowered(b, x + 1, y, z)) || 
		(isBlockPowered(b, x - 1, y, z)) || 
		(isBlockPowered(b, x, y - 2, z)) || 
		(isBlockPowered(b, x, y - 3, z)) || 
		(isBlockPowered(b, x, y - 4, z)) || 
		(isBlockPowered(b, x, y - 5, z)) || 
		(isBlockPowered(b, x, y - 6, z)) || 
		(isBlockPowered(b, x, y - 7, z)) || 
		(isBlockPowered(b, x, y - 8, z)) || 
		(isBlockPowered(b, x, y - 9, z)) || 
		(isBlockPowered(b, x, y - 10, z)) || 
		(isBlockPowered(b, x, y - 11, z)) || 
		(isBlockPowered(b, x, y + 1, z)) || 
		(isBlockPowered(b, x, y - 1, z)) || 
		(isBlockPowered(b, x, y, z + 1)) || 
		(isBlockPowered(b, x, y, z - 1));
	}

	public static boolean isBlockPowered(Block b, int x, int y, int z) {
		MaterialData md = b.getWorld().getBlockAt(x, y, z).getState().getData();
		if ((md instanceof Redstone)) {
			return ((Redstone)md).isPowered();
		}
		return false;
	}
}