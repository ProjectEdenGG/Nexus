package me.pugabyte.bncore.utils;

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
	public static Map<String, ItemStack> yml_serializeItems(ItemStack[] itemStacks) {
		Map<String, ItemStack> items = new HashMap<>();
		int slot = 0;
		for (ItemStack item : itemStacks) {
			if (item != null)
				items.put(String.valueOf(slot), item);
			slot++;
		}

		return items;
	}

	public static ItemStack[] yml_deserializeItems(Map<String, Object> items) {
		ItemStack[] inventory = new ItemStack[41];
		if (items == null) return inventory;

		for (Map.Entry<String, Object> item : items.entrySet())
			inventory[Integer.parseInt(item.getKey())] = (ItemStack) item.getValue();

		return inventory;
	}

	public static List<String> yml_serializeMaterialSet(Set<Material> materials) {
		if (materials == null) return null;
		return new ArrayList<String>(){{ addAll(materials.stream().map(Material::name).collect(Collectors.toList())); }};
	}

	public static Set<Material> yml_deserializeMaterialSet(List<String> materials) {
		if (materials == null) return null;
		return materials.stream().map(block -> Material.valueOf(block.toUpperCase())).collect(Collectors.toSet());
	}


	public static String json_serializeItem(ItemStack item) {
		Gson gson = new Gson();
		Map<String, Object> serialized = item.serialize();

		serialized.computeIfPresent("meta", ($, itemMeta) -> {
			Map<String, Object> meta = new HashMap<>(((ItemMeta) itemMeta).serialize());
			meta.put("==", "ItemMeta");
			return meta;
		});

		return gson.toJson(gson.toJsonTree(serialized));
	}

	@NotNull
	public static ItemStack json_deserializeItem(String value) {
		return json_deserializeItem(new Gson().fromJson(value, Map.class));
	}

	@NotNull
	public static ItemStack json_deserializeItem(Map<String, Object> value) {
		fixItemClasses(value);

		value.computeIfPresent("meta", ($, meta) ->
				ConfigurationSerialization.deserializeObject((Map<String, Object>) meta));

		return ItemStack.deserialize(value);
	}

	// MongoDB deserializes some properties as the wrong class, do conversion
	public static void fixItemClasses(Map<String, Object> deserialized) {
		deserialized.computeIfPresent("meta", ($, meta) -> {
			Arrays.asList("power", "repair-cost").forEach(key ->
					((Map<String, Object>) meta).computeIfPresent(key, ($2, metaValue) -> {
						if (metaValue instanceof Number)
							return ((Number) metaValue).intValue();
						return metaValue;
					}));
			return meta;
		});
	}

	@NotNull
	public static String json_serializeLocation(Location location) {
		DecimalFormat nf = new DecimalFormat("#.000");
		return location.getWorld().getName() + "," +
				nf.format(location.getX()) + "," +
				nf.format(location.getY()) + "," +
				nf.format(location.getZ()) + "," +
				nf.format(location.getYaw()) + "," +
				nf.format(location.getPitch());
	}

	@NotNull
	public static Location json_deserializeLocation(String in) {
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
