package gg.projecteden.nexus.features.customenchants;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Debug;
import lombok.SneakyThrows;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomEnchantsRegistration {
	static final Field nmsFrozenField;
	static final Field unregisteredIntrusiveHolders;

	static {
		try {
			Debug.log("Setting up custom enchant registry 1");

			nmsFrozenField = MappedRegistry.class.getDeclaredField("frozen");
			unregisteredIntrusiveHolders = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");

			nmsFrozenField.setAccessible(true);
			unregisteredIntrusiveHolders.setAccessible(true);
		} catch (Exception e) {
			Nexus.severe("Error setting up custom enchant registry 1");
			throw new RuntimeException(e);
		}
	}

	public static void unfreeze() {
		try {
			if (!(boolean) nmsFrozenField.get(nmsRegistry()))
				return;

			nmsFrozenField.set(nmsRegistry(), false);
			unregisteredIntrusiveHolders.set(nmsRegistry(), new IdentityHashMap<>());

			nmsFrozenField.set(nmsItemRegistry(), false);
			unregisteredIntrusiveHolders.set(nmsItemRegistry(), new IdentityHashMap<>());
		} catch (Exception ex) {
			Nexus.severe("Error setting up custom enchant registry");
			ex.printStackTrace();
		}
	}

	public static void freeze() {
		try {
			if ((boolean) nmsFrozenField.get(nmsRegistry()))
				return;

			freeze(nmsItemRegistry());
			freeze(nmsRegistry());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> void freeze(MappedRegistry<T> registry) {
		try {
			Object tagSet = getAllTags(registry);

			Map<TagKey<T>, HolderSet.Named<T>> tagsMap = getTagsMap(tagSet);
			// Get 'frozenTags' map with all tags of the registry.
			Map<TagKey<T>, HolderSet.Named<T>> frozenTags = getFrozenTags(registry);

			tagsMap.forEach(frozenTags::putIfAbsent);

			Class<?> clazz = Class.forName(MappedRegistry.class.getName() + "$TagSet");

			Method method = clazz.getMethod("unbound");
			method.setAccessible(true);
			Object unbound = method.invoke(registry);

			Field allTags = MappedRegistry.class.getDeclaredField("allTags");
			allTags.setAccessible(true);
			allTags.set(registry, unbound);

			registry.freeze();

			frozenTags.forEach(tagsMap::putIfAbsent);

			Field valMapField = tagSet.getClass().getDeclaredField("val$map");
			valMapField.setAccessible(true);
			valMapField.set(tagSet, tagsMap);

			allTags.set(registry, tagSet);

		} catch (NoSuchMethodException | NoSuchFieldException | ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
			Nexus.severe("Could not unbound custom enchant registry");
			e.printStackTrace();
		}
	}

	public static MappedRegistry<net.minecraft.world.item.enchantment.Enchantment> nmsRegistry() {
		return (MappedRegistry<net.minecraft.world.item.enchantment.Enchantment>) ((CraftServer) Bukkit.getServer()).getHandle().getServer().registryAccess().lookup(Registries.ENCHANTMENT).orElse(null);
	}

	public static MappedRegistry<net.minecraft.world.item.Item> nmsItemRegistry() {
		return (MappedRegistry<Item>) ((CraftServer) Bukkit.getServer()).getHandle().getServer().registryAccess().lookup(Registries.ITEM).orElse(null);
	}

	@SneakyThrows
	static Enchantment register(CustomEnchant customEnchant) {
		Optional<Holder.Reference<net.minecraft.world.item.enchantment.Enchantment>> lookup = nmsRegistry().get(getResourceKey(nmsRegistry(), customEnchant.getId()));
		if (lookup.isPresent()) {
			if (customEnchant instanceof Listener listener)
				Nexus.registerListener(listener);

			return CraftEnchantment.minecraftToBukkit(lookup.get().value());
		}

		unfreeze();

		Component display = Component.translatable("enchantment.nexus." + customEnchant.getId(), customEnchant.getName());
		HolderSet.Named<Item> supported = createItemsSet("enchant_supported", customEnchant);
		HolderSet.Named<Item> primary = createItemsSet("enchant_primary", customEnchant);

		int weight = customEnchant.getWeight();
		int maxLevel = customEnchant.getMaxLevel();
		net.minecraft.world.item.enchantment.Enchantment.Cost minCost = new net.minecraft.world.item.enchantment.Enchantment.Cost(customEnchant.getMinModifiedCost(0), 1);
		net.minecraft.world.item.enchantment.Enchantment.Cost maxCost = new net.minecraft.world.item.enchantment.Enchantment.Cost(customEnchant.getMaxModifiedCost(0), 1);
		int anvilCost = customEnchant.getAnvilCost();
		EquipmentSlotGroup[] slotGroup = getNMSSlots();

		net.minecraft.world.item.enchantment.Enchantment.EnchantmentDefinition definition = net.minecraft.world.item.enchantment.Enchantment.definition(
			supported, primary, weight, maxLevel, minCost, maxCost, anvilCost, slotGroup
		);
		HolderSet<net.minecraft.world.item.enchantment.Enchantment> exclusiveSet = createExclusiveSet(customEnchant);
		net.minecraft.world.item.enchantment.Enchantment enchantment = new net.minecraft.world.item.enchantment.Enchantment(
			display, definition, exclusiveSet, DataComponentMap.builder().build()
		);

		nmsRegistry().createIntrusiveHolder(enchantment);
		Registry.register(nmsRegistry(), customEnchant.getId(), enchantment);

		freeze();

		return CraftEnchantment.minecraftToBukkit(enchantment);
	}

	static HolderSet.Named<Item> createItemsSet(@NotNull String prefix, @NotNull CustomEnchant customEnchantment) {
		TagKey<Item> customKey = getTagKey(nmsItemRegistry(), prefix + "/" + customEnchantment.getId());
		List<Holder<Item>> holders = new ArrayList<>();

		if (customEnchantment.getSupportedMaterials() != null && !customEnchantment.getSupportedMaterials().isEmpty())
			customEnchantment.getSupportedMaterials().forEach(material -> {
				ResourceLocation location = CraftNamespacedKey.toMinecraft(material.getKey());
				Holder.Reference<Item> holder = nmsItemRegistry().get(location).orElse(null);
				if (holder == null) return;

				holders.add(holder);
			});

		// Creates new tag, puts it in the 'frozenTags' map and binds holders to it.
		nmsItemRegistry().bindTag(customKey, holders);

		return getFrozenTags(nmsItemRegistry()).get(customKey);
	}

	static <T> TagKey<T> getTagKey(@NotNull Registry<T> registry, @NotNull String name) {
		return TagKey.create(registry.key(), ResourceLocation.withDefaultNamespace(name));
	}

	@SuppressWarnings("unchecked")
	static <T> Map<TagKey<T>, HolderSet.Named<T>> getFrozenTags(@NotNull MappedRegistry<T> registry) {
		try {
			Field field = MappedRegistry.class.getDeclaredField("frozenTags");
			field.setAccessible(true);
			return (Map<TagKey<T>, HolderSet.Named<T>>) field.get(registry);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	static EquipmentSlotGroup[] getNMSSlots() {
		return Arrays.stream(EquipmentSlot.values()).map(slot -> CraftEquipmentSlot.getNMSGroup(slot.getGroup())).distinct().toArray(EquipmentSlotGroup[]::new);
	}

	static HolderSet.Named<net.minecraft.world.item.enchantment.Enchantment> createExclusiveSet(@NotNull CustomEnchant customEnchantment) {
		TagKey<net.minecraft.world.item.enchantment.Enchantment> customKey = getTagKey(nmsRegistry(), "exclusive_set/" + customEnchantment.getId());
		List<Holder<net.minecraft.world.item.enchantment.Enchantment>> holders = new ArrayList<>();

		// Creates new tag, puts it in the 'frozenTags' map and binds holders to it.
		nmsRegistry().bindTag(customKey, holders);

		return getFrozenTags(nmsRegistry()).get(customKey);
	}

	static <T> Object getAllTags(@NotNull MappedRegistry<T> registry) {
		try {
			Field field = MappedRegistry.class.getDeclaredField("allTags");
			field.setAccessible(true);
			return field.get(registry);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	static <T> Map<TagKey<T>, HolderSet.Named<T>> getTagsMap(@NotNull Object tagSet) {
		// new HashMap, because original is ImmutableMap.
		try {
			Field field = tagSet.getClass().getDeclaredField("val$map");
			field.setAccessible(true);
			return new HashMap<>((Map<TagKey<T>, HolderSet.Named<T>>) field.get(tagSet));
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	private static <T> ResourceKey<T> getResourceKey(@NotNull Registry<T> registry, @NotNull String name) {
		return ResourceKey.create(registry.key(), ResourceLocation.withDefaultNamespace(name));
	}

	@NotNull
	static NamespacedKey getKey(String id) {
		final NamespacedKey key = NamespacedKey.minecraft(id);
		if (key == null)
			throw new InvalidInputException("[CustomEnchants] Could not generate NamespacedKey for " + id);
		return key;
	}

}
