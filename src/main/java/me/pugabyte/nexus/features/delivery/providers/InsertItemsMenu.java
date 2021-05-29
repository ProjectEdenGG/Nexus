package me.pugabyte.nexus.features.delivery.providers;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.delivery.DeliveryUser;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InsertItemsMenu implements Listener {
	private final static String TITLE = StringUtils.colorize("&eInsert Items");
	UUID sendTo;
	List<ItemStack> items;
	String message;
	private final WorldGroup worldGroup;
	private final DeliveryUser user;

	public InsertItemsMenu(DeliveryUser user, WorldGroup worldGroup, UUID sendTo, List<ItemStack> items, String message) {
		this.user = user;
		this.worldGroup = worldGroup;
		this.sendTo = sendTo;
		this.items = items;
		this.message = message;

		Inventory inv = Bukkit.createInventory(null, 27, TITLE);
		if (!Utils.isNullOrEmpty(items))
			inv.setContents(items.toArray(ItemStack[]::new));

		Nexus.registerTempListener(this);
		user.getOnlinePlayer().openInventory(inv);
	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() != null) return;
		if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;
		if (!event.getPlayer().equals(user.getOnlinePlayer())) return;

		items = Arrays.stream(event.getInventory().getContents())
				.filter(item -> !ItemUtils.isNullOrAir(item)).collect(Collectors.toList());

		Nexus.unregisterTempListener(this);
		event.getPlayer().closeInventory();
		Tasks.wait(1, () -> new SendDeliveryMenuProvider(user, worldGroup, sendTo, items, message).open(user.getOnlinePlayer()));
	}
}
