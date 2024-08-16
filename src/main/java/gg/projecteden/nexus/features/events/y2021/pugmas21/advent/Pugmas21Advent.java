package gg.projecteden.nexus.features.events.y2021.pugmas21.advent;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.Pugmas21District;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeavingRegionEvent;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.pugmas21.Advent21Config;
import gg.projecteden.nexus.models.pugmas21.Advent21Config.AdventPresent;
import gg.projecteden.nexus.models.pugmas21.Advent21ConfigService;
import gg.projecteden.nexus.models.pugmas21.Pugmas21User;
import gg.projecteden.nexus.models.pugmas21.Pugmas21UserService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class Pugmas21Advent implements Listener {
	private static final Pugmas21UserService userService = new Pugmas21UserService();

	public Pugmas21Advent() {
		Nexus.registerListener(this);
	}

	public static void updateItems() {
		Advent21ConfigService configService = new Advent21ConfigService();
		Advent21Config adventConfig = Advent21Config.get();
		Location lootOrigin = adventConfig.getLootOrigin();
		int index = 1;
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
					Nexus.warn("Contents of advent present " + index + " is empty!");

				adventConfig.get(index++).setContents(contents);
			}
		}

		configService.save(adventConfig);
	}

	static {
		for (Player player : OnlinePlayers.where().world(Pugmas21.getWorld()).get())
			sendPackets(player);
	}

	public static void shutdown() {
		for (Player player : OnlinePlayers.where().world(Pugmas21.getWorld()).get()) {
			final Pugmas21User user = userService.get(player);
			for (AdventPresent present : Advent21Config.get().getPresents())
				user.advent().hide(present);
		}
	}

	private static void sendPackets(Player player) {
		final Pugmas21User user = userService.get(player);
		for (AdventPresent present : Advent21Config.get().getPresents())
			user.advent().show(present);
	}

	public static void glow(Pugmas21User user, int day) {
		user.advent().locate(Advent21Config.get().get(day));
	}

	public static void openPresent(Player player, int day) {
		Pugmas21AdventAnimation.builder()
			.location(player.getLocation())
			.player(player)
			.present(Advent21Config.get().get(day))
			.build()
			.open();
	}

	@EventHandler
	public void on(PlayerChangedWorldEvent event) {
		if (Pugmas21.isAtPugmas(event.getPlayer()))
			Tasks.wait(1, () -> sendPackets(event.getPlayer()));
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		if (Pugmas21.isAtPugmas(event.getPlayer()))
			Tasks.wait(1, () -> sendPackets(event.getPlayer()));
	}

	@EventHandler
	public void onOpenPresent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		ItemStack item = event.getItem();
		if (isNullOrAir(item))
			return;

		final CustomMaterial customMaterial = CustomMaterial.of(item);
		if (customMaterial == null || !customMaterial.name().startsWith(CustomMaterial.PUGMAS_PRESENT_ADVENT.name().replace("ADVENT", "")))
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

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		if (!Pugmas21.isAtPugmas(player))
			return;

		final Block block = event.getClickedBlock();
		if (block == null)
			return;

		if (block.getType() != Material.BARRIER)
			return;

		if (Pugmas21.isPastPugmas())
			return;

		final Advent21Config adventConfig = new Advent21ConfigService().get0();
		final AdventPresent present = adventConfig.get(block.getLocation());
		if (present == null)
			return;

		new Pugmas21UserService().edit(player, user -> user.advent().tryCollect(present));
	}

	@EventHandler
	public void onDistrictEnter(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas21.isAtPugmas(player)) return;

		Pugmas21District district = Pugmas21District.of(player.getLocation());
		if (district != null && district != Pugmas21District.UNKNOWN)
			ActionBarUtils.sendActionBar(player, "&a&lEntering " + district.getFullName());
	}

	@EventHandler
	public void onDistrictExit(PlayerLeavingRegionEvent event) {
		Player player = event.getPlayer();
		if (!Pugmas21.isAtPugmas(player)) return;

		Pugmas21District district = Pugmas21District.of(player.getLocation());
		if (district != null && district != Pugmas21District.UNKNOWN)
			ActionBarUtils.sendActionBar(player, "&c&lExiting " + district.getFullName());
	}

}
