package me.pugabyte.nexus.features.delivery;

import fr.minuskube.inv.SmartInventory;
import lombok.AllArgsConstructor;
import me.pugabyte.nexus.features.delivery.providers.DeliveryMenuProvider;
import me.pugabyte.nexus.features.delivery.providers.OpenDeliveryMenuProvider;
import me.pugabyte.nexus.features.delivery.providers.SendDeliveryMenuProvider;
import me.pugabyte.nexus.features.delivery.providers.ViewDeliveriesMenuProvider;
import me.pugabyte.nexus.models.delivery.DeliveryUser;
import me.pugabyte.nexus.models.delivery.DeliveryUser.Delivery;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@AllArgsConstructor
public class DeliveryMenu {

	public static void open(DeliveryUser user, WorldGroup worldGroup) {
		SmartInventory.builder()
				.provider(new DeliveryMenuProvider(user, worldGroup))
				.size(3, 9)
				.title(colorize("&3Deliveries"))
				.build().open(user.getPlayer());
	}

	public static void sendDelivery(DeliveryUser user, WorldGroup worldGroup) {
		sendDelivery(user, worldGroup, null, null, null);
	}

	public static void sendDelivery(DeliveryUser user, WorldGroup worldGroup, UUID sendTo, List<ItemStack> items, String message) {
		SmartInventory.builder()
				.provider(new SendDeliveryMenuProvider(user, worldGroup, sendTo, items, message))
				.size(3, 9)
				.title(colorize("&3Send A Delivery"))
				.build().open(user.getPlayer());
	}

	public static void viewDeliveries(DeliveryUser user, WorldGroup worldGroup, int page) {
		SmartInventory.builder()
				.provider(new ViewDeliveriesMenuProvider(user, worldGroup))
				.size(6, 9)
				.title(colorize("&3Your Deliveries"))
				.build().open(user.getPlayer(), page);
	}

	public static void viewDeliveries(DeliveryUser user, WorldGroup worldGroup) {
		viewDeliveries(user, worldGroup, 0);
	}

	public static void openDelivery(DeliveryUser user, WorldGroup worldGroup, Delivery delivery) {
		SmartInventory.builder()
				.provider(new OpenDeliveryMenuProvider(user, worldGroup, delivery))
				.size(4, 9)
				.title(colorize("&3From: &e" + delivery.getFrom()))
				.build().open(user.getPlayer());
	}
}
