package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks;
import gg.projecteden.nexus.utils.Nullables;
import lombok.AllArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class ItemConverter implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		handle(event.getPlayer().getInventory(), event.getPlayer().getWorld());
	}

	@EventHandler
	public void onOpen(InventoryOpenEvent event) {
		handle(event.getInventory(), event.getPlayer().getWorld());
	}

	private void handle(Inventory inventory, World world) {
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);

			if (Nullables.isNullOrAir(item)) continue;

			for (Converter converter : Converter.values()) {
				if (!converter.isOldItem.test(item)) continue;
				inventory.setItem(i, converter.function.apply(item, world));
			}
		}
	}

	@AllArgsConstructor
	public enum Converter {
		BACKPACK(Backpacks::isOldBackpack, Backpacks::convertOldToNew);

		Predicate<ItemStack> isOldItem;
		BiFunction<ItemStack, World, ItemStack> function;
	}

}
