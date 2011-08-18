package org.darksoft.electricfence;

import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class ElectricFence extends JavaPlugin{
	public static Configuration config;
	public static int damage;
	public static int radiusDamage;
	public static boolean radiusDamageEnabled;
	public static boolean earthBlockEnabled;
	public static int earthBlock;
	private final ElectricFenceListener blockDamageListener = new ElectricFenceListener();

	public void onEnable()
	{
		message("ElectricFence + " + this.getDescription().getVersion() + " has been enabled!");
		try
		{
			File newDir = new File("plugins/ElectricFence");
			if (!newDir.exists())
				newDir.mkdirs();
			File yml = new File("plugins/ElectricFence/config.yml");
			if (!yml.exists())
			{

				yml.createNewFile();
				config = getConfiguration();
				config.setProperty("damage", 10);
				config.setProperty("radiusDamageEnabled", true);
				config.setProperty("radiusDamage", 5);
				config.setProperty("earthBlockEnabled", true);
				config.setProperty("earthBlock", 15);
				config.save();

				damage = config.getInt("damage", 10);
				radiusDamageEnabled = config.getBoolean("radiusDamageEnabled", true);
				radiusDamage = config.getInt("radiusDamage", 0);
				earthBlockEnabled = config.getBoolean("earthBlockEnabled", true);
				earthBlock = config.getInt("earthBlock", 15);

				message("Configuration file created then loaded");
			}
			else
			{
				config = getConfiguration();
				damage = config.getInt("damage", 0);
				radiusDamageEnabled = config.getBoolean("radiusDamageEnabled", true);
				radiusDamage = config.getInt("radiusDamage", 0);
				earthBlockEnabled = config.getBoolean("earthBlockEnabled", true);
				earthBlock = config.getInt("earthBlock", 0);
				message("Configuration file loaded");
			}
		}
		catch (IOException e)
		{
			message("Config file read error");
		}

		if (radiusDamageEnabled)
		{
			getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
				public void run() {
					for (World world : ElectricFence.this.getServer().getWorlds())
						for (Entity entity : world.getEntities())
							if ((entity instanceof Player)){
								Block block = entity.getLocation().getBlock();
								if (entity.getLocation().getBlock().getRelative(BlockFace.NORTH_WEST).getType() == Material.FENCE)
								{
									if (earthBlockEnabled) {
										if ((block.getTypeId() != ElectricFence.earthBlock) && (block.getTypeId() != 85)) {
											return;
										}
									}
									if (!block.getRelative(BlockFace.NORTH_WEST).isBlockIndirectlyPowered())
										continue;
									ElectricFence.this.radiusStrike((Player)entity);
								}
								else if (block.getRelative(BlockFace.NORTH).getType() == Material.FENCE)
								{
									if (earthBlockEnabled) {
										if ((block.getTypeId() != ElectricFence.earthBlock) && (block.getTypeId() != 85)) {
											return;
										}
									}
									if (!block.getRelative(BlockFace.NORTH).isBlockIndirectlyPowered())
										continue;
									ElectricFence.this.radiusStrike((Player)entity);
								}
								else if (block.getRelative(BlockFace.NORTH_EAST).getType() == Material.FENCE)
								{
									if (earthBlockEnabled) {
										if ((block.getTypeId() != ElectricFence.earthBlock) && (block.getTypeId() != 85)) {
											return;
										}
									}
									if (!block.getRelative(BlockFace.NORTH_EAST).isBlockIndirectlyPowered())
										continue;
									ElectricFence.this.radiusStrike((Player)entity);
								}
								else if (block.getRelative(BlockFace.EAST).getType() == Material.FENCE)
								{
									if (earthBlockEnabled) {
										if ((block.getTypeId() != ElectricFence.earthBlock) && (block.getTypeId() != 85)) {
											return;
										}
									}
									if (!block.getRelative(BlockFace.EAST).isBlockIndirectlyPowered())
										continue;
									ElectricFence.this.radiusStrike((Player)entity);
								}
								else if (block.getRelative(BlockFace.SOUTH_EAST).getType() == Material.FENCE)
								{
									if (earthBlockEnabled) {
										if ((block.getTypeId() != ElectricFence.earthBlock) && (block.getTypeId() != 85)) {
											return;
										}
									}
									if (!block.getRelative(BlockFace.SOUTH_EAST).isBlockIndirectlyPowered())
										continue;
									ElectricFence.this.radiusStrike((Player)entity);
								}
								else if (block.getRelative(BlockFace.SOUTH).getType() == Material.FENCE)
								{
									if (earthBlockEnabled) {
										if ((block.getTypeId() != ElectricFence.earthBlock) && (block.getTypeId() != 85)) {
											return;
										}
									}
									if (!block.getRelative(BlockFace.SOUTH).isBlockIndirectlyPowered())
										continue;
									ElectricFence.this.radiusStrike((Player)entity);
								}
								else if (block.getRelative(BlockFace.SOUTH_WEST).getType() == Material.FENCE)
								{
									if (earthBlockEnabled) {
										if ((block.getTypeId() != ElectricFence.earthBlock) && (block.getTypeId() != 85)) {
											return;
										}
									}
									if (!block.getRelative(BlockFace.SOUTH_WEST).isBlockIndirectlyPowered())
										continue;
									ElectricFence.this.radiusStrike((Player)entity);
								}
								else {
									if (block.getRelative(BlockFace.WEST).getType() != Material.FENCE)
										continue;
									if (earthBlockEnabled) {
										if ((block.getTypeId() != ElectricFence.earthBlock) && (block.getTypeId() != 85)) {
											return;
										}
									}
									if (!block.getRelative(BlockFace.WEST).isBlockIndirectlyPowered())
										continue;
									ElectricFence.this.radiusStrike((Player)entity);
								}
							}
				}
			}
			, 20L, 20L);
		}

		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_DAMAGE, this.blockDamageListener, Event.Priority.Normal, this);
	}

	public void radiusStrike(Player player)
	{
		Location location = player.getLocation();
		if (canBeStruck(player))
		{
			player.getWorld().strikeLightningEffect(location);
			player.damage(radiusDamage);
			player.sendMessage("You got too close to an electric fence!");
			message(player.getName() + " got zapped by an electric fence!");
		}

	}

	public void onDisable()
	{
		message("Plugin shutting down!");
	}

	public static void message(String msg)
	{
		System.out.println("[ElectricFence]: " + msg);
	}

	public static boolean canBeStruck(Player player)
	{
		return !player.isOp() && !player.hasPermission("ElectricFence.bypass");
	}


}