package me.pugabyte.bncore.features.delivery;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.AllArgsConstructor;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.delivery.Delivery;
import me.pugabyte.bncore.models.delivery.DeliveryService;
import me.pugabyte.bncore.utils.ItemUtils;
import me.pugabyte.bncore.utils.WorldGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DeliveryMenu extends MenuUtils implements InventoryProvider {
	private final DeliveryService service = new DeliveryService();
	private final Delivery delivery;
	private final WorldGroup worldGroup;

	public SmartInventory getInv() {
		List<ItemStack> items = delivery.get(worldGroup);

		int rows = (int) (Math.ceil(items.size() / 9.0) + 1);
		rows = Math.min(Math.max(rows, 3), 6);

		return SmartInventory.builder()
				.provider(new DeliveryMenu(delivery, worldGroup))
				.size(rows, 9)
				.title(ChatColor.DARK_AQUA + "Deliveries")
				.build();
	}

	public void open(Player player) {
		open(player, 0);
	}

	@Override
	public void open(Player viewer, int page) {
		getInv().open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents);

		List<ItemStack> items = delivery.get(worldGroup);
		items.removeIf(ItemUtils::isNullOrAir);

		List<ClickableItem> clickableItems = new ArrayList<>();
		for (ItemStack itemStack : items)
			clickableItems.add(ClickableItem.from(itemStack, e -> claimDelivery(itemStack)));

		addPagination(player, contents, clickableItems);
	}

	public void claimDelivery(ItemStack item) {
		delivery.deliver(item, worldGroup);
		service.save(delivery);

		open(delivery.getPlayer());
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
