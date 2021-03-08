package me.pugabyte.nexus.utils;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

		public static String toString(Map<String, Object> map) {
			Gson gson = new Gson();
			return gson.toJson(gson.toJsonTree(map, Map.class));
		}

		public static String toString(Map<String, Object>[] map) {
			Gson gson = new Gson();
			return gson.toJson(gson.toJsonTree(map, Map.class));
		}

		public static Map<String, Object> fromString(String value) {
			return new Gson().fromJson(value, Map.class);
		}

		/** Bukkit ConfigurationSerializable */

		public static Map<String, Object>[] serialize(ConfigurationSerializable[] values) {
			Map<String, Object>[] hashMapArray = (HashMap<String, Object>[]) new HashMap[values.length];
			for (int i = 0; i < values.length; i++)
				hashMapArray[i] = serialize(values[i]);
			return hashMapArray;
		}

		public static Map<String, Object> serialize(ConfigurationSerializable value) {
			Map<String, Object> serialized = serializeRecursive(value.serialize());
			serialized.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(value.getClass()));
			return serialized;
		}

		public static Map<String, Object> serializeItemStack(ItemStack item) {
			Map<String, Object> serialized = serializeRecursive(item.serialize());
			serialized.computeIfAbsent("amount", $ -> item.getAmount());
			return serialized;
		}

		public static ItemStack deserializeItemStack(String value) {
			return deserializeItemStack(fromString(value));
		}

		public static ItemStack deserializeItemStack(Map<String, Object> value) {
			value.computeIfPresent("meta", ($, meta) -> {
				Map<String, Object> metaMap = meta instanceof String ? fromString((String) meta) : (Map<String, Object>) meta;
				fixMetaClasses(metaMap);
				return deserializeRecursive(metaMap);
			});

			ItemStack deserialize = ItemStack.deserialize(value);
			if (deserialize.getAmount() == 0)
				deserialize.setAmount(1);
			return deserialize;
		}

		public static Map<String, Object> serializeRecursive(Map<String, Object> serialized) {
			Map<String, Object> fixed = new HashMap<>(serialized);
			serialized.forEach((key, value) -> fixed.put(key, serializeRecursive(value)));
			return fixed;
		}

		private static Object serializeRecursive(Object value) {
			if (value == null)
				return null;

			if (value instanceof ConfigurationSerializable)
				return serialize((ConfigurationSerializable) value);

			if (Collection.class.isAssignableFrom(value.getClass()))
				if (((Collection<?>) value).iterator().hasNext())
					return ImmutableList.copyOf(new ArrayList<Object>() {{
						for (Object object : ((Collection<?>) value))
							add(serializeRecursive(object));
					}});

			if (Map.class.isAssignableFrom(value.getClass()))
				return serializeRecursive((Map<String, Object>) value);

			return value;
		}

		private static Object deserializeRecursive(Map<String, Object> meta) {
			Map<String, Object> fixed = new HashMap<>(meta);
			meta.forEach(((key, value) -> {
				if (Collection.class.isAssignableFrom(value.getClass())) {
					fixed.put(key, new ArrayList<Object>() {{
						for (Object next : (Collection<?>) value) {
							if (next == null || !Map.class.isAssignableFrom(next.getClass())) {
								add(next);
								continue;
							}

							add(deserializeRecursive((Map<String, Object>) next));
						}
					}});
				}

				if (Map.class.isAssignableFrom(value.getClass()))
					fixed.put(key, deserializeRecursive((Map<String, Object>) value));
			}));

			if (fixed.containsKey("=="))
				return ConfigurationSerialization.deserializeObject(fixed);

			return fixed;
		}

		// MongoDB deserializes some properties as the wrong class, do conversion
		private static void fixMetaClasses(Map<String, Object> deserialized) {
			Arrays.asList("power", "repair-cost", "Damage", "map-id", "generation", "effect", "custom-model-data").forEach(key ->
					deserialized.computeIfPresent(key, ($, metaValue) -> {
						if (metaValue instanceof Number)
							return ((Number) metaValue).intValue();
						return metaValue;
					}));

			Arrays.asList("enchants", "display-map-color").forEach(key ->
					deserialized.computeIfPresent(key, ($, metaValue) ->
							toIntMap((Map<String, Object>) metaValue)));
		}

		private static Map<String, Object> toIntMap(Map<String, Object> map) {
			return new HashMap<String, Object>() {{
				map.forEach((key, value) -> {
					put(key, value);
					if (value instanceof Number)
						put(key, ((Number) value).intValue());
				});
			}};
		}

		/** Location */

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
