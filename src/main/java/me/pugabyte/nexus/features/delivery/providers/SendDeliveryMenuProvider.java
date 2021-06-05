package me.pugabyte.nexus.features.delivery.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.delivery.DeliveryService;
import me.pugabyte.nexus.models.delivery.DeliveryUser;
import me.pugabyte.nexus.models.delivery.DeliveryUser.Delivery;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.pugabyte.nexus.features.delivery.DeliveryCommand.PREFIX;
import static me.pugabyte.nexus.features.menus.SignMenuFactory.ARROWS;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class SendDeliveryMenuProvider extends MenuUtils implements InventoryProvider {
	final DeliveryService service = new DeliveryService();
	final DeliveryUser user;
	final WorldGroup worldGroup;
	UUID sendTo;
	List<ItemStack> items;
	ItemStack message;

	public SendDeliveryMenuProvider(DeliveryUser user, WorldGroup worldGroup) {
		this.user = user;
		this.worldGroup = worldGroup;
	}

	@Override
	public void open(Player viewer, int page) {
		getMenu().open(user.getOnlinePlayer());
	}

	private SmartInventory getMenu() {
		return SmartInventory.builder()
				.provider(this)
				.size(3, 9)
				.title(colorize("&3Send A Delivery"))
				.closeable(false)
				.build();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> {
			new DeliveryMenuProvider(user, worldGroup).open(player);
			if (!Utils.isNullOrEmpty(items))
				PlayerUtils.giveItems(player, items);
		});

		ItemBuilder buttonPlayer = new ItemBuilder(Material.NAME_TAG).name("Insert Player Name");
		ItemBuilder buttonItems = new ItemBuilder(Material.CHEST).name("Items To Send");
		ItemBuilder buttonMessage = new ItemBuilder(Material.WRITABLE_BOOK).name("Message");
		ItemBuilder buttonConfirm = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("Send Delivery");

		if (sendTo != null)
			buttonPlayer.name("To: " + PlayerUtils.getPlayer(sendTo).getName());

		List<String> confirmLore = new ArrayList<>();
		if (message != null) {
			buttonMessage.lore("&e1 &7Message");
			confirmLore.add("&e1 &7Message");
		}

		if (!Utils.isNullOrEmpty(items)) {
			int count = 0;
			for (ItemStack item : items) {
				if (++count > 5) {
					confirmLore.add("&7And " + (items.size() - 5) + " more...");
					break;
				}

				confirmLore.add("&e" + item.getAmount() + " &7" + StringUtils.camelCase(item.getType()));
			}
		}

		if (!confirmLore.isEmpty())
			buttonItems.lore(confirmLore);

		if (sendTo == null)
			buttonConfirm.lore("&cRecipient not specified");
		else if (Utils.isNullOrEmpty(items) && ItemUtils.isNullOrAir(message))
			buttonConfirm.lore("&cDelivery is empty");
		else
			buttonConfirm.material(Material.LIME_STAINED_GLASS_PANE);

		contents.set(1, 1, ClickableItem.from(buttonPlayer.build(), e ->
				Nexus.getSignMenuFactory().lines("", ARROWS, "Enter a", "player's name")
						.prefix(PREFIX)
						.response(lines -> {
							if (lines[0].length() > 0) {
								OfflinePlayer _player = PlayerUtils.getPlayer(lines[0]);
								if (!PlayerUtils.isSelf(player, _player))
									sendTo = _player.getUniqueId();
							}
							open(player);
						})
						.onError(() -> open(player))
						.open(player)));

		contents.set(1, 3, ClickableItem.from(buttonItems.build(), e -> {
			getMenu().close(player);
			new InsertItemsMenu(this);
		}));

		contents.set(1, 4, ClickableItem.from(buttonMessage.build(), e -> {
			getMenu().close(player);
			new MailListener(this);
		}));

		contents.set(1, 7, ClickableItem.from(buttonConfirm.build(), e -> {
			if (buttonConfirm.get().getType() == Material.LIME_STAINED_GLASS_PANE)
				sendDelivery(player);
		}));
	}

	private void sendDelivery(Player player) {
		Delivery delivery = new Delivery(user.getUuid(), message, items);
		DeliveryUser toUser = service.get(sendTo);
		toUser.add(worldGroup, delivery);
		service.save(toUser);

		getMenu().close(player);

		final String contents = String.join(" &3and ", new ArrayList<String>() {{
			if (delivery.hasMessage()) add("&ea message");
			if (delivery.hasItems()) add("&e" + delivery.getItems().size() + " items");
		}});

		PlayerUtils.send(player, PREFIX + "Successfully sent " + contents + " &3to &e" + toUser.getNickname());
		toUser.sendNotification();
	}
}
