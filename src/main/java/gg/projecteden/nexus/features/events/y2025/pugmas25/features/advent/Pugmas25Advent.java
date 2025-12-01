package gg.projecteden.nexus.features.events.y2025.pugmas25.features.advent;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.events.advent.AdventAnimation;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideConfig.ClientSideItemFrameModifier;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.models.pugmas25.Advent25Config;
import gg.projecteden.nexus.models.pugmas25.Advent25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Advent25Present;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.features.resourcepack.models.ItemModelType.PUGMAS_PRESENT_ADVENT;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class Pugmas25Advent implements Listener {

	public Pugmas25Advent() {
		Nexus.registerListener(this);
		Pugmas25UserService service = new Pugmas25UserService();

		ClientSideConfig.registerItemFrameModifier(new ClientSideItemFrameModifier() {
			@Override
			public ItemStack modify(ClientSideUser user, ClientSideItemFrame itemFrame) {
				var present = Advent25Config.getPresent(itemFrame);
				if (present == null)
					return null;

				var adventUser = service.get(user).advent();
				var status = adventUser.getStatus(present);
				return status.getFrameItem().build();
			}
		});

		Tasks.repeat(5, TickTime.SECOND.x(3), () -> {
			Advent25Config.get().getDays().values().forEach(present -> {
				Pugmas25.get().getOnlinePlayers().forEach(player -> {
					var adventUser = service.get(player).advent();
					if (!adventUser.hasCollected(present))
						new ParticleBuilder(Particle.HAPPY_VILLAGER)
							.location(present.getLocation().toCenterLocation()).offset(0.5, 0.5, 0.5)
							.extra(0)
							.receivers(player)
							.spawn();
				});
			});

		});
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

		if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
			return;

		ItemStack item = event.getItem();
		if (isNullOrAir(item))
			return;

		final ItemModelType itemModelType = ItemModelType.of(item);
		if (itemModelType != PUGMAS_PRESENT_ADVENT)
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
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@EventHandler
	public void onClickPresent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (event.getHand() != EquipmentSlot.HAND)
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
