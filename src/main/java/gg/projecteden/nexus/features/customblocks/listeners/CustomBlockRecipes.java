package gg.projecteden.nexus.features.customblocks.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.common.ICraftable;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class CustomBlockRecipes implements Listener {

	public CustomBlockRecipes() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent ignored) {
		for (CustomBlock customBlock : CustomBlock.values())
			customBlock.registerRecipes();
	}

	@EventHandler
	public void on(PrepareItemCraftEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		ItemStack result = event.getInventory().getResult();
		if (Nullables.isNullOrAir(result))
			return;

		CustomBlock customBlock = CustomBlock.fromItemstack(result);
		if (CustomBlock.NOTE_BLOCK == customBlock)
			event.getInventory().setResult(customBlock.get().getItemStack());

		unlockRecipe(player, result);
	}

	@EventHandler
	public void on(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		unlockRecipe(player, event.getItem().getItemStack());
	}

	@EventHandler
	public void on(InventoryClickEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		final Inventory inventory = event.getClickedInventory();
		if (inventory == null || inventory.getType() == InventoryType.PLAYER)
			return;

		final ItemStack item = player.getItemOnCursor();
		if (isNullOrAir(item))
			return;

		unlockRecipe(player, item);
	}

	private static void unlockRecipe(Player player, ItemStack itemStack) {
		if (Nullables.isNullOrAir(itemStack))
			return;

		int modelData = CustomModelData.of(itemStack);
		CustomBlock _customBlock = CustomBlock.fromItemstack(itemStack);

		for (CustomBlock customBlock : CustomBlock.values()) {
			if (!(customBlock.get() instanceof ICraftable craftable))
				continue;

			Material unlockMaterial = craftable.getRecipeUnlockMaterial();
			if (modelData == 0 && !Nullables.isNullOrAir(unlockMaterial)) {
				if (unlockMaterial.equals(itemStack.getType())) {
					unlockRecipe(player, customBlock);
				}
			}

			if (craftable.getRecipeUnlockCustomBlock() != null) {
				CustomBlock unlockCustomBlock = CustomBlock.valueOf(craftable.getRecipeUnlockCustomBlock());
				if (_customBlock != null && _customBlock == unlockCustomBlock) {
					unlockRecipe(player, customBlock);
				}
			}
		}
	}

	private static void unlockRecipe(Player player, CustomBlock customBlock) {
		for (NexusRecipe recipe : customBlock.getRecipes()) {
			Keyed keyedRecipe = (Keyed) recipe.getRecipe();
			NamespacedKey key = keyedRecipe.getKey();

			if (!player.hasDiscoveredRecipe(key)) {
				Dev.WAKKA.send("unlocking recipe: " + key.getKey());
				player.discoverRecipe(key);
			}
		}
	}
}
