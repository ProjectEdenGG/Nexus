package gg.projecteden.nexus.features.events.y2024.pugmas24.advent;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.advent.AdventAnimation;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.pugmas24.Advent24Config;
import gg.projecteden.nexus.models.pugmas24.Advent24ConfigService;
import gg.projecteden.nexus.models.pugmas24.Advent24Present;
import gg.projecteden.nexus.models.pugmas24.Pugmas24User;
import gg.projecteden.nexus.models.pugmas24.Pugmas24UserService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

/*
 	TODO:
 		- GLOWING / WAYPOINT
 		- CLIENTSIDE PRESENTS --> /clientside convert
 */
public class Pugmas24Advent implements Listener {

	private static final Pugmas24UserService userService = new Pugmas24UserService();

	public Pugmas24Advent() {
		Nexus.registerListener(this);
	}

	public static void glow(Pugmas24User user, int day) {
		// TODO
		user.sendMessage("TODO glow, day = " + day);
	}

	public static void openPresent(Player player, int day) {
		Advent24Present present = Advent24Config.get().get(day);

		AdventAnimation.builder()
			.location(player.getLocation())
			.player(player)
			.presentDay(present.getDay())
			.presentContents(present.getContents())
			.build()
			.open();
	}

	public static void sendPackets(Player player) {
		final Pugmas24User user = userService.get(player);
		for (Advent24Present present : Advent24Config.get().getPresents())
			user.advent().show(present);
	}

	@EventHandler
	public void onOpenPresent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (!Pugmas24.get().isAtEvent(event))
			return;

		ItemStack item = event.getItem();
		if (isNullOrAir(item))
			return;

		final CustomMaterial customMaterial = CustomMaterial.of(item);
		if (customMaterial != CustomMaterial.PUGMAS_PRESENT_ADVENT)
			return;

		List<String> lore = item.getItemMeta().getLore();
		if (isNullOrEmpty(lore))
			return;

		for (String line : lore) {
			String _line = StringUtils.stripColor(line).trim();
			if (_line.matches("Day #[0-9]+")) {
				try {
					int day = Integer.parseInt(_line.replaceAll("Day #", ""));
					openPresent(player, day);
					item.subtract();
					return;
				} catch (Exception ignored) {
				}
			}
		}
	}

	@EventHandler
	public void onClickPresent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (!Pugmas24.get().isAtEvent(event))
			return;

		if (!Pugmas24.get().shouldHandle(event.getPlayer()))
			return;

		final Block block = event.getClickedBlock();
		if (block == null)
			return;

		if (block.getType() != Material.BARRIER)
			return;

		if (Pugmas24.get().isAfterEvent())
			return;

		final Advent24Config adventConfig = new Advent24ConfigService().get0();
		final Advent24Present present = adventConfig.get(block.getLocation());
		if (present == null)
			return;

		new Pugmas24UserService().edit(player, user -> user.advent().tryCollect(present));
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
