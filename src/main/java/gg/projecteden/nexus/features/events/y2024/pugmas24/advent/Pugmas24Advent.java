package gg.projecteden.nexus.features.events.y2024.pugmas24.advent;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.models.pugmas24.Advent24Config;
import gg.projecteden.nexus.models.pugmas24.Advent24ConfigService;
import gg.projecteden.nexus.models.pugmas24.Advent24Present;
import gg.projecteden.nexus.models.pugmas24.Pugmas24User;
import gg.projecteden.nexus.models.pugmas24.Pugmas24UserService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

// TODO
@NoArgsConstructor
public class Pugmas24Advent {

	private static final Pugmas24UserService userService = new Pugmas24UserService();

	public static void shutdown() {
		for (Player player : OnlinePlayers.where().world(Pugmas21.getWorld()).get()) {
			final Pugmas24User user = userService.get(player);
			for (Advent24Present present : Advent24Config.get().getPresents())
				user.advent().hide(present);
		}
	}

	public static void glow(Pugmas24User user, int day) {
		// TODO
		user.sendMessage("TODO: day = " + day);
	}

	public static void updateItems() {
		Advent24ConfigService configService = new Advent24ConfigService();
		Advent24Config adventConfig = Advent24Config.get();

		Location lootOrigin = adventConfig.getLootOrigin();
		lootOrigin = lootOrigin.add(2, 0, 0);

		int day = 1;
		for (int z = 0; z <= 6; z++) {         // 0-3 col (Every other)
			for (int x = 0; x <= 12; x++) {    // 0-6 row (Every other)
				Block block = lootOrigin.getBlock().getRelative(x, 0, z);
				if (isNullOrAir(block.getType()) || !block.getType().equals(Material.CHEST))
					continue;

				Chest chest = (Chest) block.getState();
				List<ItemStack> contents = Arrays.stream(chest.getBlockInventory().getContents())
						.filter(Nullables::isNotNullOrAir)
						.collect(Collectors.toList());

				if (isNullOrEmpty(contents))
					Nexus.warn("Contents of advent present " + day + " is empty!");

				adventConfig.get(day++).setContents(contents);
			}
		}

		configService.save(adventConfig);
	}
}
