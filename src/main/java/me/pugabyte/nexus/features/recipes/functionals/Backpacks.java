package me.pugabyte.nexus.features.recipes.functionals;

import com.google.common.base.Strings;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.recipes.models.FunctionalRecipe;
import me.pugabyte.nexus.utils.*;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Backpacks extends FunctionalRecipe {

	@Getter
	public ItemStack defaultBackpack = new ItemBuilder(Material.CHEST).name("Backpack").customModelData(1).build();

	@Override
	public String getPermission() {
		return "group.staff";
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
		recipe.setIngredient('3', Material.STRING);
		recipe.setIngredient('4', Material.CHEST);
		return recipe;
		// Produced many exceptions
		//return NexusRecipe.shapedRecipe(key, getDefaultBackpack(), new String[] {"121", "343", "111"}, Material.LEATHER, Material.TRIPWIRE_HOOK, Material.STRING, Material.CHEST);
	}

	@Override
	public List<ItemStack> getIngredients() {
		return new ArrayList<ItemStack>() {{
			add(new ItemStack(Material.LEATHER));
			add(new ItemStack(Material.TRIPWIRE_HOOK));
			add(new ItemStack(Material.STRING));
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
		NBTItem nbtItem = new NBTItem(item.clone());
		return !Strings.isNullOrEmpty(nbtItem.getString("BackpackId"));
	}

	public void openBackpack(Player player, ItemStack backpack) {
		new BackPackMenuListener(player, backpack);
	}

	@EventHandler
	public void onClickBackpack(InventoryClickEvent event) {
		if (ItemUtils.isNullOrAir(event.getCurrentItem())) return;
		if (!isBackpack(event.getCurrentItem())) return;
		if (!event.getClick().isRightClick()) return;
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
		ItemStack newBackpack = new ItemBuilder(backpack.clone()).name("&" + color.getColorCode() + "Backpack")
				.customModelData(color.getDataIndex()).build();
		event.getInventory().setResult(newBackpack);
	}

	@EventHandler
	public void onColorBackpack(InventoryClickEvent event) {
		if (!(event.getInventory() instanceof CraftingInventory)) return;
		CraftingInventory inv = (CraftingInventory) event.getInventory();
		if (inv.getResult() == null) return;
		if (!isBackpack(inv.getResult())) return;
		NBTItem item = new NBTItem(inv.getResult());
		if (item.getInteger("CustomModelData") <= 1) return;
		if (event.getSlot() != 0) return;
		for (ItemStack matrixItem : inv.getMatrix())
			if (ItemUtils.isNullOrAir(matrixItem)) {
				continue;
			} else if (MaterialTag.DYES.isTagged(matrixItem.getType())) {
				int dyeAmount = matrixItem.getAmount() - 1;
				Tasks.wait(1, () -> matrixItem.setAmount(dyeAmount));
			}
	}

	@EventHandler
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

	public enum BackpackColor {
		WHITE('f'),
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

		int getDataIndex() {
			return ordinal() + 2;
		}

	}

	public static class BackPackMenuListener implements Listener {

		ItemStack backpack;
		Player player;

		public BackPackMenuListener(Player player, ItemStack backpack) {
			this.backpack = backpack;
			this.player = player;
			ItemStack[] contents = new ItemStack[27];
			NBTItem nbtItem = new NBTItem(backpack);
			if (!Strings.isNullOrEmpty(nbtItem.getString("BackpackContent"))) {
				contents = SerializationUtils.JSON.deserializeItemStacks(SerializationUtils.JSON.fromStringToList(nbtItem.getString("BackpackContent"))).toArray(new ItemStack[0]);
			}
			Inventory inv = Bukkit.createInventory(null, 27, backpack.getItemMeta().getDisplayName());
			inv.setContents(contents);
			player.openInventory(inv);

			Nexus.registerListener(this);
		}

		// Cancel Moving backpacks while backpack is open
		@EventHandler
		public void onClickBackPack(InventoryClickEvent event) {
			if (player != event.getWhoClicked()) return;
			if (event.getCurrentItem() == null) return;
			if (!isBackpack(event.getCurrentItem())) return;
			event.setCancelled(true);
		}

		@EventHandler
		public void onInventoryClose(InventoryCloseEvent event) {
			if (player != event.getPlayer()) return;
			Nexus.unregisterListener(this);
			ItemStack[] contents = event.getView().getTopInventory().getContents();
			String serializedContents = SerializationUtils.JSON.toString(SerializationUtils.JSON.serialize(Arrays.asList(contents)));
			ItemStack newPackPack = backpack.clone();
			NBTItem nbtItem = new NBTItem(newPackPack);
			nbtItem.setString("BackpackContent", serializedContents);
			backpack.setItemMeta(nbtItem.getItem().getItemMeta());
			player.updateInventory();
		}


	}


}
