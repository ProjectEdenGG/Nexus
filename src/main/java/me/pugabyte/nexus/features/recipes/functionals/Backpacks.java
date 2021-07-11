package me.pugabyte.nexus.features.recipes.functionals;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.listeners.TemporaryListener;
import me.pugabyte.nexus.features.recipes.models.FunctionalRecipe;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SerializationUtils.JSON;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static eden.utils.StringUtils.isNullOrEmpty;
import static me.pugabyte.nexus.utils.MaterialTag.DYES;

public class Backpacks extends FunctionalRecipe {

	@Getter
	public static ItemStack defaultBackpack = new ItemBuilder(Material.SHULKER_BOX)
		.name("Backpack")
		.customModelData(1)
		.build();

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public ItemStack getResult() {
		return getDefaultBackpack();
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_backpack");
		ShapedRecipe recipe = new ShapedRecipe(key, getDefaultBackpack());
		recipe.shape("121", "343", "111");
		recipe.setIngredient('1', Material.LEATHER);
		recipe.setIngredient('2', Material.TRIPWIRE_HOOK);
		recipe.setIngredient('3', Material.SHULKER_SHELL);
		recipe.setIngredient('4', Material.CHEST);
		return recipe;
	}

	@Override
	public List<ItemStack> getIngredients() {
		return new ArrayList<>() {{
			add(new ItemStack(Material.LEATHER));
			add(new ItemStack(Material.TRIPWIRE_HOOK));
			add(new ItemStack(Material.SHULKER_SHELL));
			add(new ItemStack(Material.CHEST));
		}};
	}

	@Override
	public String[] getPattern() {
		return new String[]{"121", "343", "111"};
	}

	@Override
	public MaterialChoice getMaterialChoice() {
		return null;
	}

	public static String getRandomBackPackId() {
		return RandomStringUtils.randomAlphabetic(10);
	}

	public static boolean isBackpack(ItemStack item) {
		if (ItemUtils.isNullOrAir(item))
			return false;

		return !isNullOrEmpty(new NBTItem(item.clone()).getString("BackpackId"));
	}

	public void openBackpack(Player player, ItemStack backpack) {
		player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, .3f, 1f);
		new BackPackMenuListener(player, backpack);
	}

	@EventHandler
	public void onClickBackpack(InventoryClickEvent event) {
		if (!isBackpack(event.getCurrentItem()))
			return;

		if (!event.getClick().isRightClick())
			return;

		if (!(event.getClickedInventory() instanceof PlayerInventory))
			return;

		event.setCancelled(true);
		event.getWhoClicked().closeInventory();
		openBackpack((Player) event.getWhoClicked(), event.getCurrentItem());
	}

	@EventHandler
	public void onColorBackpackPrepareCraft(PrepareItemCraftEvent event) {
		List<ItemStack> matrix = new ArrayList<>(Arrays.asList(event.getInventory().getMatrix().clone()));
		matrix.removeIf(ItemUtils::isNullOrAir);

		if (matrix.size() != 2)
			return;

		ItemStack dye = find(matrix, DYES::isTagged);
		ItemStack backpack = find(matrix, Backpacks::isBackpack);

		if (backpack == null || dye == null)
			return;

		final ColorType color = ColorType.of(dye.getType());
		if (color == null)
			return;

		ItemStack newBackpack = new ItemBuilder(backpack.clone())
			.material(Material.valueOf(color.name() + "_SHULKER_BOX"))
			.name(backpack.getItemMeta().hasDisplayName() ? backpack.getItemMeta().getDisplayName() : color.getChatColor() + "Backpack")
			.build();

		copyContents(backpack, newBackpack);

		event.getInventory().setResult(newBackpack);
	}

	private ItemStack find(List<ItemStack> items, Predicate<ItemStack> predicate) {
		for (ItemStack item : items)
			if (predicate.test(item))
				return item;
		return null;
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
		nbtItem.setString("BackpackId", getRandomBackPackId());
		nbtItem.setString("BackpackOwner", player.getUniqueId().toString());
		return nbtItem.getItem();
	}

	@EventHandler
	public void onPlaceBackpack(PlayerInteractEvent event) {
		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;

		if (ItemUtils.isNullOrAir(event.getItem()))
			return;

		if (!isBackpack(event.getItem()))
			return;

		event.setCancelled(true);
		openBackpack(event.getPlayer(), event.getItem());
	}

	@EventHandler
	public void onDispenserPlaceBackpack(BlockDispenseEvent event) {
		if (ItemUtils.isNullOrAir(event.getItem()))
			return;
		if (!isBackpack(event.getItem()))
			return;
		event.setCancelled(true);
	}

	public static class BackPackMenuListener implements TemporaryListener {
		private final ItemStack backpack;
		private final String backpackId;
		@Getter
		private final Player player;
		private final ItemStack[] originalItems;

		public BackPackMenuListener(Player player, ItemStack backpack) {
			this.backpack = backpack;
			this.player = player;
			this.backpackId = new NBTItem(backpack.clone()).getString("BackpackId");

			BlockStateMeta blockStateMeta = (BlockStateMeta) backpack.getItemMeta();
			ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
			originalItems = shulkerBox.getInventory().getContents();

			Inventory inv = Bukkit.createInventory(null, 27, backpack.getItemMeta().getDisplayName());
			inv.setContents(originalItems);
			player.openInventory(inv);
			Nexus.registerTemporaryListener(this);
		}

		public void saveContents(ItemStack[] contents) {
			BlockStateMeta blockStateMeta = null;
			ItemStack[] inv = player.getInventory().getContents();
			ItemStack itemStack = null;

			for (ItemStack item : inv) {
				if (!isBackpack(item))
					continue;

				if (new NBTItem(item.clone()).getString("BackpackId").equals(backpackId)) {
					blockStateMeta = (BlockStateMeta) item.getItemMeta();
					itemStack = item;
					break;
				}
			}

			if (blockStateMeta == null) {
				Nexus.warn("There was an error while saving Backpack contents for " + player.getName());
				Nexus.warn("Below is a serialized paste of the original and new contents in the backpack:");
				Nexus.warn("Old Contents: " + StringUtils.paste(JSON.toString(JSON.serialize(Arrays.asList(originalItems)))));
				Nexus.warn("New Contents: " + StringUtils.paste(JSON.toString(JSON.serialize(Arrays.asList(contents)))));
				PlayerUtils.send(player,"&cThere was an error while saving your backpack items. Please report this to staff to retrieve your lost items.");
				return;
			}

			ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
			shulkerBox.getInventory().setContents(contents);
			blockStateMeta.setBlockState(shulkerBox);
			itemStack.setItemMeta(blockStateMeta);

			player.updateInventory();
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
		public void onClickBackPack(InventoryClickEvent event) {
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
			if (player != event.getPlayer())
				return;

			Nexus.unregisterTemporaryListener(this);
			ItemStack[] contents = event.getView().getTopInventory().getContents();
			saveContents(contents);
		}
	}

}
