package org.darksoft.electricfence;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

public class ElectricFenceListener implements Listener {

	private ElectricFence electricFence;

	ElectricFenceListener(ElectricFence e) {
		this.electricFence = e;
	}

	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		if (electricFence.isElectricFence(event.getBlock())) {
			Block b = event.getBlock();

			Location location = player.getLocation();
			if (electricFence.canBeStruck(player) && electricFence.isElectricFence(b)) {
				player.damage(electricFence.damage);

				if (electricFence.isMessaging) {
					player.sendMessage(ChatColor.YELLOW + "You tried to break through an electric fence!");
				}

				if (electricFence.isUsingLightning) {
					player.getWorld().strikeLightningEffect(location);
				}

				event.setCancelled(true);
			}
		}
	}
}