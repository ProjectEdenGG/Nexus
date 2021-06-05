package me.pugabyte.nexus.features.delivery.providers;

import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.listeners.TemporaryListener;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

public class InsertItemsMenu implements TemporaryListener {
	private final static String TITLE = StringUtils.colorize("&eInsert Items");
	private final SendDeliveryMenuProvider menu;
	@Getter
	private final Player player;

	public InsertItemsMenu(SendDeliveryMenuProvider menu) {
		this.menu = menu;
		this.player = menu.user.getOnlinePlayer();

		Inventory inv = Bukkit.createInventory(null, 27, TITLE);
		if (!Utils.isNullOrEmpty(menu.items))
			inv.setContents(menu.items.toArray(ItemStack[]::new));

		Nexus.registerTemporaryListener(this);
		player.openInventory(inv);
	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() != null) return;
		if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;
		if (!event.getPlayer().equals(player)) return;

		menu.items = Arrays.stream(event.getInventory().getContents())
				.filter(item -> !ItemUtils.isNullOrAir(item)).collect(Collectors.toList());

		Nexus.unregisterTemporaryListener(this);
		event.getPlayer().closeInventory();
		Tasks.wait(1, () -> menu.open(player));
	}
}
