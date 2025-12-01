package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Waystone;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideConfig.ClientSideItemFrameModifier;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.DialogUtils.DialogBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

public class Pugmas25Waystones implements Listener {

	private final Pugmas25UserService userService = new Pugmas25UserService();

	public Pugmas25Waystones() {
		Nexus.registerListener(this);

		ClientSideConfig.registerItemFrameModifier(new ClientSideItemFrameModifier() {
			@Override
			public ItemStack modify(ClientSideUser user, ClientSideItemFrame itemFrame) {
				var waystone = Pugmas25Waystone.fromFrameLocation(itemFrame.getLocation());
				if (waystone == null)
					return null;

				var display = DecorationType.WAYSTONE;
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
			new Pugmas25WaystoneMenu(event.getPlayer(), waystone).open();
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

		new Pugmas25WaystoneMenu(event.getPlayer()).open();
	}

	@AllArgsConstructor
	public static class Pugmas25WaystoneMenu {
		private final Player player;
		private final Pugmas25Waystone clickedWayStone;

		public Pugmas25WaystoneMenu(Player player) {
			this.player = player;
			this.clickedWayStone = null;
		}

		public void open() {
			var dialog = new DialogBuilder()
				.title("Teleport to a waystone:")
				.bodyText("")
				.bodyText("")
				.multiAction()
				.columns(2);

			var user = new Pugmas25UserService().get(player);
			for (Pugmas25Waystone waystone : Pugmas25Waystone.values()) {
				if (!user.getFoundWaystones().contains(waystone)) {
					dialog.button("&8" + StringUtils.camelCase(waystone));
					continue;
				}

				if (clickedWayStone != null && waystone == clickedWayStone) {
					dialog.button("&8" + StringUtils.camelCase(waystone));
					continue;
				}

				dialog.button(StringUtils.camelCase(waystone), action -> teleport(waystone));
			}

			dialog.open(player);
		}

		private void teleport(Pugmas25Waystone waystone) {
			Pugmas25.get().fadeToBlack(player)
				.thenRun(() -> {
					Pugmas25.get().poof(player.getLocation());
					player.teleportAsync(waystone.getWarpLoc(), TeleportCause.PLUGIN).thenRun(() -> {
						Pugmas25.get().poof(waystone.getWarpLoc());
					});
				});
		}
	}

}
