package me.pugabyte.bncore.features.mcmmo;

import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Crops;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class McMMOListener {
	public McMMOListener() {
		Tasks.repeat(0, 10, () -> {
			Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			players.forEach(player -> {
				// If player is wearing boots
				if (Utils.isNullOrAir(player.getInventory().getBoots()))
					return;

				// If player is wearing gold boots
				if (!player.getInventory().getBoots().getType().equals(Material.GOLD_BOOTS))
					return;

				// if player is in survival
				WorldGroup world = WorldGroup.get(player.getWorld());
				if (!world.equals(WorldGroup.SURVIVAL))
					return;

				// if boots has lore
				ItemStack boots = player.getInventory().getBoots();
				ItemMeta meta = boots.getItemMeta();
				List<String> lore = meta.getLore();
				if (lore == null)
					return;

				// if lore on boots contains "bonemeal boots"
				if (!(String.join(",", lore).contains("Bonemeal Boots")))
					return;

				// Loop all blocks in radius x of player
				Location location = player.getLocation();
				int radius = 5;
				for (int x = -radius; x <= radius; x++) {
					for (int z = -radius; z <= radius; z++) {
						for (int y = -radius; y <= radius; y++) {
							// 33% chance
							int random = Utils.randomInt(1, 3);
							if (random >= 2)
								continue;

							// If block is farmland
							Block block = location.getBlock().getRelative(x, y, z);
							if (!block.getType().equals(Material.SOIL))
								continue;

							// if block above dirt, is crops
							Block blockAbove = block.getRelative(0, 1, 0);
							if (!blockAbove.getType().equals(Material.POTATO))
								continue;

							Crops crop = (Crops) blockAbove.getState().getData();
							if (crop.getState() == CropState.RIPE)
								continue;

							int i = Arrays.asList(CropState.values()).indexOf(crop.getState());
							CropState newState = Arrays.asList(CropState.values()).get(i + 1);
							blockAbove.setData(newState.getData());

							// 50% chance
							random = Utils.randomInt(1, 4);
							if (random > 2)
								continue;

							player.spawnParticle(Particle.VILLAGER_HAPPY, blockAbove.getLocation(), 5, 0.5, 0.5, 0.5, 0.01);
						}
					}
				}

			});
		});
	}
}
