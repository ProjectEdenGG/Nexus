package gg.projecteden.nexus.features.events.y2021.pugmas21.advent;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.models.pugmas21.Advent21Config;
import gg.projecteden.nexus.models.pugmas21.Advent21Config.AdventPresent;
import gg.projecteden.nexus.models.pugmas21.Advent21ConfigService;
import gg.projecteden.nexus.models.pugmas21.Pugmas21User;
import gg.projecteden.nexus.models.pugmas21.Pugmas21UserService;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Utils;
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

public class Advent implements Listener {
	private static final Pugmas21UserService userService = new Pugmas21UserService();

	public Advent() {
		Nexus.registerListener(this);
		loadItems();
	}

	private void loadItems() {
		Advent21Config adventConfig = Advent21Config.get();
		Location lootOrigin = adventConfig.getLootOrigin();
		int index = 1;
		for (int z = 0; z <= 6; z++) {         // 0-3 col (Every other)
			for (int x = 0; x <= 12; x++) {    // 0-6 row (Every other)
				Block block = lootOrigin.getBlock().getRelative(x, 0, z);
				if (ItemUtils.isNullOrAir(block.getType()) || !block.getType().equals(Material.CHEST))
					continue;

				Chest chest = (Chest) block.getState();
				List<ItemStack> contents = Arrays.stream(chest.getBlockInventory().getContents())
					.filter(itemStack -> !ItemUtils.isNullOrAir(itemStack))
					.collect(Collectors.toList());

				adventConfig.get(index++).setItems(contents);
			}
		}
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
		AdventAnimation.builder()
			.location(player.getLocation())
			.player(player)
			.items(Advent21Config.get().get(day).getItems())
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
		if (ItemUtils.isNullOrAir(item))
			return;

		if (!item.getType().equals(Material.TRAPPED_CHEST))
			return;

		if (!CustomModel.exists(item))
			return;

		List<String> lore = item.getItemMeta().getLore();
		if (Utils.isNullOrEmpty(lore))
			return;

		for (String line : lore) {
			String _line = StringUtils.stripColor(line).trim();
			if (_line.matches("Day [0-9]+}")) {
				String day = _line.replaceAll("Day ", "");
				openPresent(player, Integer.parseInt(day));
				return;
			}
		}
	}

	@EventHandler
	public void onClickPresent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final Block block = event.getClickedBlock();
		if (block == null)
			return;

		if (block.getType() != Material.BARRIER)
			return;

		if (Pugmas21.TODAY.isAfter(Pugmas21.END))
			return;

		final Advent21Config adventConfig = new Advent21ConfigService().get0();
		final AdventPresent present = adventConfig.get(block.getLocation());
		if (present == null)
			return;

		new Pugmas21UserService().edit(player, user -> user.advent().tryCollect(present));
	}

}
