package gg.projecteden.nexus.features.customenchants;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.models.CraftCustomEnchant;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.features.customenchants.models.NMSCustomEnchant;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.SneakyThrows;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class CustomEnchantsRegistration {
	static final Field nmsFrozenField;
	static final Field unregisteredIntrusiveHolders;
	static final Field registriesField;
	static final HashMap<Class<?>, org.bukkit.Registry<?>> craftRegistries;
	static final Set<NamespacedKey> vanillaEnchantments;

	static {
		try {
			Nexus.debug("Setting up custom enchant registry 1");

			printRegistryContents("1");

			nmsFrozenField = Arrays.stream(MappedRegistry.class.getDeclaredFields()).filter(field -> field.getType().isPrimitive()).findFirst().orElse(null);
			unregisteredIntrusiveHolders = Arrays.stream(MappedRegistry.class.getDeclaredFields()).filter(field -> field.getType() == Map.class).reduce((f, s) -> s).orElse(null);
			registriesField = CraftServer.class.getDeclaredField("registries");

			nmsFrozenField.setAccessible(true);
			unregisteredIntrusiveHolders.setAccessible(true);
			registriesField.setAccessible(true);

			craftRegistries = (HashMap<Class<?>, org.bukkit.Registry<?>>) registriesField.get(Bukkit.getServer());
			vanillaEnchantments = Arrays.stream(Enchantments.class.getDeclaredFields())
				.filter(field -> field.getType() == net.minecraft.world.item.enchantment.Enchantment.class)
				.map(field -> {
					try {
						final var enchantment = (net.minecraft.world.item.enchantment.Enchantment) field.get(null);
						final ResourceLocation key = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
						if (key == null)
							return null;
						return CraftNamespacedKey.fromMinecraft(key);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		} catch (Exception e) {
			Nexus.severe("Error setting up custom enchant registry 1");
			throw new RuntimeException(e);
		}
	}

	public static void register() {
		try {
			if (!(boolean) nmsFrozenField.get(BuiltInRegistries.ENCHANTMENT)) // Don't replace if we already have (reloads)
				return;
			Nexus.debug("Setting up custom enchant registry 2");
			printRegistryContents("2");
			var craftRegistry = new CraftRegistry<>(Enchantment.class, nmsRegistry(), (key, handle) -> {
				Nexus.debug("Converting " + key.getKey() + " to CraftEnchantment");
				if (handle == null)
					Nexus.debug("Handle is null");
				else
					Nexus.debug("handle classname: %s, category: %s, tostring: %s".formatted(handle.getClass().getSimpleName(), handle.category, handle.toString()));

				if (vanillaEnchantments.contains(key))
					return new CraftEnchantment(key, handle);
				else
					return CustomEnchants.get(key.key());
			});
			craftRegistries.put(Enchantment.class, craftRegistry);
			NMSUtils.setStaticFinal(org.bukkit.Registry.class.getDeclaredField("ENCHANTMENT"), craftRegistry);
			printRegistryContents("2.5");
			nmsFrozenField.set(BuiltInRegistries.ENCHANTMENT, false);
			unregisteredIntrusiveHolders.set(BuiltInRegistries.ENCHANTMENT,
				new IdentityHashMap<net.minecraft.world.item.enchantment.Enchantment,
					Holder.Reference<net.minecraft.world.item.enchantment.Enchantment>>());
			printRegistryContents("3");
		} catch (Exception ex) {
			Nexus.severe("Error setting up custom enchant registry 2");
			ex.printStackTrace();
		}
	}

	@NotNull
	public static Registry<net.minecraft.world.item.enchantment.Enchantment> nmsRegistry() {
		return ((CraftServer) Bukkit.getServer()).getHandle().getServer().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
	}

	public static void unregister() {
		/*
		You can't unregister from a minecraft registry, so we simply leave the stale reference there.
		This shouldn't cause many issues in production as the bukkit registry is replaced on each reload.
		*/
	}

	@SneakyThrows
	static Enchantment register(CustomEnchant customEnchant) {
		Nexus.debug("Registering " + customEnchant.getClass().getSimpleName());
		final String id = customEnchant.getId();
		final NamespacedKey nmsKey = NamespacedKey.minecraft(id);
		final ResourceLocation resourceLocation = CraftNamespacedKey.toMinecraft(nmsKey);
		if (BuiltInRegistries.ENCHANTMENT.containsKey(resourceLocation)) {
			if (customEnchant instanceof Listener listener)
				Nexus.registerListener(listener);

			var nms = BuiltInRegistries.ENCHANTMENT.get(resourceLocation);
			if (nms != null) {
				return new CraftCustomEnchant(customEnchant, nms);
			} else {
				throw new IllegalStateException("Enchantment " + id + " wasn't registered");
			}
		}

		Registry.register(BuiltInRegistries.ENCHANTMENT, id, new NMSCustomEnchant(customEnchant));

		return register(customEnchant);
	}

	@NotNull
	static NamespacedKey getKey(String id) {
		final NamespacedKey key = NamespacedKey.minecraft(id);
		if (key == null)
			throw new InvalidInputException("[CustomEnchants] Could not generate NamespacedKey for " + id);
		return key;
	}

	public static void printRegistryContents(String number) {
		Nexus.debug("Registry contents " + number + ":");
		final String nmsEnchants = nmsRegistry().stream().map(enchantment -> {
			final ResourceLocation resourceLocation = Objects.requireNonNull(BuiltInRegistries.ENCHANTMENT.getKey(enchantment));
			return resourceLocation + "/" + BuiltInRegistries.ENCHANTMENT.get(resourceLocation);
		}).collect(Collectors.joining(","));
		final String bukkitEnchants = org.bukkit.Registry.ENCHANTMENT.stream().map(enchantment -> enchantment == null ? "null" : enchantment.getKey().getKey()).collect(Collectors.joining(","));
		Nexus.debug("  nmsEnchants:" + nmsEnchants);
		Nexus.debug("  bukkitEnchants:" + bukkitEnchants);
	}

}
