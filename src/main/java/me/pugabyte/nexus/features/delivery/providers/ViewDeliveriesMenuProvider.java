package me.pugabyte.nexus.features.delivery.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.pugabyte.nexus.features.delivery.DeliveryMenu;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.delivery.DeliveryService;
import me.pugabyte.nexus.models.delivery.DeliveryUser;
import me.pugabyte.nexus.models.delivery.DeliveryUser.Delivery;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ViewDeliveriesMenuProvider extends MenuUtils implements InventoryProvider {
	private final DeliveryService service = new DeliveryService();
	private final DeliveryUser user;
	private final WorldGroup worldGroup;

	public ViewDeliveriesMenuProvider(DeliveryUser user, WorldGroup worldGroup) {
		this.user = user;
		this.worldGroup = worldGroup;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> DeliveryMenu.open(user, worldGroup));

		ItemStack info = new ItemBuilder(Material.BOOK).name("&3Info")
				.lore("&eOpened deliveries cannot be closed",
						"&eAny items left over, will be",
						"&egiven to you, or dropped.")
				.loreize(false)
				.build();

		contents.set(0, 8, ClickableItem.empty(info));

		Pagination page = contents.pagination();
		List<ClickableItem> items = new ArrayList<>();
		List<Delivery> deliveries = user.getDeliveries().get(worldGroup);
		if (!Utils.isNullOrEmpty(deliveries)) {
			for (Delivery delivery : deliveries) {
				List<String> lore = new ArrayList<>();
				if (delivery.getMessage() != null)
					lore.add("&e1 &7Message");

				if (!Utils.isNullOrEmpty(delivery.getItems())) {
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
					DeliveryMenu.openDelivery(user, worldGroup, delivery);
				}));
			}
		}

		page.setItems(items.toArray(new ClickableItem[0]));
		page.setItemsPerPage(36);
		page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

		// Arrows
		if (!page.isFirst())
			contents.set(5, 0, ClickableItem.from(new ItemBuilder(Material.ARROW).name("<-- Back").build(), e ->
					DeliveryMenu.viewDeliveries(user, worldGroup, page.previous().getPage())));
		if (!page.isLast())
			contents.set(5, 8, ClickableItem.from(new ItemBuilder(Material.ARROW).name("Next -->").build(), e ->
					DeliveryMenu.viewDeliveries(user, worldGroup, page.next().getPage())));
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
