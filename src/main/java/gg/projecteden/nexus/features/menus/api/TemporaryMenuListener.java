package gg.projecteden.nexus.features.menus.api;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.common.TemporaryListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Nullables;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

public interface TemporaryMenuListener extends TemporaryListener {

	default String getTitle() {
		if (getClass().isAnnotationPresent(Title.class))
			return getClass().getAnnotation(Title.class).value();

		throw new InvalidInputException("Title not defined for " + getClass().getSimpleName());
	}

	default void open() {
		open(6);
	}

	default void open(int rows) {
		open(rows, Collections.emptyList());
	}

	default void open(int rows, List<ItemStack> contents) {
		final int slots = rows * 9;
		Inventory inv = Bukkit.createInventory(getInventoryHolder(), slots, colorize(getTitle()));
		if (!isNullOrEmpty(contents))
			inv.setContents(contents.subList(0, Math.min(contents.size(), slots)).toArray(ItemStack[]::new));

		if (getInventoryHolder() instanceof CustomInventoryHolder custom)
			custom.setInventory(inv);

		Nexus.registerTemporaryListener(this);
		getPlayer().openInventory(inv);
	}

	default void open(Inventory inv) {
		Nexus.registerTemporaryListener(this);
		getPlayer().openInventory(inv);
	}

	default void open(List<ItemStack> contents) {
		open(6, contents);
	}

	default <T extends InventoryHolder> T getInventoryHolder() {
		return null;
	}

	default boolean keepAirSlots() {
		return false;
	}

	@EventHandler
	default void onChestClose(InventoryCloseEvent event) {
		final InventoryHolder actualHolder = event.getInventory().getHolder();
		if (actualHolder != null) {
			final InventoryHolder expectedHolder = getInventoryHolder();
			if (expectedHolder == null || actualHolder.getClass() != expectedHolder.getClass())
				return;
		}

		if (!event.getPlayer().equals(getPlayer()))
			return;

		List<ItemStack> contents = Arrays.stream(event.getInventory().getContents())
				.filter(item -> keepAirSlots() || Nullables.isNotNullOrAir(item))
				.collect(Collectors.toList());

		Nexus.unregisterTemporaryListener(this);

		onClose(event, contents);
	}

	@Data
	abstract class CustomInventoryHolder implements InventoryHolder {
		private Inventory inventory;
	}

	void onClose(InventoryCloseEvent event, List<ItemStack> contents);

}
