package gg.projecteden.nexus.utils;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.itemtags.Condition;
import gg.projecteden.nexus.features.itemtags.ItemTagsUtils;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.survival.MendingIntegrity;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.parchment.HasPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Material;
import org.bukkit.StructureType;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.potion.CraftPotionUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class ItemUtils {

	public static boolean isPreferredTool(ItemStack tool, Block block) {
		if (isNullOrAir(tool) || isNullOrAir(block))
			return false;

		final ToolType toolType = ToolType.of(tool);
		if (toolType == null)
			return false;

		if (toolType.getPreferredToolTag() == null)
			return false;

		return NMSUtils.toNMS(block.getBlockData()).is(toolType.getPreferredToolTag());
	}

	@Contract("_, null -> false; null, _ -> false")
	public static boolean isTypeAndNameEqual(ItemStack itemStack1, ItemStack itemStack2) {
		if (isNullOrAir(itemStack1) || isNullOrAir(itemStack2)) return false;
		if (itemStack1.getType() != itemStack2.getType()) return false;
		Function<ItemStack, String> name = item -> stripColor(item.getItemMeta().getDisplayName());
		return name.apply(itemStack1).equals(name.apply(itemStack2));
	}

	public static boolean isModelMatch(ItemStack itemStack1, ItemStack itemStack2) {
		if (Nullables.isNullOrAir(itemStack1) || Nullables.isNullOrAir(itemStack2))
			return false;

		if (itemStack1.getType() != itemStack2.getType())
			return false;

		int modelId1 = new ItemBuilder(itemStack1).modelId();
		int modelId2 = new ItemBuilder(itemStack2).modelId();

		return modelId1 == modelId2;
	}

	public static boolean isFuzzyMatch(ItemStack itemStack1, ItemStack itemStack2) {
		if (itemStack1 == null || itemStack2 == null)
			return false;

		if (itemStack1.getType() != itemStack2.getType())
			return false;

		ItemMeta itemMeta1 = itemStack1.getItemMeta();
		ItemMeta itemMeta2 = itemStack2.getItemMeta();

		if ((itemMeta1 == null && itemMeta2 != null) || (itemMeta1 != null && itemMeta2 == null))
			return false;

		if (itemMeta1 != null && itemMeta2 != null) {
			if (!itemMeta1.getDisplayName().equals(itemMeta2.getDisplayName()))
				return false;

			List<String> lore1 = itemMeta1.getLore();
			List<String> lore2 = itemMeta2.getLore();

			final List<String> conditionTags = Arrays.stream(Condition.values()).map(condition -> stripColor(condition.getTag())).toList();
			final Function<List<String>, List<String>> filter = lore -> lore.stream()
				.filter(line -> !conditionTags.contains(stripColor(line)))
				.filter(line -> !Nullables.isNullOrEmpty(line.trim()))
				.toList();

			if (lore1 != null)
				lore1 = filter.apply(lore1);

			if (lore2 != null)
				lore2 = filter.apply(lore2);

			if (!Objects.equals(lore1, lore2))
				return false;

			if (itemMeta1.hasCustomModelData() && itemMeta2.hasCustomModelData())
				return itemMeta1.getCustomModelData() == itemMeta2.getCustomModelData();
		}

		return true;
	}

	public static @Nullable ItemStack clone(@Nullable ItemStack itemStack) {
		if (isNullOrAir(itemStack))
			return null;

		return itemStack.clone();
	}

	@Contract("null, _ -> null; !null, _ -> _")
	public static @Nullable ItemStack clone(@Nullable ItemStack itemStack, int amount) {
		if (isNullOrAir(itemStack))
			return null;

		final ItemStack clone = itemStack.clone();
		clone.setAmount(amount);
		return clone;
	}

	public static void combine(List<ItemStack> itemStacks, ItemStack... newItemStacks) {
		combine(itemStacks, Arrays.asList(newItemStacks));
	}

	public static void combine(List<ItemStack> itemStacks, List<ItemStack> newItemStacks) {
		for (ItemStack newItemStack : newItemStacks) {
			if (isNullOrAir(newItemStack))
				continue;

			final Iterator<ItemStack> iterator = itemStacks.iterator();
			while (iterator.hasNext()) {
				final ItemStack next = iterator.next();
				if (isNullOrAir(next))
					continue;
				if (next.getAmount() >= next.getType().getMaxStackSize())
					continue;
				if (!next.isSimilar(newItemStack))
					continue;

				iterator.remove();
				int amountICanAdd = Math.min(newItemStack.getAmount(), next.getType().getMaxStackSize() - next.getAmount());
				next.setAmount(next.getAmount() + amountICanAdd);
				newItemStack.setAmount(newItemStack.getAmount() - amountICanAdd);
				itemStacks.add(next.clone());
				break;
			}

			if (newItemStack.getAmount() > 0)
				itemStacks.add(newItemStack.clone());
		}
	}

	public static List<ItemStack> nonNullOrAir(ItemStack[] items) {
		if (items == null)
			return Collections.emptyList();

		return nonNullOrAir(Arrays.asList(items));
	}

	public static List<ItemStack> nonNullOrAir(List<ItemStack> items) {
		if (items == null)
			return Collections.emptyList();

		return items.stream().filter(Nullables::isNotNullOrAir).collect(Collectors.toList());
	}

	public static List<ItemStack> getShulkerContents(ItemStack itemStack) {
		return getRawShulkerContents(itemStack).stream().filter(Nullables::isNotNullOrAir).collect(Collectors.toList());
	}

	public static List<ItemStack> getRawShulkerContents(ItemStack itemStack) {
		List<ItemStack> contents = new ArrayList<>();

		if (isNullOrAir(itemStack))
			return contents;

		if (!MaterialTag.SHULKER_BOXES.isTagged(itemStack.getType()))
			return contents;
		
		if (!(itemStack.getItemMeta() instanceof BlockStateMeta meta))
			return contents;

		if (!(meta.getBlockState() instanceof ShulkerBox shulkerBox))
			return contents;

		contents.addAll(Arrays.asList(shulkerBox.getInventory().getContents()));

		return contents;
	}

	public static ItemStack getTool(HasPlayer player, EquipmentSlot hand) {
		return player.getPlayer().getInventory().getItem(hand);
	}

	public static ItemStack getTool(HasPlayer player) {
		return getTool(player, (Material) null);
	}

	public static ItemStack getTool(HasPlayer player, CustomMaterial material) {
		Player _player = player.getPlayer();
		ItemStack mainHand = _player.getInventory().getItemInMainHand();
		ItemStack offHand = _player.getInventory().getItemInOffHand();
		if (!isNullOrAir(mainHand) && (material == null || material.is(mainHand)))
			return mainHand;
		else if (!isNullOrAir(offHand) && (material == null || material.is(offHand)))
			return offHand;
		return null;
	}

	public static ItemStack getTool(HasPlayer player, Material material) {
		Player _player = player.getPlayer();
		ItemStack mainHand = _player.getInventory().getItemInMainHand();
		ItemStack offHand = _player.getInventory().getItemInOffHand();
		if (!isNullOrAir(mainHand) && (material == null || mainHand.getType() == material))
			return mainHand;
		else if (!isNullOrAir(offHand) && (material == null || offHand.getType() == material))
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
		return getHandWithTool(player, null);
	}

	public static EquipmentSlot getHandWithTool(HasPlayer player, Material material) {
		Player _player = player.getPlayer();
		ItemStack mainHand = _player.getInventory().getItemInMainHand();
		ItemStack offHand = _player.getInventory().getItemInOffHand();
		if (!isNullOrAir(mainHand) && (material == null || mainHand.getType() == material))
			return EquipmentSlot.HAND;
		else if (!isNullOrAir(offHand) && (material == null || offHand.getType() == material))
			return EquipmentSlot.OFF_HAND;
		return null;
	}

	public static EquipmentSlot getHandWithToolRequired(HasPlayer player) {
		EquipmentSlot hand = getHandWithTool(player);
		if (hand == null)
			throw new InvalidInputException("You are not holding anything");
		return hand;
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

	public static List<Enchantment> getApplicableEnchantments(ItemStack item) {
		List<Enchantment> applicable = new ArrayList<>();
		for (Enchantment enchantment : Enchant.values()) {
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

	public static ItemStack getItem(Block block) {
		if (Nullables.isNullOrAir(block))
			return null;

		@NotNull Collection<ItemStack> drops = block.getDrops();
		if (Nullables.isNullOrEmpty(drops))
			return null;

		return drops.iterator().next();
	}

	public static ItemStack find(ItemStack[] items, Predicate<ItemStack> predicate) {
		return find(Arrays.asList(items), predicate);
	}

	public static ItemStack find(List<ItemStack> items, Predicate<ItemStack> predicate) {
		for (ItemStack item : items)
			if (predicate.test(item))
				return item;
		return null;
	}

	public static List<ItemStack> fixMaxStackSize(List<ItemStack> items) {
		List<ItemStack> fixed = new ArrayList<>();
		for (ItemStack item : items) {
			if (isNullOrAir(item))
				continue;

			final Material material = item.getType();

			while (item.getAmount() > material.getMaxStackSize()) {
				final ItemStack replacement = item.clone();
				final int moving = Math.min(material.getMaxStackSize(), item.getAmount() - material.getMaxStackSize());
				replacement.setAmount(moving);
				item.setAmount(item.getAmount() - moving);

				fixed.add(replacement);
			}
			fixed.add(item);
		}

		return fixed;
	}

	public static boolean hasLore(ItemStack dye, String line) {
		line = StringUtils.stripColor(line);

		List<String> lore = dye.getItemMeta().getLore();
		if (lore == null || lore.isEmpty())
			return false;

		for (String _line : lore) {
			_line = StringUtils.stripColor(_line);
			if (_line.equals(line))
				return true;
		}

		return false;
	}

	public static void subtract(Player player, ItemStack item) {
		if (item == null)
			return;

		if (!GameModeWrapper.of(player).isCreative())
			item.subtract();
	}

	public static void update(ItemStack item, @Nullable Player player) {
		CustomEnchants.update(item, player);
		MendingIntegrity.update(item, player);

		// keep last
		ItemTagsUtils.update(item);
	}

	public static int getBurnTime(ItemStack itemStack) {
		return AbstractFurnaceBlockEntity.getFuel().getOrDefault(NMSUtils.toNMS(itemStack).getItem(), 0);
	}

	public static class ItemStackComparator implements Comparator<ItemStack> {
		@Override
		public int compare(ItemStack a, ItemStack b) {
			int result = Integer.compare(b.getMaxStackSize(), a.getMaxStackSize());
			if (result != 0) return result;

			result = b.getType().compareTo(a.getType());
			if (result != 0) return result;

			result = Integer.compare(ModelId.of(a), ModelId.of(b));
			if (result != 0) return result;

			result = b.getRarity().compareTo(a.getRarity());
			if (result != 0) return result;

			result = Integer.compare(b.getAmount(), a.getAmount());
			return result;
		}
	}

	public static boolean isSameHead(ItemStack itemStack1, ItemStack itemStack2) {
		if (isNullOrAir(itemStack1) || isNullOrAir(itemStack2)) return false;
		if (itemStack1.getType() != Material.PLAYER_HEAD || itemStack2.getType() != Material.PLAYER_HEAD) return false;
		return Nexus.getHeadAPI().getItemID(itemStack1).equals(Nexus.getHeadAPI().getItemID(itemStack2));
	}

	/**
	 * Get the raw defense points of an armor piece. This (obviously, it's a {@link Material}) does not include enchantments or custom NBT.
	 * @param item armor item
	 * @return defense value in half-bars
	 */
	public static int getDefensePoints(Material item) {
		// there might be a bukkit method for this somewhere but i haven't found it and i don't wanna do NMS
		switch (item) {
			// i think this looks better as an old switch and will make better diffs
			case LEATHER_HELMET:
			case LEATHER_BOOTS:
			case CHAINMAIL_BOOTS:
			case GOLDEN_BOOTS:
				return 1;
			case LEATHER_LEGGINGS:
			case CHAINMAIL_HELMET:
			case IRON_HELMET:
			case IRON_BOOTS:
			case GOLDEN_HELMET:
			case TURTLE_HELMET:
				return 2;
			case LEATHER_CHESTPLATE:
			case DIAMOND_HELMET:
			case DIAMOND_BOOTS:
			case GOLDEN_LEGGINGS:
			case NETHERITE_HELMET:
			case NETHERITE_BOOTS:
				return 3;
			case CHAINMAIL_LEGGINGS:
				return 4;
			case CHAINMAIL_CHESTPLATE:
			case IRON_LEGGINGS:
			case GOLDEN_CHESTPLATE:
				return 5;
			case IRON_CHESTPLATE:
			case DIAMOND_LEGGINGS:
			case NETHERITE_LEGGINGS:
				return 6;
			case DIAMOND_CHESTPLATE:
			case NETHERITE_CHESTPLATE:
				return 8;
			default:
				return 0;
		}
	}

	/**
	 * Get the raw armor toughness points of an armor piece. This (obviously, it's a {@link Material}) does not include enchantments or custom NBT.
	 * @param item armor item
	 * @return armor toughness value
	 */
	public static int getArmorToughness(Material item) {
		// there might be a bukkit method for this somewhere but i haven't found it and i don't wanna do NMS
		switch (item) {
			// i think this looks better as an old switch and will make better diffs
			case DIAMOND_HELMET:
			case DIAMOND_CHESTPLATE:
			case DIAMOND_LEGGINGS:
			case DIAMOND_BOOTS:
				return 2;
			case NETHERITE_HELMET:
			case NETHERITE_CHESTPLATE:
			case NETHERITE_LEGGINGS:
			case NETHERITE_BOOTS:
				return 3;
			default:
				return 0;
		}
	}

	/**
	 * Get the raw knockback resistance of an armor piece. This (obviously, it's a {@link Material}) does not include enchantments or custom NBT.
	 * @param item armor item
	 * @return knockback resistance value
	 */
	public static int getKnockbackResistance(Material item) {
		// there might be a bukkit method for this somewhere but i haven't found it and i don't wanna do NMS
		switch (item) {
			// i think this looks better as an old switch and will make better diffs
			case NETHERITE_HELMET:
			case NETHERITE_CHESTPLATE:
			case NETHERITE_LEGGINGS:
			case NETHERITE_BOOTS:
				return 1;
			default:
				return 0;
		}
	}

	/**
	 * Gets the equipment slot for an armor item
	 * @param item armor item
	 * @return equipment slot, or null if non-armor-item
	 */
	@Nullable
	public static EquipmentSlot getArmorEquipmentSlot(Material item) {
		switch (item) {
			case CHAINMAIL_HELMET:
			case DIAMOND_HELMET:
			case GOLDEN_HELMET:
			case IRON_HELMET:
			case LEATHER_HELMET:
			case NETHERITE_HELMET:
			case TURTLE_HELMET:
			case ZOMBIE_HEAD:
			case SKELETON_SKULL:
			case WITHER_SKELETON_SKULL:
			case CREEPER_HEAD:
			case PLAYER_HEAD:
			case CARVED_PUMPKIN:
				return EquipmentSlot.HEAD;
			case DIAMOND_CHESTPLATE:
			case CHAINMAIL_CHESTPLATE:
			case GOLDEN_CHESTPLATE:
			case IRON_CHESTPLATE:
			case LEATHER_CHESTPLATE:
			case NETHERITE_CHESTPLATE:
				return EquipmentSlot.CHEST;
			case CHAINMAIL_LEGGINGS:
			case DIAMOND_LEGGINGS:
			case GOLDEN_LEGGINGS:
			case IRON_LEGGINGS:
			case LEATHER_LEGGINGS:
			case NETHERITE_LEGGINGS:
				return EquipmentSlot.LEGS;
			case CHAINMAIL_BOOTS:
			case DIAMOND_BOOTS:
			case GOLDEN_BOOTS:
			case IRON_BOOTS:
			case LEATHER_BOOTS:
			case NETHERITE_BOOTS:
				return EquipmentSlot.FEET;
			default:
				return null;
		}
	}

	public static Material getStructureTypeDisplayMaterial(StructureType structureType) {
		if (structureType == StructureType.MINESHAFT) return Material.COBWEB;
		if (structureType == StructureType.VILLAGE) return Material.BELL;
		if (structureType == StructureType.NETHER_FORTRESS) return Material.NETHER_BRICK;
		if (structureType == StructureType.STRONGHOLD) return Material.STONE_BRICKS;
		if (structureType == StructureType.JUNGLE_PYRAMID) return Material.MOSSY_COBBLESTONE;
		if (structureType == StructureType.OCEAN_RUIN) return Material.MAGMA_BLOCK;
		if (structureType == StructureType.DESERT_PYRAMID) return Material.SANDSTONE;
		if (structureType == StructureType.IGLOO) return Material.SNOW_BLOCK;
		if (structureType == StructureType.SWAMP_HUT) return Material.CAULDRON;
		if (structureType == StructureType.OCEAN_MONUMENT) return Material.PRISMARINE;
		if (structureType == StructureType.END_CITY) return Material.PURPUR_BLOCK;
		if (structureType == StructureType.WOODLAND_MANSION) return Material.DARK_OAK_LOG;
		if (structureType == StructureType.BURIED_TREASURE) return Material.CHEST;
		if (structureType == StructureType.SHIPWRECK) return Material.OAK_BOAT;
		if (structureType == StructureType.PILLAGER_OUTPOST) return Material.BIRCH_PLANKS;
		if (structureType == StructureType.NETHER_FOSSIL) return Material.BONE_BLOCK;
		if (structureType == StructureType.RUINED_PORTAL) return Material.OBSIDIAN;
		if (structureType == StructureType.BASTION_REMNANT) return Material.BASALT;
		return null;
	}

	public static Material getDimensionDisplayMaterial(Environment dimension) {
		return switch (dimension) {
			case NORMAL -> Material.GRASS_BLOCK;
			case NETHER -> Material.NETHERRACK;
			case THE_END -> Material.END_STONE;
			default -> null;
		};
	}

	public static final Map<PotionEffectType, String> fixedPotionNames = Map.of(
		PotionEffectType.SLOW, "SLOWNESS",
		PotionEffectType.FAST_DIGGING, "HASTE",
		PotionEffectType.SLOW_DIGGING, "MINING_FATIGUE",
		PotionEffectType.INCREASE_DAMAGE, "STRENGTH",
		PotionEffectType.HEAL, "INSTANT_HEALTH",
		PotionEffectType.HARM, "INSTANT_DAMAGE",
		PotionEffectType.JUMP, "JUMP_BOOST",
		PotionEffectType.CONFUSION, "NAUSEA",
		PotionEffectType.DAMAGE_RESISTANCE, "RESISTANCE"
	);

	public static String getFixedPotionName(PotionEffectType effect) {
		return fixedPotionNames.getOrDefault(effect, effect.getName());
	}

	public static ItemStack setNBTContentsOfNonInventoryItem(ItemStack mainItem, List<ItemStack> itemStacks) {
		NonNullList<net.minecraft.world.item.ItemStack> minecraft = NonNullList.create();
		for (int i = 0; i < itemStacks.size(); i++) {
			if (Nullables.isNullOrAir(itemStacks.get(i)))
				minecraft.add(i, net.minecraft.world.item.ItemStack.EMPTY);
			else
				minecraft.add(i, CraftItemStack.asNMSCopy(itemStacks.get(i)));
		}

		net.minecraft.world.item.ItemStack handle = CraftItemStack.asNMSCopy(mainItem);
		CompoundTag tag = new CompoundTag();

		tag = handle.save(tag).getCompound("tag");

		CompoundTag pe = new CompoundTag();
		if (tag.contains("ProjectEden"))
			pe = tag.getCompound("ProjectEden");

		ContainerHelper.saveAllItems(pe, minecraft);
		tag.put("ProjectEden", pe);

		handle.setTag(tag);

		ItemStack bukkit = handle.getBukkitStack();
		mainItem.setItemMeta(bukkit.getItemMeta());
		return handle.asBukkitCopy();
	}

	public static List<ItemStack> getNBTContentsOfNonInventoryItem(ItemStack backpack, int expectedSize) {
		net.minecraft.world.item.ItemStack handle = CraftItemStack.asNMSCopy(backpack);

		List<ItemStack> bukkit = new ArrayList<>();

		if (!handle.hasTag()) return bukkit;
		if (!handle.getTag().contains("ProjectEden")) return bukkit;
		if (!handle.getTag().getCompound("ProjectEden").contains("Items")) return bukkit;


		NonNullList<net.minecraft.world.item.ItemStack> minecraft = NonNullList.withSize(expectedSize, net.minecraft.world.item.ItemStack.EMPTY);
		ContainerHelper.loadAllItems(handle.getTag().getCompound("ProjectEden"), minecraft);

		for (int i = 0; i < Math.max(expectedSize, minecraft.size()); i++) {
			if (i >= minecraft.size())
				bukkit.add(null);
			else {
				net.minecraft.world.item.ItemStack minecraftItem = minecraft.get(i);
				if (minecraftItem.equals(net.minecraft.world.item.ItemStack.EMPTY))
					bukkit.add(null);
				else
					bukkit.add(minecraftItem.asBukkitCopy());
			}
		}

		return bukkit;
	}

	@Data
	public static class PotionWrapper {
		private List<MobEffectInstance> effects = new ArrayList<>();

		private PotionWrapper() {}

		@NotNull
		public static PotionWrapper of(ItemStack item) {
			if (!(item.getItemMeta() instanceof PotionMeta potionMeta))
				return new PotionWrapper();

			return of(toNMS(potionMeta.getBasePotionData()), potionMeta.getCustomEffects());
		}

		@NotNull
		public static PotionWrapper of(AreaEffectCloudApplyEvent event) {
			final Potion potion = toNMS(event.getEntity().getBasePotionData());
			final List<PotionEffect> customEffects = event.getEntity().getCustomEffects();
			return of(potion, customEffects);
		}

		@NotNull
		public static PotionWrapper of(Potion potion, List<PotionEffect> customEffects) {
			final PotionWrapper wrapper = new PotionWrapper();
			wrapper.getEffects().addAll(potion.getEffects());
			wrapper.getEffects().addAll(customEffects.stream().map(PotionWrapper::toNMS).toList());
			return wrapper;
		}

		public boolean hasNegativeEffects() {
			return effects.stream().anyMatch(effect -> !effect.getEffect().isBeneficial());
		}

		public boolean hasOnlyBeneficialEffects() {
			return !hasNegativeEffects();
		}

		public boolean isSimilar(ItemStack item) {
			return isSimilar(PotionWrapper.of(item));
		}

		public boolean isSimilar(PotionWrapper wrapper) {
			for (MobEffectInstance effect : effects)
				if (!wrapper.getEffects().contains(effect))
					return false;

			for (MobEffectInstance effect : wrapper.getEffects())
				if (!effects.contains(effect))
					return false;

			return true;
		}

		@NotNull
		public static MobEffectInstance toNMS(PotionEffect effect) {
			return new MobEffectInstance(toNMS(effect.getType()), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles());
		}

		@NotNull
		public static MobEffect toNMS(PotionEffectType effect) {
			final MobEffect nmsEffect = BuiltInRegistries.MOB_EFFECT.byId(effect.getId());
			if (nmsEffect != null)
				return nmsEffect;

			throw new InvalidInputException("Unknown potion type " + effect);
		}

		@NotNull
		public static Potion toNMS(PotionData basePotionData) {
			return BuiltInRegistries.POTION.get(ResourceLocation.tryParse(CraftPotionUtil.fromBukkit(basePotionData).name()));
		}
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static abstract class NBTDataType<T> {

		private BiFunction<NBTItem, String, T> getter;
		private TriConsumer<NBTItem, String, T> setter;
		private Function<String, T> converter;

		@Getter
		@AllArgsConstructor
		public enum NBTDataTypeType {
			STRING(StringType.class),
			UUID(UuidType.class),
			BOOLEAN(BooleanType.class),
			BYTE(ByteType.class),
			SHORT(ShortType.class),
			INTEGER(IntegerType.class),
			LONG(LongType.class),
			FLOAT(FloatType.class),
			DOUBLE(DoubleType.class),
//			BYTE_ARRAY(ByteArrayType.class),
			INT_ARRAY(IntArrayType.class),
			;

			private final Class<? extends NBTDataType<?>> clazz;
		}

		public static class StringType extends NBTDataType<String> {
			public StringType() {
				super(NBTItem::getString, NBTItem::setString, String::valueOf);
			}
		}

		public static class UuidType extends NBTDataType<UUID> {
			public UuidType() {
				super(NBTItem::getUUID, NBTItem::setUUID, java.util.UUID::fromString);
			}
		}

		public static class BooleanType extends NBTDataType<Boolean> {
			public BooleanType() {
				super(NBTItem::getBoolean, NBTItem::setBoolean, Boolean::valueOf);
			}
		}

		public static class ByteType extends NBTDataType<Byte> {
			public ByteType() {
				super(NBTItem::getByte, NBTItem::setByte, Byte::valueOf);
			}
		}

		public static class ShortType extends NBTDataType<Short> {
			public ShortType() {
				super(NBTItem::getShort, NBTItem::setShort, Short::valueOf);
			}
		}

		public static class IntegerType extends NBTDataType<Integer> {
			public IntegerType() {
				super(NBTItem::getInteger, NBTItem::setInteger, Integer::valueOf);
			}
		}

		public static class LongType extends NBTDataType<Long> {
			public LongType() {
				super(NBTItem::getLong, NBTItem::setLong, Long::valueOf);
			}
		}

		public static class FloatType extends NBTDataType<Float> {
			public FloatType() {
				super(NBTItem::getFloat, NBTItem::setFloat, Float::valueOf);
			}
		}

		public static class DoubleType extends NBTDataType<Double> {
			public DoubleType() {
				super(NBTItem::getDouble, NBTItem::setDouble, Double::valueOf);
			}
		}

		/* TODO Java doesnt include support for Byte arrays in streams
		public static class ByteArrayType extends NBTDataType<byte[]> {
			public ByteArrayType() {
				super(NBTItem::getByteArray, NBTItem::setByteArray, string -> Arrays.stream(string.split(",")).map(Byte::valueOf).toArray());
			}
		}
		*/

		public static class IntArrayType extends NBTDataType<int[]> {
			public IntArrayType() {
				super(NBTItem::getIntArray, NBTItem::setIntArray, string -> Arrays.stream(string.split(",")).mapToInt(Integer::valueOf).toArray());
			}
		}
	}

}
