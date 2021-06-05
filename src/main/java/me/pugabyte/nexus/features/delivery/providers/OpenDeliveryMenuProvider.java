package me.pugabyte.nexus.features.delivery.providers;

import eden.utils.Utils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import lombok.Getter;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.delivery.DeliveryUser;
import me.pugabyte.nexus.models.delivery.DeliveryUser.Delivery;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

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
	public void open(Player viewer) {
		SmartInventory.builder()
				.provider(this)
				.size(4, 9)
				.title(colorize("&3From: &e" + delivery.getFrom()))
				.build()
				.open(user.getOnlinePlayer());
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> {
			ItemStack[] menuContents = player.getOpenInventory().getTopInventory().getContents();
			for (ItemStack item : Arrays.copyOfRange(menuContents, 9, menuContents.length))
				PlayerUtils.giveItem(player, item);
			new ViewDeliveriesMenuProvider(user, worldGroup).open(player);
		});

		ItemStack info = new ItemBuilder(Material.BOOK).name("&3Info")
				.lore("&eOpened deliveries cannot be closed",
						"&eAny items left over, will be",
						"&egiven to you, or dropped.")
				.loreize(false)
				.build();

		contents.set(0, 8, ClickableItem.empty(info));

		final Iterator<ItemStack> iterator = new ArrayList<ItemStack>() {{
			if (!isNullOrAir(delivery.getMessage()))
				add(delivery.getMessage());

			if (!Utils.isNullOrEmpty(delivery.getItems()))
				addAll(delivery.getItems());
		}}.iterator();

		for (int row = 1; row <= 4; row++) {
			for (int col = 0; col <= 8; col++) {
				SlotPos slotpos = new SlotPos(row, col);
				if (iterator.hasNext()) {
					ItemStack item = iterator.next();
					if (!isNullOrAir(item))
						contents.set(slotpos, ClickableItem.empty(item));
				}

				contents.setEditable(slotpos, true);
			}
		}
	}
}
