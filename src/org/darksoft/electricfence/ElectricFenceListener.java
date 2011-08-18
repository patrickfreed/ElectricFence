package org.darksoft.electricfence;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;

public class ElectricFenceListener extends BlockListener {
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		if (event.getBlock().getType().equals(Material.FENCE)) {
			Block b = event.getBlock();
			if (b.isBlockIndirectlyPowered()) {
				if (ElectricFence.earthBlockEnabled) {
					if ((b.getTypeId() != ElectricFence.earthBlock) && (b.getTypeId() != 85)) {
						return;
					}
				}
				Location location = player.getLocation();
				event.setCancelled(true);
				if (ElectricFence.canBeStruck(player)) {
					player.damage(ElectricFence.damage);
					player.getWorld().strikeLightningEffect(location);
					player.sendMessage("You tried to break through an electric fence!!");
					ElectricFence.message(player.getName() + " just tried to break an electric fence!");
				}
			}
		}
	}
}