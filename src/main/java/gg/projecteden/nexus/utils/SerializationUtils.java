package gg.projecteden.nexus.utils;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.serialization.Dynamic;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import java.util.stream.Collectors;

public class SerializationUtils {

	public static class NBT {

		public static String serializeItemStack(ItemStack itemStack) {
			net.minecraft.world.item.ItemStack nms = NMSUtils.toNMS(itemStack);
			CompoundTag tag = (CompoundTag) nms.save(((CraftServer) Bukkit.getServer()).getServer().registryAccess());
			tag.putInt("DataVersion", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
			return tag.toString();
		}

		public static ItemStack deserializeItemStack(String string) {
			try {
				CompoundTag updated = deserializeItemStackToTagAndUpdate(string);
				net.minecraft.world.item.ItemStack fixed = net.minecraft.world.item.ItemStack.parse(((CraftServer) Bukkit.getServer()).getServer().registryAccess(), updated).orElse(null);
				if (fixed == null)
					throw new RuntimeException("Deserialized item stack from " + string + " is null");
				return fixed.asBukkitCopy();
			} catch (Exception ex) {
				Nexus.warn("Failed to parse ItemStack from String:");
				ex.printStackTrace();
				return null;
			}
		}

		private static CompoundTag deserializeItemStackToTagAndUpdate(String string) {
			try {
				CompoundTag tag = TagParser.parseTag(string);
				return updateItemStack(tag);
			} catch (Exception ex) {
				Nexus.warn("Failed to parse ItemStack from String: " + string);
				throw new RuntimeException(ex);
			}
		}

		@SneakyThrows
		public static CompoundTag updateItemStack(@NonNull CompoundTag data) {
			return (CompoundTag) DataFixers.getDataFixer().update(
				References.ITEM_STACK,
				new Dynamic<>(NbtOps.INSTANCE, data),
				data.contains("DataVersion") ? data.getInt("DataVersion") : 3700,
				SharedConstants.getCurrentVersion().getDataVersion().getVersion()
			).getValue();
		}

		public static ListTag updateItemStacks(ListTag data) {
			ListTag updated = new ListTag();
			for (int i = 0; i < data.size(); i++) {
				CompoundTag item = data.getCompound(i);
				updated.add(updateItemStack(item));
			}
			return updated;
		}

	}

	public static class YML {

		public static ItemStack[] asInventory(Map<String, ItemStack> items) {
			return asArray(items, 41);
		}

		public static ItemStack[] asArray(Map<String, ItemStack> items) {
			if (items == null) return new ItemStack[0];
			return asArray(items, items.keySet().size());
		}

		public static ItemStack[] asArray(Map<String, ItemStack> items, int length) {
			if (items == null) return new ItemStack[0];
			ItemStack[] inventory = new ItemStack[length];

			for (Map.Entry<String, ItemStack> item : items.entrySet())
				inventory[Integer.parseInt(item.getKey())] = item.getValue();

			return inventory;
		}

		public static Map<String, ItemStack> deserializeItemStacks(Map<String, Object> items) {
			Map<String, ItemStack> deserialized = new LinkedHashMap<>();
			for (String key : items.keySet()) {
				var obj = items.get(key);
				if (obj instanceof CraftItemStack itemStack) {
					deserialized.put(key, itemStack);
				} else if (obj instanceof String string) {
					deserialized.put(key, NBT.deserializeItemStack(string));
				} else {
					Nexus.severe("Unknown class for serialized item stack: " + obj.getClass().getSimpleName());
				}
			}
			return deserialized;
		}

		public static Map<String, String> serializeItemStacks(ItemStack[] items) {
			Map<String, String> serialized = new LinkedHashMap<>();
			for (int i = 0; i < items.length; i++) {
				if (!isNullOrAir(items[i]))
					serialized.put(String.valueOf(i), NBT.serializeItemStack(items[i]));
			}
			return serialized;
		}

		public static List<String> serializeMaterialSet(Set<Material> materials) {
			if (materials == null) return null;
			return materials.stream().map(Material::name).sorted().collect(Collectors.toList());
		}

		public static Set<Material> deserializeMaterialSet(List<String> materials) {
			if (materials == null) return null;
			return new LinkedHashSet<>(materials.stream()
					.map(material -> Material.matchMaterial(material.toUpperCase()))
					.filter(Objects::nonNull)
					.sorted()
					.toList());
		}

	}

	public static class Json {

		public static Gson getGson() {
			return Utils.getGson();
		}

		public static String of(Object object) {
			return getGson().toJson(object);
		}

		public static String toString(Map<String, Object> map) {
			return getGson().toJson(getGson().toJsonTree(map, Map.class));
		}

		public static String toString(List<Map<String, Object>> list) {
			return getGson().toJson(getGson().toJsonTree(list, List.class));
		}

		public static Map<String, Object> fromString(String value) {
			return getGson().fromJson(value, Map.class);
		}

		public static List<Map<String, Object>> fromStringToList(String value) {
			return getGson().fromJson(value, List.class);
		}

		/** Bukkit ConfigurationSerializable */

		public static List<Map<String, Object>> serialize(List<? extends ConfigurationSerializable> values) {
			return new ArrayList<>() {{
				for (ConfigurationSerializable value : values)
					add(serialize(value));
			}};
		}

		public static Map<String, Object> serialize(ConfigurationSerializable value) {
			if (value == null) return null;
			Map<String, Object> serialized = serializeRecursive(value.serialize());
			serialized.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(value.getClass()));
			addExtraValues(serialized, value);
			return serialized;
		}

		public static List<Object> deserialize(List<Map<String, Object>> values) {
			return new ArrayList<>() {{
				for (Map<String, Object> value : values)
					add(deserializeRecursive(value));
			}};
		}

		public static List<ItemStack> deserializeItemStacks(String value) {
			return deserializeItemStacks(fromStringToList(value));
		}

		public static List<ItemStack> deserializeItemStacks(List<Map<String, Object>> values) {
			return new ArrayList<>() {{
				for (Map<String, Object> value : values)
					add(deserializeItemStack(value));
			}};
		}

		public static ItemStack deserializeItemStack(String value) {
			return deserializeItemStack(fromString(value));
		}

		public static ItemStack deserializeItemStack(Map<String, Object> value) {
			if (value == null) return null;
			value.computeIfPresent("meta", ($, meta) -> {
				Map<String, Object> metaMap = meta instanceof String ? fromString((String) meta) : (Map<String, Object>) meta;
				fixItemMeta(metaMap);
				return deserializeRecursive(metaMap);
			});

			value.putIfAbsent("v", 2586);

			ItemStack deserialize = ItemStack.deserialize(value);
			if (deserialize.getAmount() == 0)
				deserialize.setAmount(1);
			return deserialize;
		}

		private static Map<String, Object> serializeRecursive(Map<String, Object> serialized) {
			Map<String, Object> fixed = new HashMap<>(serialized);
			serialized.forEach((key, value) -> fixed.put(key, serializeRecursive(value)));
			return fixed;
		}

		private static Object serializeRecursive(Object value) {
			if (value == null)
				return null;

			if (value instanceof ConfigurationSerializable configurationSerializable)
				return serialize(configurationSerializable);

			if (Collection.class.isAssignableFrom(value.getClass()))
				if (((Collection<?>) value).iterator().hasNext())
					return ImmutableList.copyOf(new ArrayList<>() {{
						for (Object object : ((Collection<?>) value))
							add(serializeRecursive(object));
					}});

			if (Map.class.isAssignableFrom(value.getClass()))
				return serializeRecursive((Map<String, Object>) value);

			return value;
		}

		private static Object deserializeRecursive(Map<String, Object> values) {
			Map<String, Object> fixed = new HashMap<>(values);
			values.forEach(((key, value) -> {
				if (Collection.class.isAssignableFrom(value.getClass())) {
					fixed.put(key, new ArrayList<>() {{
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

			if (fixed.containsKey("==")) {
				try { fixItemMeta(fixed); } catch (ClassCastException ignore) {}
				return ConfigurationSerialization.deserializeObject(fixed);
			}

			return fixed;
		}

		private static void addExtraValues(Map<String, Object> serialized, ConfigurationSerializable value) {
			if (value instanceof ItemStack itemStack)
				serialized.computeIfAbsent("amount", $ -> itemStack.getAmount());
		}

		private static final List<String> intKeys = Arrays.asList("power", "repair-cost", "Damage", "map-id", "generation", "custom-model-data",
				"effect", "duration", "amplifier", "fish-variant", "LodestonePosX", "LodestonePosY", "LodestonePosZ", "axolotl-variant");
		private static void fixItemMeta(Map<String, Object> deserialized) {
			if (deserialized.containsKey("skull-owner")) {
				Map<String, Object> skulLOwner = (Map<String, Object>) deserialized.get("skull-owner");
				if (skulLOwner.containsKey("name")) {
					skulLOwner.put("name", ((String) skulLOwner.get("name")).replaceAll(" ", ""));
				}
			}

			// MongoDB deserializes some properties as the wrong class, do conversion
			intKeys.forEach(key ->
					deserialized.computeIfPresent(key, ($, metaValue) -> {
						if (metaValue instanceof Number number)
							return number.intValue();
						return metaValue;
					}));

			Arrays.asList("enchants", "stored-enchants", "display-map-color").forEach(key ->
					deserialized.computeIfPresent(key, ($, metaValue) ->
							toIntMap((Map<String, Object>) metaValue)));
		}

		private static Map<String, Object> toIntMap(Map<String, Object> map) {
			return new HashMap<>() {{
				map.forEach((key, value) -> {
					put(key, value);
					if (value instanceof Number number)
						put(key, number.intValue());
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

		public static class LocationGsonSerializer implements JsonSerializer<Location> {

			@Override
			public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {
				return new JsonPrimitive(StringUtils.getShortishLocationString(location));
			}

		}

		public static class LocalDateGsonSerializer implements JsonSerializer<LocalDate> {

			@Override
			public JsonElement serialize(LocalDate timestamp, Type type, JsonSerializationContext context) {
				return new JsonPrimitive(DateTimeFormatter.ISO_LOCAL_DATE.format(timestamp));
			}

		}

		public static class LocalDateTimeGsonSerializer implements JsonSerializer<LocalDateTime> {

			@Override
			public JsonElement serialize(LocalDateTime timestamp, Type type, JsonSerializationContext context) {
				return new JsonPrimitive(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(timestamp));
			}

		}

	}
}
