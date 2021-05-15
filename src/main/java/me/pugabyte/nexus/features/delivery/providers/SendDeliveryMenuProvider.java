package me.pugabyte.nexus.features.delivery.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import joptsimple.internal.Strings;
import me.pugabyte.nexus.Nexus;
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
import static me.pugabyte.nexus.features.menus.SignMenuFactory.ARROWS;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class SendDeliveryMenuProvider extends MenuUtils implements InventoryProvider {
	private final DeliveryService service = new DeliveryService();
	private final DeliveryUser user;
	private final WorldGroup worldGroup;
	private UUID sendTo;
	private List<ItemStack> items;
	private String message;

	public SendDeliveryMenuProvider(DeliveryUser user, WorldGroup worldGroup) {
		this(user, worldGroup, null, null, null);
	}

	public SendDeliveryMenuProvider(DeliveryUser user, WorldGroup worldGroup, UUID sendTo, List<ItemStack> items, String message) {
		this.user = user;
		this.worldGroup = worldGroup;
		this.sendTo = sendTo;
		this.items = items;
		this.message = message;
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
			getMenu().close(player);
			if (!Utils.isNullOrEmpty(items))
				PlayerUtils.giveItems(player, items);
			new DeliveryMenuProvider(user, worldGroup).open(player);
		});

		ItemBuilder playerName = new ItemBuilder(Material.NAME_TAG).name("Insert Player Name");
		if (sendTo != null)
			playerName.name("Send To: " + PlayerUtils.getPlayer(sendTo).getName());
		//
		ItemBuilder insertItems = new ItemBuilder(Material.CHEST).name("Items To Send");
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
		//
		ItemBuilder typeMessage = new ItemBuilder(Material.WRITABLE_BOOK).name("Type Message").lore("Temporarily Disabled");
		ItemBuilder confirm = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("Send Delivery");
		if (sendTo != null && (!Utils.isNullOrEmpty(items) || !Strings.isNullOrEmpty(message)))
			confirm.material(Material.LIME_STAINED_GLASS_PANE);
		else if (sendTo == null)
			confirm.lore("&cRecipient not specified");
		else if (Utils.isNullOrEmpty(items) && Strings.isNullOrEmpty(message))
			confirm.lore("&cDelivery is empty");

		contents.set(1, 1, ClickableItem.from(playerName.build(), e -> {
			getMenu().close(player);
			Nexus.getSignMenuFactory().lines("", ARROWS, "Enter a", "player's name")
					.prefix(PREFIX)
					.response(lines -> {
						if (lines[0].length() > 0) {
							OfflinePlayer _player = PlayerUtils.getPlayer(lines[0]);
							if (!PlayerUtils.isSelf(player, _player))
								sendTo = _player.getUniqueId();
						}
						new SendDeliveryMenuProvider(user, worldGroup, sendTo, items, message).open(player);
					})
					.onError(() -> new SendDeliveryMenuProvider(user, worldGroup, sendTo, items, message).open(player))
					.open(player);
		}));
		contents.set(1, 3, ClickableItem.from(insertItems.build(), e -> {
			getMenu().close(player);
			new InsertItemsMenu(user, worldGroup, sendTo, items, message);
		}));

		// TODO: open a book menu where the player can type a message
		// TODO: on opening a delivery: if has message, set it as a written book given to the player, unless it's from the server
		contents.set(1, 4, ClickableItem.empty(typeMessage.build()));
		contents.set(1, 7, ClickableItem.from(confirm.build(), e -> {
			if (sendTo != null && (!Utils.isNullOrEmpty(items) || !Strings.isNullOrEmpty(message)))
				sendDelivery(player);
		}));
	}


	private void sendDelivery(Player player) {
		Delivery delivery = new Delivery(user.getUuid(), message, items);
		DeliveryUser toUser = service.get(sendTo);
		toUser.add(worldGroup, delivery);
		service.save(toUser);

		getMenu().close(player);

		toUser.sendNotification();
	}
}
