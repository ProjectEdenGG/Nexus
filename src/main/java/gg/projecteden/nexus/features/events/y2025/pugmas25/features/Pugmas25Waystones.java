package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Waystone;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideConfig.ClientSideItemFrameModifier;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Pugmas25Waystones implements Listener {

	private final Pugmas25UserService userService = new Pugmas25UserService();

	public Pugmas25Waystones() {
		Nexus.registerListener(this);

		ClientSideConfig.registerItemFrameModifier(new ClientSideItemFrameModifier() {
			@Override
			public ItemStack modify(ClientSideUser user, ClientSideItemFrame itemFrame) {
				Pugmas25Waystone waystone = Pugmas25Waystone.fromFrameLocation(itemFrame.getLocation());
				if (waystone == null)
					return itemFrame.content();

				DecorationType display = DecorationType.WAYSTONE;
				var pugmasUser = new Pugmas25UserService().get(user);
				if (pugmasUser.getFoundWaystones().contains(waystone))
					display = DecorationType.WAYSTONE_ACTIVATED;

				return display.getConfig().getItemBuilder().resetName().build();
			}
		});
	}

	@EventHandler
	public void on(DecorationInteractEvent event) {
		if (!Pugmas25.get().shouldHandle(event.getPlayer()))
			return;

		if (event.getDecorationType() != DecorationType.WAYSTONE_ACTIVATED && event.getDecorationType() != DecorationType.WAYSTONE)
			return;

		Pugmas25Waystone waystone = Pugmas25Waystone.fromFrameLocation(event.getDecoration().getClientsideLocation());
		if (waystone == null)
			return;

		Pugmas25User pugmasUser = userService.get(event.getPlayer());
		if (pugmasUser.getFoundWaystones().contains(waystone)) {
			new Pugmas25WaystoneMenu(waystone).open(event.getPlayer());
			return;
		}

		pugmasUser.unlockWaystone(waystone);
		userService.save(pugmasUser);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (!Pugmas25.get().shouldHandle(event.getPlayer()))
			return;

		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item))
			return;

		if (!Pugmas25QuestItem.MAGIC_MIRROR.fuzzyMatch(item) && !Pugmas25QuestItem.PDA.fuzzyMatch(item))
			return;

		new Pugmas25WaystoneMenu().open(event.getPlayer());
	}

	@Rows(3)
	@Title("Teleport to a waystone:")
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Pugmas25WaystoneMenu extends InventoryProvider {
		@Nullable
		private Pugmas25Waystone clickedWayStone;

		@Override
		public void init() {
			addCloseItem();

			List<ClickableItem> items = new ArrayList<>();
			Pugmas25User user = new Pugmas25UserService().get(viewer);
			for (Pugmas25Waystone waystone : user.getFoundWaystones()) {
				if (clickedWayStone != null && waystone == clickedWayStone)
					continue;

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
					viewer.teleportAsync(waystone.getWarpLoc(), TeleportCause.PLUGIN).thenRun(() -> {
						Pugmas25.get().poof(waystone.getWarpLoc());
					});
				});
		}
	}

}
