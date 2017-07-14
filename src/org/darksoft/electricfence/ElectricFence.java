package org.darksoft.electricfence;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ElectricFence extends JavaPlugin {

	boolean isUsingLightning;
	boolean isMessaging;
	int damage;

	private int radiusDamage;
	private boolean radiusDamageEnabled;

	private YamlConfiguration config;

	private boolean isShockingMobs;
	private boolean isElectricWood;
	private boolean isElectricIron;
	private boolean isShockingPlayers;

	private final static String BLOCK_DAMAGE_CONFIG = "damage";
	private final static String RADIUS_DAMAGE_ENABLED_CONFIG = "radiusDamageEnabled";
	private final static String RADIUS_DAMAGE_CONFIG = "radiusDamage";
	private final static String SHOCK_MOBS_CONFIG = "Shock.Mobs";
	private final static String SHOCK_PLAYERS_CONFIG = "Shock.Players";
	private final static String FENCE_TYPE_WOOD_CONFIG = "FenceTypes.Wood";
	private final static String FENCE_TYPE_IRON_CONFIG = "FenceTypes.Iron";
	private final static String MESSAGING_CONFIG = "isSendingMessages";
	private final static String LIGHTNING_CONFIG = "useLightningEffect";

	private final static BlockFace[] directions = {
			BlockFace.DOWN,
			BlockFace.UP,
			BlockFace.NORTH,
			BlockFace.SOUTH,
			BlockFace.WEST,
			BlockFace.EAST
	};

	public void onEnable() {
		File newDir = new File("plugins/ElectricFence");
		File configFile = new File("plugins/ElectricFence", "config.yml");

		config = YamlConfiguration.loadConfiguration(new File("plugins/ElectricFence", "config.yml"));

		if (!newDir.exists()) {
			newDir.mkdirs();
		}

		if (!configFile.exists()) {
			config.set(BLOCK_DAMAGE_CONFIG, 0);
			config.set(RADIUS_DAMAGE_ENABLED_CONFIG, true);
			config.set(RADIUS_DAMAGE_CONFIG, 0);
			config.set(SHOCK_MOBS_CONFIG, true);
			config.set(SHOCK_PLAYERS_CONFIG, true);
			config.set(FENCE_TYPE_WOOD_CONFIG, true);
			config.set(FENCE_TYPE_IRON_CONFIG, true);
			config.set(MESSAGING_CONFIG, true);
			config.set(LIGHTNING_CONFIG, true);

			try {
				config.save(new File("plugins/ElectricFence", "config.yml"));
			} catch (IOException e1) {
				message("Failed to save configuration file!");
				e1.printStackTrace();
			}
		}

		damage = config.getInt(BLOCK_DAMAGE_CONFIG);
		radiusDamageEnabled = config.getBoolean(RADIUS_DAMAGE_ENABLED_CONFIG);
		radiusDamage = config.getInt(RADIUS_DAMAGE_CONFIG);
		isShockingMobs = config.getBoolean(SHOCK_MOBS_CONFIG);
		isShockingPlayers = config.getBoolean(SHOCK_PLAYERS_CONFIG);
		isElectricWood = config.getBoolean(FENCE_TYPE_WOOD_CONFIG);
		isElectricIron = config.getBoolean(FENCE_TYPE_IRON_CONFIG);
		isMessaging = config.getBoolean(MESSAGING_CONFIG);
		isUsingLightning = config.getBoolean(LIGHTNING_CONFIG);

		message("Configuration file loaded");

		new Metrics(this);

		if (radiusDamageEnabled) {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
                for (World world : ElectricFence.this.getServer().getWorlds()) {
					for (Entity entity : world.getEntities()) {
						if (!canBeStruck(entity)) {
							continue;
						}

						Block block = entity.getLocation().getBlock();
						LivingEntity living = (LivingEntity) entity;

						if (isElectricFence(block)) {
							radiusStrike(living);
							break;
						}

						for (BlockFace direction : directions) {
							if (isElectricFence(block.getRelative(direction))) {
								radiusStrike(living);
								break;
							}
						}
					}
				}
            }, 20L, 20L);
		}

		if (isShockingPlayers) {
			getServer().getPluginManager().registerEvents(new ElectricFenceListener(this), this);
		}

		message("ElectricFence v" + getDescription().getVersion() + " has been enabled!");
	}

	private void radiusStrike(LivingEntity entity) {
		Location location = entity.getLocation();

		if (isMessaging) {
			entity.sendMessage(ChatColor.YELLOW + "You got too close to an electric fence!");
		}

		if (isUsingLightning) {
			entity.getWorld().strikeLightningEffect(location);
		}

		entity.damage(radiusDamage);
	}

	public void onDisable() {
		message("Plugin shutting down!");
	}

	boolean canBeStruck(Entity entity) {
		if (entity instanceof Player) {
			return isShockingPlayers && !entity.isOp() && !entity.hasPermission("ElectricFence.bypass");
		}

		return isShockingMobs && entity instanceof LivingEntity;
	}

	boolean isElectricFence(Block b) {
		boolean type = false;

		if (isElectricWood) {
			type = b.getType() == Material.FENCE;
		} else if (isElectricIron) {
			type = b.getType() == Material.IRON_FENCE;
		}

		if (type) {
			if (b.isBlockIndirectlyPowered() || b.isBlockPowered()) {
				return true;
			}

			for (BlockFace direction : directions) {
				if (b.isBlockFaceIndirectlyPowered(direction) || b.isBlockFacePowered(direction)) {
					return true;
				}
			}
		}

		return false;
	}

	private static void message(String msg) {
		System.out.println("[ElectricFence] " + msg);
	}
}