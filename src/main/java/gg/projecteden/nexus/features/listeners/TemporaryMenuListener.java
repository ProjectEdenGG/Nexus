package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Utils;
import org.apache.commons.lang.ObjectUtils.Null;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.utils.Nullables.isNullOrEmpty;

public interface TemporaryMenuListener extends TemporaryListener {

	String getTitle();

	default void open() {
		open(3);
	}

	default void open(int rows) {
		open(rows, Collections.emptyList());
	}

	default void open(int rows, List<ItemStack> contents) {
		final int slots = rows * 9;
		Inventory inv = Bukkit.createInventory(null, slots, colorize(getTitle()));
		if (!isNullOrEmpty(contents))
			inv.setContents(contents.subList(0, Math.min(contents.size(), slots)).toArray(ItemStack[]::new));

		Nexus.registerTemporaryListener(this);
		getPlayer().openInventory(inv);
	}

	@EventHandler
	default void onChestClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() != null) return;
		if (!Utils.equalsInvViewTitle(event.getView(), colorize(getTitle()))) return;
		if (!event.getPlayer().equals(getPlayer())) return;

		List<ItemStack> contents = Arrays.stream(event.getInventory().getContents())
				.filter(Nullables::isNotNullOrAir)
				.collect(Collectors.toList());

		onClose(event, contents);

		Nexus.unregisterTemporaryListener(this);
	}

	void onClose(InventoryCloseEvent event, List<ItemStack> contents);

}
