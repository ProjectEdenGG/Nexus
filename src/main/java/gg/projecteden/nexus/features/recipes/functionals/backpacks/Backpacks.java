package gg.projecteden.nexus.features.recipes.functionals.backpacks;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.events.FakePlayerInteractEvent;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.resourcepack.ResourcePack.RainbowBlockOrder;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.utils.ItemUtils.find;
import static gg.projecteden.nexus.utils.MaterialTag.DYES;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.paste;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;

public class Backpacks extends FunctionalRecipe {

	@Getter
	public static ItemStack defaultBackpack = new ItemBuilder(CustomMaterial.BACKPACK).name("Backpack").build();
	public static final String NBT_KEY = "BackpackId";

	@Override
	public ItemStack getResult() {
		return getDefaultBackpack();
	}

	@NonNull
	@Override
	public @NotNull Recipe getRecipe() {
		return shaped("121", "343", "111")
			.add('1', Material.LEATHER)
			.add('2', Material.TRIPWIRE_HOOK)
			.add('3', Material.SHULKER_SHELL)
			.add('4', Material.CHEST)
			.toMake(getResult())
			.getRecipe();
	}

	public static boolean isBackpack(ItemStack item) {
		if (isNullOrAir(item))
			return false;

		return !isNullOrEmpty(new NBTItem(item).getString(NBT_KEY));
	}

	public static String getBackpackId(ItemStack item) {
		if (!isBackpack(item))
			return null;

		return new NBTItem(item).getString(NBT_KEY);
	}

	public void openBackpack(Player player, ItemStack backpack) {
		new SoundBuilder(Sound.BLOCK_CHEST_OPEN).receiver(player).volume(.3f).play();
		new BackpackMenu(player, backpack);
	}

	@EventHandler
	public void onClickBackpack(InventoryClickEvent event) {
		if (!isBackpack(event.getCurrentItem()))
			return;

		if (!event.getClick().isRightClick())
			return;

		if (!(event.getClickedInventory() instanceof PlayerInventory))
			return;

		if (!(event.getWhoClicked() instanceof Player player))
			return;

		Optional<SmartInventory> smartInv = SmartInvsPlugin.manager().getInventory(player);
		if (smartInv.isPresent() && !smartInv.get().isCloseable())
			return;

		event.setCancelled(true);
		player.closeInventory();
		openBackpack(player, event.getCurrentItem());
	}

	@EventHandler
	public void onColorBackpackPrepareCraft(PrepareItemCraftEvent event) {
		List<ItemStack> matrix = getFilteredMatrix(event);

		if (matrix.size() != 2)
			return;

		ItemStack dye = find(matrix, DYES::isTagged);
		ItemStack backpack = find(matrix, Backpacks::isBackpack);

		if (backpack == null || dye == null)
			return;

		final ColorType color = ColorType.of(dye.getType());
		if (color == null)
			return;

		Component displayName = Component.text("Backpack").color(color.getNamedColor());
		if (backpack.getItemMeta().hasDisplayName())
			displayName = backpack.getItemMeta().displayName();

		ItemStack newBackpack = new ItemBuilder(backpack.clone())
			.name(displayName)
			.modelId(RainbowBlockOrder.of(color).ordinal() + 2)
			.build();

		copyContents(backpack, newBackpack);

		event.getInventory().setResult(newBackpack);
	}

	private void copyContents(ItemStack oldBackpack, ItemStack newBackpack) {
		new ItemBuilder(newBackpack, true).clearShulkerBox().shulkerBox(new ItemBuilder(oldBackpack).shulkerBoxContents());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onCraftBackpack(PrepareItemCraftEvent event) {
		if (event.getRecipe() == null)
			return;

		if (!(event.getView().getPlayer() instanceof Player))
			return;

		final ItemStack result = event.getInventory().getResult();
		if (!defaultBackpack.equals(result))
			return;

		final ItemStack backpack = getBackpack(result.clone());
		event.getInventory().setResult(backpack);
	}

	public static ItemStack getBackpack() {
		return getBackpack(null);
	}

	public static ItemStack getBackpack(ItemStack backpack) {
		if (backpack == null)
			backpack = defaultBackpack.clone();

		return new ItemBuilder(backpack)
			.nbt(nbt -> nbt.setString(NBT_KEY, randomAlphabetic(10)))
			.build();
	}

	@EventHandler
	public void onPlaceBackpack(PlayerInteractEvent event) {
		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;

		if (!isBackpack(event.getItem()))
			return;

		if (event instanceof FakePlayerInteractEvent)
			return;

		event.setCancelled(true);

		openBackpack(event.getPlayer(), event.getItem());
	}

	@EventHandler
	public void onDispenserPlaceBackpack(BlockDispenseEvent event) {
		if (!isBackpack(event.getItem()))
			return;

		event.setCancelled(true);
	}

	public static class BackpackMenu implements TemporaryMenuListener {
		@Getter
		private final Player player;
		private final ItemStack backpack;
		private final List<ItemStack> originalItems;

		@Getter
		private final BackpackHolder inventoryHolder = new BackpackHolder();

		public BackpackMenu(Player player, ItemStack backpack) {
			this.player = player;
			this.backpack = backpack;
			this.originalItems = new ItemBuilder(backpack).shulkerBoxContents();

			try {
				verifyInventory(player);
				open(3, originalItems);
			} catch (Exception ex) {
				ex.printStackTrace();
				PlayerUtils.send(player, StringUtils.getPrefix("Backpacks") + "&c" + ex.getMessage());
			}
		}

		public static class BackpackHolder extends CustomInventoryHolder {}

		@Override
		public String getTitle() {
			final String displayName = backpack.getItemMeta().getDisplayName();
			if (!isNullOrEmpty(displayName))
				return displayName;

			return "Backpack";
		}

		@EventHandler
		public void onDropBackpack(PlayerDropItemEvent event) {
			if (player != event.getPlayer())
				return;

			if (!isBackpack(event.getItemDrop().getItemStack()))
				return;

			event.setCancelled(true);
			player.getInventory().setItem(player.getInventory().getHeldItemSlot(), backpack);
		}

		// Cancel Moving Shulker Boxes While backpack is open
		@EventHandler
		public void onClickBackpack(InventoryClickEvent event) {
			if (player != event.getWhoClicked())
				return;

			if (event.getClickedInventory() == null)
				return;

			ItemStack item = event.getClickedInventory().getItem(event.getSlot());
			if (event.getClick() == ClickType.NUMBER_KEY)
				item = player.getInventory().getContents()[event.getHotbarButton()];

			if (!MaterialTag.SHULKER_BOXES.isTagged(item))
				return;

			event.setCancelled(true);
		}

		@Override
		public boolean keepAirSlots() {
			return true;
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			if (backpack == null || !(backpack.getItemMeta() instanceof BlockStateMeta)) {
				handleError(contents);
				return;
			}

			backpack.setItemMeta(new ItemBuilder(backpack).clearShulkerBox().shulkerBox(contents).build().getItemMeta());

			player.updateInventory();
			Tasks.wait(1, player::updateInventory);
		}

		private void handleError(List<ItemStack> contents) {
			Nexus.warn("There was an error while saving Backpack contents for " + player.getName());
			Nexus.warn("Below is a serialized paste of the original and new contents in the backpack:");
			Nexus.warn("Old Contents: " + paste(Json.toString(Json.serialize(originalItems))));
			Nexus.warn("New Contents: " + paste(Json.toString(Json.serialize(contents))));
			PlayerUtils.send(player, "&cThere was an error while saving your backpack items. Please report this to staff to retrieve your lost items.");
		}

		private static void verifyInventory(Player player) {
			List<String> ids = new ArrayList<>();
			for (ItemStack item : player.getInventory().getContents()) {
				if (!isBackpack(item))
					continue;

				final String id = getBackpackId(item);
				if (ids.contains(id))
					throw new InvalidInputException("Duplicate backpacks found, please contact staff");
				ids.add(id);
			}
		}
	}

}
