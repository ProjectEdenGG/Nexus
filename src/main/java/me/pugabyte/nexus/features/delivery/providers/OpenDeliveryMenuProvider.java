package me.pugabyte.nexus.features.delivery.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import lombok.Getter;
import me.pugabyte.nexus.features.delivery.DeliveryMenu;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.delivery.DeliveryUser;
import me.pugabyte.nexus.models.delivery.DeliveryUser.Delivery;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OpenDeliveryMenuProvider extends MenuUtils implements InventoryProvider {
	private final DeliveryUser user;
	private final WorldGroup worldGroup;
	@Getter
	private final Delivery delivery;

	public OpenDeliveryMenuProvider(DeliveryUser user, WorldGroup worldGroup, Delivery delivery) {
		this.user = user;
		this.worldGroup = worldGroup;
		this.delivery = delivery;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> {
			for (ItemStack item : player.getOpenInventory().getTopInventory().getContents()) {
				if (delivery.getItems().contains(item))
					ItemUtils.giveItem(player, item);
			}
			DeliveryMenu.viewDeliveries(user, worldGroup);
		});

		ItemStack info = new ItemBuilder(Material.BOOK).name("&3Info")
				.lore("&eOpened deliveries cannot be closed",
						"&eAny items left over, will be",
						"&egiven to you, or dropped.")
				.loreize(false)
				.build();

		contents.set(0, 8, ClickableItem.empty(info));

		ItemStack[] items = delivery.getItems().toArray(new ItemStack[0]);
		int i = 0;
		for (int row = 1; row <= 4; row++) {
			for (int col = 0; col <= 8; col++) {
				SlotPos slotpos = new SlotPos(row, col);
				if (i < items.length) {
					ItemStack item = items[i++];
					if (!ItemUtils.isNullOrAir(item))
						contents.set(slotpos, ClickableItem.empty(item));
				}

				contents.setEditable(slotpos, true);
			}
		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
