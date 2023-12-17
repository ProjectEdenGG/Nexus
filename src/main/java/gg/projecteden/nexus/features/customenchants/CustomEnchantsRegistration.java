package gg.projecteden.nexus.features.customenchants;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.models.CraftCustomEnchant;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.features.customenchants.models.NMSCustomEnchant;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
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
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.ReflectionUtils.subTypesOf;
import static gg.projecteden.nexus.utils.NMSUtils.setStaticFinal;

public class CustomEnchantsRegistration {
	static final Field frozen;
	static final Field unregisteredIntrusiveHolders;
	static final Field registriesField;
	static final HashMap<Class<?>, Registry<?>> registries;
	static final Set<NamespacedKey> vanillaEnchantments;

	static {
		try {
			frozen = Arrays.stream(MappedRegistry.class.getDeclaredFields()).filter(field -> field.getType().isPrimitive()).findFirst().orElse(null);
			unregisteredIntrusiveHolders = Arrays.stream(MappedRegistry.class.getDeclaredFields()).filter(field -> field.getType() == Map.class).findFirst().orElse(null);
			registriesField = CraftServer.class.getDeclaredField("registries");

			frozen.setAccessible(true);
			unregisteredIntrusiveHolders.setAccessible(true);
			registriesField.setAccessible(true);

			registries = (HashMap<Class<?>, Registry<?>>) registriesField.get(Bukkit.getServer());
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
			throw new RuntimeException(e);
		}
	}

	public static void register() {
		try {
			var server = (CraftServer) Bukkit.getServer();
			var registry = server.getHandle().getServer().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
			new CraftRegistry<>(Enchantment.class, registry, (key, $) -> CustomEnchants.get(key.key()));
			registries.put(Enchantment.class, registry);
			setStaticFinal(org.bukkit.Registry.class.getDeclaredField("ENCHANTMENT"), registry);
			frozen.set(BuiltInRegistries.ENCHANTMENT, false);
			unregisteredIntrusiveHolders.set(BuiltInRegistries.ENCHANTMENT,
				new IdentityHashMap<net.minecraft.world.item.enchantment.Enchantment,
					Holder.Reference<net.minecraft.world.item.enchantment.Enchantment>>());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void unregister() {
		/*
		You can't unregister from a minecraft registry, so we simply leave the stale reference there.
		This shouldn't cause many issues in production as the bukkit registry is replaced on each reload.
		*/
	}

	@SneakyThrows
	static Enchantment register(CustomEnchant customEnchant) {
		final String id = customEnchant.getId();
		final NamespacedKey nmsKey = NamespacedKey.minecraft(id);
		final ResourceLocation resourceLocation = CraftNamespacedKey.toMinecraft(nmsKey);
		if (BuiltInRegistries.ENCHANTMENT.containsKey(resourceLocation)) {
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
		final NamespacedKey key = NamespacedKey.fromString(id, Nexus.getInstance());
		if (key == null)
			throw new InvalidInputException("[CustomEnchants] Could not generate NamespacedKey for " + id);
		return key;
	}

	private static void setAcceptingNew() throws NoSuchFieldException, IllegalAccessException {
		Field fieldAcceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
		fieldAcceptingNew.setAccessible(true);
		fieldAcceptingNew.set(null, true);
	}

	private static Set<Class<? extends CustomEnchant>> getClasses() {
		return subTypesOf(CustomEnchant.class, getEnchantsPackage());
	}

	@NotNull
	private static String getEnchantsPackage() {
		return CustomEnchants.class.getPackageName() + ".enchants";
	}

	@SneakyThrows
	private static Map<NamespacedKey, Enchantment> getByKey() {
		Field fieldByKey = Enchantment.class.getDeclaredField("byKey");
		fieldByKey.setAccessible(true);
		return (Map<NamespacedKey, Enchantment>) fieldByKey.get(null);
	}

	@SneakyThrows
	private static Map<String, Enchantment> getByName() {
		Field fieldByName = Enchantment.class.getDeclaredField("byName");
		fieldByName.setAccessible(true);
		return (Map<String, Enchantment>) fieldByName.get(null);
	}

}
