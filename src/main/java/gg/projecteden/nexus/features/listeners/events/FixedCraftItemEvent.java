package gg.projecteden.nexus.features.listeners.events;

import lombok.Getter;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

@Getter
public class FixedCraftItemEvent extends CraftItemEvent {
	private final ItemStack resultItemStack;

	public FixedCraftItemEvent(@NotNull ItemStack resultItemStack, @NotNull Recipe recipe, @NotNull InventoryView what, @NotNull InventoryType.SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action) {
		super(recipe, what, type, slot, click, action);
		this.resultItemStack = resultItemStack;
	}

	public FixedCraftItemEvent(@NotNull ItemStack resultItemStack, @NotNull Recipe recipe, @NotNull InventoryView what, @NotNull InventoryType.SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action, int key) {
		super(recipe, what, type, slot, click, action, key);
		this.resultItemStack = resultItemStack;
	}

}
