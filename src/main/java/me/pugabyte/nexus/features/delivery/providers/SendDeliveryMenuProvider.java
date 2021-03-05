package me.pugabyte.nexus.features.delivery.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.delivery.DeliveryMenu;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.delivery.DeliveryService;
import me.pugabyte.nexus.models.delivery.DeliveryUser;
import me.pugabyte.nexus.models.delivery.DeliveryUser.Delivery;
import me.pugabyte.nexus.utils.ItemBuilder;
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

public class SendDeliveryMenuProvider extends MenuUtils implements InventoryProvider {
	private final DeliveryService service = new DeliveryService();
	private final DeliveryUser user;
	private final WorldGroup worldGroup;
	UUID sendTo;
	List<ItemStack> items;
	String message;


	public SendDeliveryMenuProvider(DeliveryUser user, WorldGroup worldGroup, UUID sendTo, List<ItemStack> items, String message) {
		this.user = user;
		this.worldGroup = worldGroup;
		this.sendTo = sendTo;
		this.items = items;
		this.message = message;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> DeliveryMenu.open(user, worldGroup));

		ItemBuilder playerName = new ItemBuilder(Material.NAME_TAG).name("Insert Player Name");
		ItemBuilder insertItems = new ItemBuilder(Material.CHEST).name("Items To Send");
		ItemBuilder typeMessage = new ItemBuilder(Material.WRITABLE_BOOK).name("Type Message").lore("Temporarily Disabled");

		if (sendTo != null)
			playerName.name("Send To: " + PlayerUtils.getPlayer(sendTo).getName());

		List<String> lore = new ArrayList<>();
		if (message != null)
			lore.add("&e1 &7Message");

		if (!Utils.isNullOrEmpty(items)) {
			int count = 1;
			int size = items.size();
			for (ItemStack item : items) {
				lore.add("&e" + item.getAmount() + " &7" + StringUtils.camelCase(item.getType()));
				if (++count > 5) {
					lore.add("&7And " + (size - count) + " more");
					break;
				}
			}
		}

		if (!lore.isEmpty())
			insertItems.lore(lore);


		ItemStack confirm = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("Send Delivery").build();

		contents.set(1, 1, ClickableItem.from(playerName.build(), e ->
				Nexus.getSignMenuFactory().lines("", "^ ^ ^ ^ ^ ^", "Enter a", "player's name")
						.prefix(PREFIX)
						.response(lines -> {
							if (lines[0].length() > 0) {
								OfflinePlayer _player = PlayerUtils.getPlayer(lines[0]);
								sendTo = _player.getUniqueId();
							}
							DeliveryMenu.sendDelivery(user, worldGroup, sendTo, items, message);
						})
						.onError((lines, ex) -> DeliveryMenu.sendDelivery(user, worldGroup, sendTo, items, message))
						.open(player)));
		contents.set(1, 3, ClickableItem.from(insertItems.build(), e -> new InsertItemsMenu(user, worldGroup, sendTo, items, message)));
		// TODO: open a book menu where the player can type a message
		// TODO: on opening a delivery: if has message, set it as a written book given to the player, unless it's from the server
		contents.set(1, 4, ClickableItem.empty(typeMessage.build()));
		contents.set(1, 7, ClickableItem.from(confirm, e -> {
			if (sendTo == null) {
				PlayerUtils.send(player, PREFIX + "You did not specify a recipient");
				return;
			}
			sendDelivery(player);
		}));
	}


	private void sendDelivery(Player player) {
		Delivery delivery = new Delivery(user.getUuid(), message, items);
		DeliveryUser toUser = service.get(sendTo);
		toUser.add(worldGroup, delivery);
		service.save(toUser);

		player.closeInventory();

		toUser.sendNotification();
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
