package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Pugmas25Waystones implements Listener {

	public Pugmas25Waystones() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (!Pugmas25.get().shouldHandle(event.getPlayer()))
			return;

		if (!Pugmas25.get().isAtEvent(event))
			return;

		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item))
			return;

		if (!Pugmas25QuestItem.MAGIC_MIRROR.fuzzyMatch(item))
			return;

		new Pugmas25WaystoneMenu().open(event.getPlayer());
	}

	@AllArgsConstructor
	public enum Pugmas25Waystone {
		HOT_SPRINGS(loc(-474, 127, -3060), loc(-474, 127, -3059, 45).toCenterLocation()),
		TRAIN_STATION(loc(-689, 83, -2961), loc(-689, 82, -2966, 180).toCenterLocation()),
		FAIRGROUNDS(loc(-763, 81, -2987), loc(-762, 81, -2896, -45).toCenterLocation()),
		TOWN(loc(-721, 118, -3161), loc(-720, 118, -3159, -28).toCenterLocation()),
		RIDGE(null, null), // TODO
		;

		final Location frameLoc;
		final Location warpLoc;

		private static Location loc(int x, int y, int z) {
			return Pugmas25.get().location(x, y, z);
		}

		private static Location loc(int x, int y, int z, int yaw) {
			return Pugmas25.get().location(x, y, z, yaw, 0);
		}
	}

	@Rows(3)
	@Title("Teleport to a waystone")
	@NoArgsConstructor
	public static class Pugmas25WaystoneMenu extends InventoryProvider {

		@Override
		public void init() {
			addCloseItem();

			List<ClickableItem> items = new ArrayList<>();
			for (Pugmas25Waystone waystone : Pugmas25Waystone.values()) {
				ItemBuilder item = DecorationType.WAYSTONE_ACTIVATED.getConfig().getItemBuilder().name(StringUtils.camelCase(waystone)).resetLore();
				items.add(ClickableItem.of(item, e -> teleport(waystone)));
			}

			paginate(items);
		}

		private void teleport(Pugmas25Waystone waystone) {
			close();

			Pugmas25.get().fadeToBlack(viewer)
				.thenRun(() -> {
					Pugmas25.get().poof(viewer.getLocation());
					viewer.teleportAsync(waystone.warpLoc, TeleportCause.PLUGIN).thenRun(() -> {
						Pugmas25.get().poof(waystone.warpLoc);
					});
				});
		}
	}

}
