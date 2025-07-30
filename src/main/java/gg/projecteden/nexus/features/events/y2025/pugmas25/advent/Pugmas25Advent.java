package gg.projecteden.nexus.features.events.y2025.pugmas25.advent;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import gg.projecteden.nexus.features.events.advent.AdventAnimation;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideConfig.ClientSideVisibilityCondition;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.models.pugmas25.Advent25Config;
import gg.projecteden.nexus.models.pugmas25.Advent25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Advent25Present;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
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

import static gg.projecteden.nexus.features.resourcepack.models.ItemModelType.PUGMAS_PRESENT_ADVENT;
import static gg.projecteden.nexus.features.resourcepack.models.ItemModelType.PUGMAS_PRESENT_ADVENT_OPENED;

/*
 	TODO:
 		- GLOWING / WAYPOINT
 		- CLIENTSIDE PRESENTS --> /clientside convert
 */
public class Pugmas25Advent implements Listener {

	private static final Pugmas25UserService userService = new Pugmas25UserService();

	public Pugmas25Advent() {
		Nexus.registerListener(this);

		ClientSideConfig.registerVisibilityCondition(new ClientSideVisibilityCondition() {
			@Override
			public boolean shouldHide(ClientSideUser user, IClientSideEntity<?, ?, ?> entity) {
				if (!(entity instanceof ClientSideItemFrame itemFrame))
					return false;

				var present = Advent25Config.getPresent(itemFrame);
				if (present == null)
					return false;

				var adventUser = new Pugmas25UserService().get(user).advent();

				if (PUGMAS_PRESENT_ADVENT.is(itemFrame.content()))
					return adventUser.hasFound(present.getDay());
				else if (PUGMAS_PRESENT_ADVENT_OPENED.is(itemFrame.content()))
					return !adventUser.hasFound(present.getDay());
				else
					return false;
			}
		});
	}

	public static void glow(Pugmas25User user, int day) {
		// TODO
		user.sendMessage("TODO glow, day = " + day);
	}

	public static void openPresent(Player player, int day) {
		Advent25Present present = Advent25Config.get().get(day);

		AdventAnimation.builder()
			.location(player.getLocation())
			.player(player)
			.presentDay(present.getDay())
			.presentContents(present.getContents())
			.build()
			.open();
	}

	@EventHandler
	public void onOpenPresent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (!Pugmas25.get().isAtEvent(event))
			return;

		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item))
			return;

		final ItemModelType itemModelType = ItemModelType.of(item);
		if (itemModelType != PUGMAS_PRESENT_ADVENT)
			return;

		List<String> lore = item.getItemMeta().getLore();
		if (gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(lore))
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

		if (!Pugmas25.get().isAtEvent(event))
			return;

		if (!Pugmas25.get().shouldHandle(event.getPlayer()))
			return;

		final Block block = event.getClickedBlock();
		if (block == null)
			return;

		if (block.getType() != Material.BARRIER)
			return;

		if (Pugmas25.get().isAfterEvent())
			return;

		final Advent25Config adventConfig = new Advent25ConfigService().get0();
		final Advent25Present present = adventConfig.get(block.getLocation());
		if (present == null)
			return;

		new Pugmas25UserService().edit(player, user -> user.advent().tryCollect(present));
	}


	public static void updateItems() {
		Advent25ConfigService configService = new Advent25ConfigService();
		Advent25Config adventConfig = Advent25Config.get();

		Location lootOrigin = adventConfig.getLootOrigin();
		lootOrigin = lootOrigin.add(2, 0, 0);

		int day = 1;
		for (int z = 0; z <= 6; z++) {         // 0-3 col (Every other)
			for (int x = 0; x <= 12; x++) {    // 0-6 row (Every other)
				Block block = lootOrigin.getBlock().getRelative(x, 0, z);
				if (Nullables.isNullOrAir(block.getType()) || !block.getType().equals(Material.CHEST))
					continue;

				Chest chest = (Chest) block.getState();
				List<ItemStack> contents = Arrays.stream(chest.getBlockInventory().getContents())
						.filter(Nullables::isNotNullOrAir)
						.collect(Collectors.toList());

				if (gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(contents))
					Nexus.warn("Contents of advent present " + day + " is empty!");

				adventConfig.get(day++).setContents(contents);
			}
		}

		configService.save(adventConfig);
	}
}
