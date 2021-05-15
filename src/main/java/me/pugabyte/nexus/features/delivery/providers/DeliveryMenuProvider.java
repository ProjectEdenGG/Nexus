package me.pugabyte.nexus.features.delivery.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.delivery.DeliveryUser;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class DeliveryMenuProvider extends MenuUtils implements InventoryProvider {
	private final DeliveryUser user;
	private final WorldGroup worldGroup;

	public DeliveryMenuProvider(DeliveryUser user, WorldGroup worldGroup) {
		this.user = user;
		this.worldGroup = worldGroup;
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.size(3, 9)
				.title(colorize("&3Deliveries"))
				.build().open(user.getOnlinePlayer());
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addCloseItem(contents);

		ItemStack sendDelivery = new ItemBuilder(Material.SHULKER_BOX).name("Send A Delivery").build();
		ItemStack viewDeliveries = new ItemBuilder(Material.CHEST).name("View Deliveries").build();

		contents.set(1, 2, ClickableItem.from(sendDelivery, e -> new SendDeliveryMenuProvider(user, worldGroup).open(player)));
		contents.set(1, 6, ClickableItem.from(viewDeliveries, e -> new ViewDeliveriesMenuProvider(user, worldGroup).open(player)));
	}
}
