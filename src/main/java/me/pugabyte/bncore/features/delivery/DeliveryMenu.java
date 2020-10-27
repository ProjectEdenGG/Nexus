package me.pugabyte.bncore.features.delivery;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import lombok.AllArgsConstructor;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.delivery.Delivery;
import me.pugabyte.bncore.models.delivery.DeliveryService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class DeliveryMenu extends MenuUtils implements InventoryProvider {
	private final DeliveryService service = new DeliveryService();
	private final Delivery delivery;
	private final WorldGroup worldGroup;

	public SmartInventory getInv() {
		List<ItemStack> items = worldGroup.equals(WorldGroup.SURVIVAL) ? delivery.getSurvivalItems() : delivery.getSkyblockItems();

		int rows = (int) (Math.ceil(items.size() / 9.0) + 1);
		rows = Math.min(Math.max(rows, 3), 6);


		return SmartInventory.builder()
				.provider(new DeliveryMenu(delivery, worldGroup))
				.size(rows, 9)
				.title(ChatColor.DARK_AQUA + "Deliveries")
				.build();
	}

	public void open(Player player) {
		getInv().open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		List<ItemStack> items = worldGroup.equals(WorldGroup.SURVIVAL) ? delivery.getSurvivalItems() : delivery.getSkyblockItems();

		addCloseItem(contents);
		Pagination page = contents.pagination();

		for (int i = 0; i < items.size(); i++) {
			ItemStack itemStack = items.get(i);
			if (Utils.isNullOrAir(itemStack))
				items.remove(itemStack);
		}

		ClickableItem[] clickableItems = new ClickableItem[items.size()];
		for (int i = 0; i < items.size(); i++) {
			ItemStack itemStack = items.get(i);
			clickableItems[i] = ClickableItem.from(itemStack, e -> claimDelivery(itemStack, items, worldGroup));
		}

		page.setItems(clickableItems);
		page.setItemsPerPage(36);
		page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

		if (!page.isFirst())
			contents.set(5, 0, ClickableItem.from(new ItemBuilder(Material.ARROW).name("<-- Back").build(),
					e -> getInv().open(player, page.previous().getPage())));
		if (!page.isLast())
			contents.set(5, 8, ClickableItem.from(new ItemBuilder(Material.ARROW).name("Next -->").build(),
					e -> getInv().open(player, page.next().getPage())));
	}

	public void claimDelivery(ItemStack item, List<ItemStack> items, WorldGroup worldGroup) {
		Player player = delivery.getPlayer();

		switch (worldGroup) {
			case SURVIVAL:
				items.remove(item);
				delivery.setSurvivalItems(items);
				break;
			case SKYBLOCK:
				items.remove(item);
				delivery.setSkyblockItems(items);
				break;
			default:
				return;
		}

		player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1F, 1F);
		Utils.giveItem(player, item);
		service.save(delivery);

		open(player);
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
