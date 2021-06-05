package me.pugabyte.nexus.features.delivery.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.delivery.DeliveryService;
import me.pugabyte.nexus.models.delivery.DeliveryUser;
import me.pugabyte.nexus.models.delivery.DeliveryUser.Delivery;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class ViewDeliveriesMenuProvider extends MenuUtils implements InventoryProvider {
	private final DeliveryService service = new DeliveryService();
	private final DeliveryUser user;
	private final WorldGroup worldGroup;

	public ViewDeliveriesMenuProvider(DeliveryUser user, WorldGroup worldGroup) {
		this.user = user;
		this.worldGroup = worldGroup;
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.size(6, 9)
				.title(colorize("&3Your Deliveries"))
				.build()
				.open(user.getOnlinePlayer(), page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> new DeliveryMenuProvider(user, worldGroup).open(player));

		ItemStack info = new ItemBuilder(Material.BOOK).name("&3Info")
				.lore("&eOpened deliveries cannot be closed",
						"&eAny items left over, will be",
						"&egiven to you, or dropped.")
				.loreize(false)
				.build();

		contents.set(0, 8, ClickableItem.empty(info));

		List<ClickableItem> items = new ArrayList<>();
		List<Delivery> deliveries = user.get(worldGroup);
		if (!Utils.isNullOrEmpty(deliveries)) {
			for (Delivery delivery : deliveries) {
				boolean hasMessage = !ItemUtils.isNullOrAir(delivery.getMessage());
				boolean hasItems = !Utils.isNullOrEmpty(delivery.getItems());

				List<String> lore = new ArrayList<>();

				if (hasMessage)
					lore.add("&e1 &7Message");

				if (hasItems) {
					int count = 1;
					int size = delivery.getItems().size();
					for (ItemStack item : delivery.getItems()) {
						lore.add("&e" + item.getAmount() + " &7" + StringUtils.camelCase(item.getType()));
						if (++count > 5) {
							lore.add("&7And " + (size - count) + " more");
							break;
						}
					}
				}

				ItemStack item = new ItemBuilder(Material.CHEST).name("&7From: &e" + delivery.getFrom()).lore(lore).build();
				items.add(ClickableItem.from(item, e -> {
					user.remove(worldGroup, delivery);
					service.save(user);
					new OpenDeliveryMenuProvider(user, worldGroup, delivery).open(player);
				}));
			}
		}

		addPagination(player, contents, items);
	}
}
