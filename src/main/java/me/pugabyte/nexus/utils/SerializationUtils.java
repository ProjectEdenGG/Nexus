package me.pugabyte.nexus.utils;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SerializationUtils {

	public static class YML {

		public static Map<String, ItemStack> serializeItems(ItemStack[] itemStacks) {
			Map<String, ItemStack> items = new HashMap<>();
			int slot = 0;
			for (ItemStack item : itemStacks) {
				if (item != null)
					items.put(String.valueOf(slot), item);
				slot++;
			}

			return items;
		}

		public static ItemStack[] deserializeItems(Map<String, Object> items) {
			ItemStack[] inventory = new ItemStack[41];
			if (items == null) return inventory;

			for (Map.Entry<String, Object> item : items.entrySet())
				inventory[Integer.parseInt(item.getKey())] = (ItemStack) item.getValue();

			return inventory;
		}

		public static List<String> serializeMaterialSet(Set<Material> materials) {
			if (materials == null) return null;
			return new ArrayList<String>(){{ addAll(materials.stream().map(Material::name).collect(Collectors.toList())); }};
		}

		public static Set<Material> deserializeMaterialSet(List<String> materials) {
			if (materials == null) return null;
			return materials.stream().map(block -> Material.matchMaterial(block.toUpperCase())).collect(Collectors.toSet());
		}

	}

	public static class JSON {

		/** ITEM STACK */

		public static String serializeItemStack(ItemStack item) {
			Gson gson = new Gson();
			Map<String, Object> serialized = item.serialize();

			serialized.computeIfPresent("meta", ($, itemMeta) -> serializeItemMeta(((ItemMeta) itemMeta).serialize()));
			serialized.computeIfAbsent("amount", $ -> item.getAmount());

			return gson.toJson(gson.toJsonTree(serialized));
		}

		public static ItemStack deserializeItemStack(String value) {
			return deserializeItemStack(new Gson().fromJson(value, Map.class));
		}

		public static ItemStack deserializeItemStack(Map<String, Object> value) {
			value.computeIfPresent("meta", ($, meta) -> {
				fixMetaClasses((Map<String, Object>) meta);
				return deserializeItemMeta((Map<String, Object>) meta);
			});

			ItemStack deserialize = ItemStack.deserialize(value);
			if (deserialize.getAmount() == 0)
				deserialize.setAmount(1);
			return deserialize;
		}

		/** ITEM META */

		public static String serializeItemMeta(ItemMeta itemMeta) {
			Gson gson = new Gson();
			Map<String, Object> serialized = serializeItemMeta(itemMeta.serialize());
			return gson.toJson(gson.toJsonTree(serialized));
		}

		public static Map<String, Object> serializeItemMeta(Map<String, Object> serialized) {
			serialized = new HashMap<>(serialized);
			serialized.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, "ItemMeta");
			return serialized;
		}

		public static ItemMeta deserializeItemMeta(String value) {
			return deserializeItemMeta(new Gson().fromJson(value, Map.class));
		}

		public static ItemMeta deserializeItemMeta(Map<String, Object> meta) {
			return (ItemMeta) ConfigurationSerialization.deserializeObject(meta);
		}

		// MongoDB deserializes some properties as the wrong class, do conversion
		public static void fixMetaClasses(Map<String, Object> deserialized) {
			Arrays.asList("power", "repair-cost", "Damage").forEach(key ->
					deserialized.computeIfPresent(key, ($, metaValue) -> {
						if (metaValue instanceof Number)
							return ((Number) metaValue).intValue();
						return metaValue;
					}));
		}

		/** LOCATION */

		@NotNull
		public static String serializeLocation(Location location) {
			DecimalFormat nf = new DecimalFormat("#.000");
			return location.getWorld().getName() + "," +
					nf.format(location.getX()) + "," +
					nf.format(location.getY()) + "," +
					nf.format(location.getZ()) + "," +
					nf.format(location.getYaw()) + "," +
					nf.format(location.getPitch());
		}

		@NotNull
		public static Location deserializeLocation(String in) {
			List<String> parts = Arrays.asList(in.split(","));
			return new Location(
					Bukkit.getWorld(parts.get(0)),
					Double.parseDouble(parts.get(1)),
					Double.parseDouble(parts.get(2)),
					Double.parseDouble(parts.get(3)),
					Float.parseFloat(parts.get(4)),
					Float.parseFloat(parts.get(5))
			);
		}

	}
}
