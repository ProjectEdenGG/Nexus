package me.pugabyte.nexus.utils;

import com.google.common.base.Strings;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemUtils {

	public static boolean isFuzzyMatch(ItemStack itemStack1, ItemStack itemStack2) {
		if (itemStack1.getType() != itemStack2.getType())
			return false;

		ItemMeta itemMeta1 = itemStack1.getItemMeta();
		ItemMeta itemMeta2 = itemStack2.getItemMeta();

		if (!itemMeta1.getDisplayName().equals(itemMeta2.getDisplayName()))
			return false;

		if (!Objects.equals(itemMeta1.getLore(), itemMeta2.getLore()))
			return false;

		return true;
	}

	public static void giveItem(Player player, Material material) {
		giveItem(player, material, 1);
	}

	public static void giveItem(Player player, Material material, String nbt) {
		giveItem(player, material, 1, nbt);
	}

	public static void giveItem(Player player, Material material, int amount) {
		giveItem(player, material, amount, null);
	}

	public static void giveItem(Player player, Material material, int amount, String nbt) {
		if (material == Material.AIR)
			throw new InvalidInputException("Cannot spawn air");

		if (amount > 64) {
			for (int i = 0; i < (amount / 64); i++)
				giveItem(player, new ItemStack(material, 64), nbt);
			giveItem(player, new ItemStack(material, amount % 64), nbt);
		} else {
			giveItem(player, new ItemStack(material, amount), nbt);
		}
	}

	public static void giveItem(Player player, ItemStack item) {
		giveItems(player, Collections.singletonList(item));
	}

	public static void giveItem(Player player, ItemStack item, String nbt) {
		giveItems(player, Collections.singletonList(item), nbt);
	}

	public static void giveItems(Player player, Collection<ItemStack> items) {
		giveItems(player, items, null);
	}

	public static void giveItems(Player player, Collection<ItemStack> items, String nbt) {
		List<ItemStack> finalItems = new ArrayList<>(items);
		finalItems.removeIf(ItemUtils::isNullOrAir);
		if (!Strings.isNullOrEmpty(nbt)) {
			finalItems.clear();
			NBTContainer nbtContainer = new NBTContainer(nbt);
			for (ItemStack item : new ArrayList<>(items)) {
				NBTItem nbtItem = new NBTItem(item);
				nbtItem.mergeCompound(nbtContainer);
				finalItems.add(nbtItem.getItem());
			}
		}

		dropExcessItems(player, giveItemsGetExcess(player, finalItems));
	}

	public static List<ItemStack> giveItemsGetExcess(Player player, List<ItemStack> finalItems) {
		List<ItemStack> excess = new ArrayList<>();
		for (ItemStack item : finalItems)
			if (!isNullOrAir(item))
				excess.addAll(player.getInventory().addItem(item).values());

		return excess;
	}

	public static void dropExcessItems(Player player, List<ItemStack> excess) {
		if (!excess.isEmpty())
			excess.forEach(itemStack -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
	}

	public static ItemStack getTool(Player player) {
		ItemStack mainHand = player.getInventory().getItemInMainHand();
		ItemStack offHand = player.getInventory().getItemInOffHand();
		if (!isNullOrAir(mainHand))
			return mainHand;
		else if (!isNullOrAir(offHand))
			return offHand;
		return null;
	}

	public static ItemStack getToolRequired(Player player) {
		ItemStack item = getTool(player);
		if (isNullOrAir(item))
			throw new InvalidInputException("You are not holding anything");
		return item;
	}

	public static EquipmentSlot getHandWithTool(Player player) {
		ItemStack mainHand = player.getInventory().getItemInMainHand();
		ItemStack offHand = player.getInventory().getItemInOffHand();
		if (!isNullOrAir(mainHand))
			return EquipmentSlot.HAND;
		else if (!isNullOrAir(offHand))
			return EquipmentSlot.OFF_HAND;
		return null;
	}

	public static EquipmentSlot getHandWithToolRequired(Player player) {
		EquipmentSlot hand = getHandWithTool(player);
		if (hand == null)
			throw new InvalidInputException("You are not holding anything");
		return hand;
	}

	@Contract("null -> true")
	public static boolean isNullOrAir(ItemStack itemStack) {
		return itemStack == null || itemStack.getType().equals(Material.AIR);
	}

	public static boolean isNullOrAir(Material material) {
		return material == null || material.equals(Material.AIR);
	}

	public static boolean isInventoryEmpty(Inventory inventory) {
		for (ItemStack itemStack : inventory.getContents())
			if (!isNullOrAir(itemStack))
				return false;
		return true;
	}

	public static @Nullable UUID getSkullOwner(ItemStack skull) {
		if (!skull.getType().equals(Material.PLAYER_HEAD))
			return null;

		ItemMeta itemMeta = skull.getItemMeta();
		SkullMeta skullMeta = (SkullMeta) itemMeta;


		if (skullMeta.getPlayerProfile() == null)
			return null;

		if (skullMeta.getPlayerProfile().getId() == null)
			return null;

		return skullMeta.getPlayerProfile().getId();
	}

	public static ItemStack setDurability(ItemStack item, int percentage) {
		ItemMeta meta = item.getItemMeta();
		if (meta instanceof Damageable) {
			Damageable damageable = (Damageable) meta;
			double maxDurability = item.getType().getMaxDurability();
			double damage = (percentage / 100.0) * maxDurability;
			damageable.setDamage((int) damage);

			item.setItemMeta((ItemMeta) damageable);
		}

		return item;
	}

	public static List<Enchantment> getApplicableEnchantments(ItemStack item) {
		List<Enchantment> applicable = new ArrayList<>();
		for (Enchantment enchantment : Enchantment.values()) {
			try {
				item = new ItemStack(item.getType());
				item.addEnchantment(enchantment, 1);
				applicable.add(enchantment); // if it gets here it hasnt errored, so its valid
			} catch (Exception ex) { /* Not applicable, do nothing */ }
		}
		return applicable;
	}

	public static String getName(ItemStack result) {
		if (result.getItemMeta().hasDisplayName())
			return result.getItemMeta().getDisplayName();

		return result.getType().name();
	}
}
