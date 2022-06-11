package gg.projecteden.nexus.features.legacy;

import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.features.listeners.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.legacy.ItemTransferUser;
import gg.projecteden.nexus.models.legacy.ItemTransferUser.ReviewStatus;
import gg.projecteden.nexus.models.legacy.ItemTransferUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.Env;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Environments(Env.TEST)
@Permission(Group.STAFF)
public class LegacyCommand extends CustomCommand {
	public static final String PREFIX = StringUtils.getPrefix("Legacy");

	public LegacyCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("items transfer")
	void items_transfer() {
		// TODO 1.19 Only in legacy
		new ItemTransferMenu(player());
	}

	@Path("items pending")
	void items_pending() {
		new ItemPendingMenu(player()).open(player());
	}

	@Path("items review [player]")
	@Permission(Group.ADMIN)
	void items_review(ItemTransferUser user) {
		new ItemReviewMenu(user).open(player());
	}

	@Path("items receive")
	void items_receive() {
		new ItemReceiveMenu(player());
	}

	@Title("Legacy Item Transfer")
	public static class ItemTransferMenu implements TemporaryMenuListener {
		@Getter
		private final Player player;

		public ItemTransferMenu(Player player) {
			this.player = player;
			openMax();
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			new ItemTransferUserService().edit(player, user -> {
				user.getItems(ReviewStatus.PENDING).addAll(contents);
				user.sendMessage(PREFIX + "Successfully stored " + contents.stream().mapToInt(ItemStack::getAmount).sum() + " legacy items for staff review");
			});
		}

	}

	@Title("Pending Items")
	public static class ItemPendingMenu extends InventoryProvider {
		private final ItemTransferUserService service = new ItemTransferUserService();
		private final ItemTransferUser user;

		public ItemPendingMenu(Player player) {
			this.user = service.get(player);
		}

		@Override
		public void init() {
			addCloseItem();

			List<ClickableItem> items = new ArrayList<>();

			final boolean inLegacy = WorldGroup.of(player) == WorldGroup.LEGACY;

			for (ItemStack item : user.getItems(ReviewStatus.PENDING)) {
				if (inLegacy)
					items.add(ClickableItem.of(new ItemBuilder(item).lore("Click to cancel"), e -> {
						user.getItems(ReviewStatus.PENDING).remove(item);
						PlayerUtils.giveItem(user, item);
						refresh();
					}));
				else
					items.add(ClickableItem.empty(item));
			}

			paginator().items(items).build();
		}
	}

	@Title("Review Items")
	@RequiredArgsConstructor
	public static class ItemReviewMenu extends InventoryProvider {
		private final ItemTransferUserService service = new ItemTransferUserService();
		private final ItemTransferUser user;

		@Override
		public void init() {
			addCloseItem();

			List<ClickableItem> items = new ArrayList<>();

			contents.set(0, 3, ClickableItem.of(Material.RED_CONCRETE, "&cDeny All Items", e -> ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					user.denyAll();
					service.save(user);
					player.closeInventory();
				})
				.onCancel(e2 -> new ItemReviewMenu(user).open(player, contents.pagination().getPage()))
				.open(player)));

			contents.set(0, 5, ClickableItem.of(Material.LIME_CONCRETE, "&cAccept All Items", e -> ConfirmationMenu.builder()
				.onConfirm(e2 -> {
					user.acceptAll();
					service.save(user);
					player.closeInventory();
				})
				.onCancel(e2 -> new ItemReviewMenu(user).open(player, contents.pagination().getPage()))
				.open(player)));

			for (ItemStack item : user.getItems(ReviewStatus.PENDING))
				items.add(ClickableItem.of(item, e ->
					new ItemReviewSubMenu(user, item, contents.pagination().getPage()).open(player)));

			paginator().items(items).build();
		}

	}

	@Title("Review Item")
	@RequiredArgsConstructor
	public static class ItemReviewSubMenu extends InventoryProvider {
		private final ItemTransferUserService service = new ItemTransferUserService();
		private final ItemTransferUser user;
		private final ItemStack item;
		private final int parentPage;

		@Override
		public void init() {
			addBackItem(e -> new ItemReviewMenu(user).open(player, parentPage));

			contents.set(1, 3, ClickableItem.of(Material.RED_CONCRETE, "&cDeny Item", e -> {
				user.deny(item);
				service.save(user);
				new ItemReviewMenu(user).open(player, parentPage);
			}));

			contents.set(1, 5, ClickableItem.of(Material.LIME_CONCRETE, "&cAccept Item", e -> {
				user.accept(item);
				service.save(user);
				new ItemReviewMenu(user).open(player, parentPage);
			}));
		}

	}

	@Title("Receive Items")
	public static class ItemReceiveMenu implements TemporaryMenuListener {
		@Getter
		private final Player player;
		private final ItemTransferUserService service = new ItemTransferUserService();

		public ItemReceiveMenu(Player player) {
			this.player = player;

			if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
				throw new InvalidInputException("You must be in the survival world to receive your items");

			final ItemTransferUser user = service.get(player);
			// TODO Receive denied items back in legacy world?
			final ReviewStatus status = ReviewStatus.ACCEPTED;

			if (user.getItems(status).isEmpty())
				throw new InvalidInputException("No " + status.name().toLowerCase() + " items available, " +
					(user.getItems(ReviewStatus.PENDING).isEmpty() ? "add them with '/legacy items transfer' in the legacy worlds" :
						"please wait for the staff team to reivew your items" ));

			final List<ItemStack> contents = new ArrayList<>(user.getItems(status).subList(0, Math.min(user.getItems(status).size(), 4 * 9)));

			user.getItems(status).removeAll(contents);
			service.save(user);

			openMax(contents);

			service.save(user);
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			service.edit(player, user -> user.getItems(ReviewStatus.ACCEPTED).addAll(contents));
		}
	}

	@EventHandler
	public void on(InventoryClickEvent event) {
		// Ability to open shulker boxes without placing them --> probably editable for item transfer
	}


	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (true) return; // TODO 1.19

		if (WorldGroup.of(event.getPlayer()) != WorldGroup.LEGACY)
			return;

		if (!(event.getRightClicked() instanceof ItemFrame itemFrame))
			return;

		final ItemStack item = itemFrame.getItem();
		if (isNullOrAir(item))
			return;

		if (item.getType() != Material.WRITTEN_BOOK && item.getType() != Material.WRITABLE_BOOK)
			return;

		event.getPlayer().openBook(item);
	}

}
