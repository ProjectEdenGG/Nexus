package me.pugabyte.nexus.features.recipes.functionals;

import com.google.common.base.Strings;
import de.tr7zw.nbtapi.NBTItem;
import fr.minuskube.inv.content.InventoryContents;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.recipes.models.FunctionalRecipe;
import me.pugabyte.nexus.utils.*;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Backpacks extends FunctionalRecipe {

	@Getter
	public ItemStack defaultBackpack = new ItemBuilder(Material.SHULKER_BOX).name("&rBackpack").customModelData(1).build();

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
		return new ArrayList<ItemStack>() {{
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
	public RecipeChoice.MaterialChoice getMaterialChoice() {
		return null;
	}

	public String getRandomBackPackId() {
		return RandomStringUtils.randomAlphabetic(10);
	}

	public static boolean isBackpack(ItemStack item) {
		if (ItemUtils.isNullOrAir(item)) return false;
		NBTItem nbtItem = new NBTItem(item.clone());
		return !Strings.isNullOrEmpty(nbtItem.getString("BackpackId"));
	}

	public void openBackpack(Player player, ItemStack backpack) {
		player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, .3f, 1f);
		new BackPackMenuListener(player, backpack);
	}

	@EventHandler
	public void onClickBackpack(InventoryClickEvent event) {
		if (!isBackpack(event.getCurrentItem())) return;
		if (!event.getClick().isRightClick()) return;
		if (!(event.getClickedInventory() instanceof PlayerInventory)) return;
		event.setCancelled(true);
		event.getWhoClicked().closeInventory();
		openBackpack((Player) event.getWhoClicked(), event.getCurrentItem());
	}

	@EventHandler
	public void onColorBackpackPrepareCraft(PrepareItemCraftEvent event) {
		ItemStack[] matrix = event.getInventory().getMatrix();
		List<ItemStack> matrixList = new ArrayList<>(Arrays.asList(matrix.clone()));
		matrixList.removeIf(ItemUtils::isNullOrAir);
		if (matrixList.size() != 2) return;
		ItemStack backpack;
		ItemStack dye;
		if (isBackpack(matrixList.get(0)) && MaterialTag.DYES.isTagged(matrixList.get(1).getType())) {
			backpack = matrixList.get(0);
			dye = matrixList.get(1);
		} else if (isBackpack(matrixList.get(1)) && MaterialTag.DYES.isTagged(matrixList.get(0).getType())) {
			backpack = matrixList.get(1);
			dye = matrixList.get(0);
		} else return;
		BackpackColor color = BackpackColor.fromDye(dye.getType());
		if (color == null) return;
		ItemStack newBackpack = new ItemBuilder(backpack.clone()).material(Material.valueOf(color.name() + "_SHULKER_BOX")).name("&" + color.getColorCode() + "Backpack").build();
		// Copy Contents
		BlockStateMeta oldMeta = (BlockStateMeta) backpack.getItemMeta();
		BlockStateMeta newMeta = (BlockStateMeta) newBackpack.getItemMeta();
		ShulkerBox oldBox = (ShulkerBox) oldMeta.getBlockState();
		ShulkerBox newBox = (ShulkerBox) newMeta.getBlockState();
		newBox.getInventory().setContents(oldBox.getInventory().getContents());
		newMeta.setBlockState(newBox);
		newBackpack.setItemMeta(newMeta);

		event.getInventory().setResult(newBackpack);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onCraftBackpack(PrepareItemCraftEvent event) {
		if (event.getRecipe() == null) return;
		if (event.getInventory().getResult() == null) return;
		if (!event.getInventory().getResult().equals(getDefaultBackpack())) return;
		NBTItem nbtItem = new NBTItem(event.getInventory().getResult().clone());
		nbtItem.setString("BackpackId", getRandomBackPackId());
		nbtItem.setString("BackpackOwner", event.getView().getPlayer().getUniqueId().toString());
		event.getInventory().setResult(nbtItem.getItem());
	}

	@EventHandler
	public void onPlaceBackpack(PlayerInteractEvent event) {
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event)) return;
		if (ItemUtils.isNullOrAir(event.getItem())) return;
		if (!isBackpack(event.getItem())) return;
		event.setCancelled(true);
		openBackpack(event.getPlayer(), event.getItem());
	}

	@EventHandler
	public void onDispenserPlaceBackpack(BlockDispenseEvent event) {
		if (ItemUtils.isNullOrAir(event.getItem())) return;
		if (!isBackpack(event.getItem())) return;
		event.setCancelled(true);
	}

	public enum BackpackColor {
		WHITE('r'),
		ORANGE('6'),
		MAGENTA('d'),
		LIGHT_BLUE('b'),
		YELLOW('e'),
		LIME('a'),
		PINK('d'),
		GRAY('8'),
		LIGHT_GRAY('7'),
		CYAN('3'),
		PURPLE('5'),
		BLUE('9'),
		BROWN('6'),
		GREEN('2'),
		RED('c'),
		BLACK('0');

		@Getter
		char colorCode;

		BackpackColor(char colorCode) {
			this.colorCode = colorCode;
		}

		static BackpackColor fromDye(Material material) {
			try {
				return valueOf(material.name().replace("_DYE", ""));
			} catch (Exception ig) {
			}
			return null;
		}
	}

	public static class BackPackMenuListener implements Listener {

		ItemStack backpack;
		String backpackId;
		Player player;
		Inventory inv;
		ItemStack[] originalItems;

		public BackPackMenuListener(Player player, ItemStack backpack) {
			this.backpack = backpack;
			this.player = player;
			this.backpackId = new NBTItem(backpack.clone()).getString("BackpackId");

			BlockStateMeta blockStateMeta = (BlockStateMeta) backpack.getItemMeta();
			ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
			originalItems = shulkerBox.getInventory().getContents();

			Inventory inv = Bukkit.createInventory(null, 27, backpack.getItemMeta().getDisplayName());
			inv.setContents(originalItems);
			this.inv = inv;
			player.openInventory(inv);
			Nexus.registerListener(this);
		}

		public void saveContents(ItemStack[] contents) {
			BlockStateMeta blockStateMeta = null;
			ItemStack itemStack = null;
			try {
				blockStateMeta = (BlockStateMeta) backpack.getItemMeta();
				itemStack = backpack;
			} catch (Exception ignore) {
				ItemStack[] inv = player.getInventory().getContents();
				for (int i = 0; i < 36; i++) {
					ItemStack item = inv[i];
					if (!isBackpack(item)) continue;
					if (new NBTItem(item.clone()).getString("BackpackId").equals(backpackId)) {
						blockStateMeta = (BlockStateMeta) item.getItemMeta();
						itemStack = item;
						break;
					}
				}
				if (blockStateMeta == null) {
					Nexus.warn("There was an error while saving Backpack contents for " + player.getName());
					Nexus.warn("Below is a serialized paste of the original and new contents in the backpack:");
					Nexus.warn("Old Contents: " + StringUtils.paste(SerializationUtils.JSON.toString(SerializationUtils.JSON.serialize(Arrays.asList(originalItems)))));
					Nexus.warn("New Contents: " + StringUtils.paste(SerializationUtils.JSON.toString(SerializationUtils.JSON.serialize(Arrays.asList(contents)))));
					PlayerUtils.send(player,"&cThere was an error while saving your backpack items. Please report this to staff to retrieve your lost items.");
					return;
				}
			}

			ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
			shulkerBox.getInventory().setContents(contents);
			blockStateMeta.setBlockState(shulkerBox);
			itemStack.setItemMeta(blockStateMeta);

			player.updateInventory();
		}

		@EventHandler
		public void onDropBackpack(PlayerDropItemEvent event) {
			if (player != event.getPlayer()) return;
			if (!isBackpack(event.getItemDrop().getItemStack())) return;
			event.setCancelled(true);
			player.getInventory().setItem(player.getInventory().getHeldItemSlot(), backpack);
		}

		// Cancel Moving Shulker Boxes While backpack is open
		@EventHandler
		public void onClickBackPack(InventoryClickEvent event) {
			if (player != event.getWhoClicked()) return;
			if (event.getClickedInventory() == null) return;
			ItemStack item = event.getClickedInventory().getItem(event.getSlot());
			if (event.getClick() == ClickType.NUMBER_KEY)
				item = player.getInventory().getContents()[event.getHotbarButton()];
			if (ItemUtils.isNullOrAir(item)) return;
			if (!MaterialTag.SHULKER_BOXES.isTagged(item.getType())) return;
			event.setCancelled(true);
		}

		@EventHandler
		public void onInventoryClose(InventoryCloseEvent event) {
			if (player != event.getPlayer()) return;
			Nexus.unregisterListener(this);
			ItemStack[] contents = event.getView().getTopInventory().getContents();
			saveContents(contents);
		}
	}

}
