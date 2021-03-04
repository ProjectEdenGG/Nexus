package me.pugabyte.nexus.features.delivery.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.delivery.DeliveryMenu;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.delivery.DeliveryUser;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DeliveryMenuProvider extends MenuUtils implements InventoryProvider {
	private final DeliveryUser user;
	private final WorldGroup worldGroup;

	public DeliveryMenuProvider(DeliveryUser user, WorldGroup worldGroup) {
		this.user = user;
		this.worldGroup = worldGroup;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents);

		ItemStack sendDelivery = new ItemBuilder(Material.SHULKER_BOX).name("Send A Delivery").build();
		ItemStack viewDeliveries = new ItemBuilder(Material.CHEST).name("View Deliveries").build();

		contents.set(1, 2, ClickableItem.from(sendDelivery, e -> DeliveryMenu.sendDelivery(user, worldGroup)));
		contents.set(1, 6, ClickableItem.from(viewDeliveries, e -> DeliveryMenu.viewDeliveries(user, worldGroup)));
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
