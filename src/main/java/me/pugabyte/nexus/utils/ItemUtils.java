package me.pugabyte.nexus.utils;

import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

	public static void combine(List<ItemStack> itemStacks, ItemStack newItemStack) {
		Optional<ItemStack> matching = itemStacks.stream()
				.filter(existing -> existing.isSimilar(newItemStack) && existing.getAmount() < existing.getType().getMaxStackSize())
				.findFirst();

		if (matching.isPresent()) {
			ItemStack match = matching.get();
			itemStacks.remove(match);
			int amountICanAdd = Math.min(newItemStack.getAmount(), match.getType().getMaxStackSize() - match.getAmount());
			match.setAmount(match.getAmount() + amountICanAdd);
			itemStacks.add(new ItemStack(match));

			newItemStack.setAmount(newItemStack.getAmount() - amountICanAdd);
		}

		if (newItemStack.getAmount() > 0)
			itemStacks.add(new ItemStack(newItemStack));
	}

	public static List<ItemStack> getShulkerContents(ItemStack itemStack) {
		return getRawShulkerContents(itemStack).stream().filter(content -> !ItemUtils.isNullOrAir(content)).collect(Collectors.toList());
	}

	public static List<ItemStack> getRawShulkerContents(ItemStack itemStack) {
		List<ItemStack> contents = new ArrayList<>();

		if (ItemUtils.isNullOrAir(itemStack))
			return contents;

		if (!MaterialTag.SHULKER_BOXES.isTagged(itemStack.getType()))
			return contents;
		
		if (!(itemStack.getItemMeta() instanceof BlockStateMeta))
			return contents;

		BlockStateMeta meta = (BlockStateMeta) itemStack.getItemMeta();
		if (!(meta.getBlockState() instanceof ShulkerBox))
			return contents;

		ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
		contents.addAll(Arrays.asList(shulkerBox.getInventory().getContents()));

		return contents;
	}

	public static ItemStack getTool(HasPlayer player) {
		Player _player = player.getPlayer();
		ItemStack mainHand = _player.getInventory().getItemInMainHand();
		ItemStack offHand = _player.getInventory().getItemInOffHand();
		if (!isNullOrAir(mainHand))
			return mainHand;
		else if (!isNullOrAir(offHand))
			return offHand;
		return null;
	}

	public static ItemStack getToolRequired(HasPlayer player) {
		ItemStack item = getTool(player);
		if (isNullOrAir(item))
			throw new InvalidInputException("You are not holding anything");
		return item;
	}

	public static EquipmentSlot getHandWithTool(HasPlayer player) {
		Player _player = player.getPlayer();
		ItemStack mainHand = _player.getInventory().getItemInMainHand();
		ItemStack offHand = _player.getInventory().getItemInOffHand();
		if (!isNullOrAir(mainHand))
			return EquipmentSlot.HAND;
		else if (!isNullOrAir(offHand))
			return EquipmentSlot.OFF_HAND;
		return null;
	}

	public static EquipmentSlot getHandWithToolRequired(HasPlayer player) {
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

	public static boolean isSimilar(ItemStack item1, ItemStack item2) {
		if (isNullOrAir(item1) || isNullOrAir(item2))
			return false;

		if (item1.getType() != item2.getType())
			return false;

		if (!MaterialTag.SHULKER_BOXES.isTagged(item1.getType()))
			return item1.isSimilar(item2);

		List<ItemStack> contents1 = getRawShulkerContents(item1);
		List<ItemStack> contents2 = getRawShulkerContents(item2);
		if (contents1.isEmpty() && contents2.isEmpty())
			return true;

		for (int i = 0; i < contents1.size(); i++) {
			if (contents1.get(i) == null && contents2.get(i) == null)
				continue;
			if (contents1.get(i) == null || !contents1.get(i).isSimilar(contents2.get(i)))
				return false;
		}

		return true;
	}
}
