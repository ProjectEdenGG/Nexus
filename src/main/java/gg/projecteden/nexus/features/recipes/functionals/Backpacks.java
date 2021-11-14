package gg.projecteden.nexus.features.recipes.functionals;

import de.tr7zw.nbtapi.NBTItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.SmartInvsPlugin;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.TemporaryListener;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.FakePlayerInteractEvent;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.utils.ItemUtils.find;
import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;
import static gg.projecteden.nexus.utils.MaterialTag.DYES;
import static gg.projecteden.utils.StringUtils.isNullOrEmpty;
import static gg.projecteden.utils.StringUtils.paste;

public class Backpacks extends FunctionalRecipe {

	@Getter
	public static ItemStack defaultBackpack = new ItemBuilder(Material.SHULKER_BOX)
		.name("Backpack")
		.customModelData(1)
		.build();

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

		return !isNullOrEmpty(new NBTItem(item).getString("BackpackId"));
	}

	public static boolean isBackpack(ItemStack item, String id) {
		if (!isBackpack(item))
			return false;

		return new NBTItem(item).getString("BackpackId").equals(id);
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
			.material(color.getShulkerBox())
			.name(displayName)
			.build();

		copyContents(backpack, newBackpack);

		event.getInventory().setResult(newBackpack);
	}

	private void copyContents(ItemStack oldBackpack, ItemStack newBackpack) {
		BlockStateMeta oldMeta = (BlockStateMeta) oldBackpack.getItemMeta();
		BlockStateMeta newMeta = (BlockStateMeta) newBackpack.getItemMeta();
		ShulkerBox oldBox = (ShulkerBox) oldMeta.getBlockState();
		ShulkerBox newBox = (ShulkerBox) newMeta.getBlockState();
		newBox.getInventory().setContents(oldBox.getInventory().getContents());
		newMeta.setBlockState(newBox);
		newBackpack.setItemMeta(newMeta);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onCraftBackpack(PrepareItemCraftEvent event) {
		if (event.getRecipe() == null)
			return;

		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		final ItemStack result = event.getInventory().getResult();
		if (!getDefaultBackpack().equals(result))
			return;

		final ItemStack backpack = getBackpack(player, result.clone());
		event.getInventory().setResult(backpack);
	}

	public static ItemStack getBackpack(Player player) {
		return getBackpack(player, null);
	}

	public static ItemStack getBackpack(Player player, ItemStack backpack) {
		if (backpack == null)
			backpack = defaultBackpack.clone();

		NBTItem nbtItem = new NBTItem(backpack);
		nbtItem.setString("BackpackId", RandomStringUtils.randomAlphabetic(10));
		nbtItem.setString("BackpackOwner", player.getUniqueId().toString());
		return nbtItem.getItem();
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

	public static class BackpackMenu implements TemporaryListener {
		@Getter
		private final Player player;
		private final ItemStack backpack;
		private final String backpackId;
		private final ItemStack[] originalItems;

		public BackpackMenu(Player player, ItemStack backpack) {
			this.player = player;
			this.backpack = backpack;
			this.backpackId = new NBTItem(backpack.clone()).getString("BackpackId");

			BlockStateMeta blockStateMeta = (BlockStateMeta) backpack.getItemMeta();
			ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
			originalItems = shulkerBox.getInventory().getContents();

			Inventory inv = Bukkit.createInventory(null, 27, backpack.getItemMeta().getDisplayName());
			inv.setContents(originalItems);
			player.openInventory(inv);
			Nexus.registerTemporaryListener(this);
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

		@EventHandler
		public void onInventoryClose(InventoryCloseEvent event) {
			if (!player.equals(event.getPlayer()))
				return;

			Nexus.unregisterTemporaryListener(this);
			save(event.getView().getTopInventory().getContents());
		}

		private void save(ItemStack[] contents) {
			ItemStack[] inv = player.getInventory().getContents();
			ItemStack backpack = find(inv, item -> isBackpack(item, this.backpackId));
			BlockStateMeta meta = null;

			if (backpack != null)
				meta = (BlockStateMeta) backpack.getItemMeta();

			if (meta == null) {
				handleError(contents);
				return;
			}

			ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
			shulkerBox.getInventory().setContents(contents);
			meta.setBlockState(shulkerBox);
			backpack.setItemMeta(meta);

			player.updateInventory();
			Tasks.wait(1, player::updateInventory);
		}

		private void handleError(ItemStack[] contents) {
			Nexus.warn("There was an error while saving Backpack contents for " + player.getName());
			Nexus.warn("Below is a serialized paste of the original and new contents in the backpack:");
			Nexus.warn("Old Contents: " + paste(Json.toString(Json.serialize(Arrays.asList(originalItems)))));
			Nexus.warn("New Contents: " + paste(Json.toString(Json.serialize(Arrays.asList(contents)))));
			PlayerUtils.send(player, "&cThere was an error while saving your backpack items. Please report this to staff to retrieve your lost items.");
		}

	}

}
