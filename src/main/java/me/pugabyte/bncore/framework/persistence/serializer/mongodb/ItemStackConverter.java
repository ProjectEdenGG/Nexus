package me.pugabyte.bncore.framework.persistence.serializer.mongodb;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemStackConverter extends TypeConverter {

	public ItemStackConverter() {
		super(ItemStack.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null)
			return null;

		Gson gson = new Gson();
		ItemStack itemStack = (ItemStack) value;
		Map<String, Object> serialized = itemStack.serialize();

		serialized.computeIfPresent("meta", ($, itemMeta) -> {
			Map<String, Object> meta = new HashMap<>(((ItemMeta) itemMeta).serialize());
			meta.put("==", "ItemMeta");
			return meta;
		});

		String json = gson.toJson(gson.toJsonTree(serialized));
		return BasicDBObject.parse(json);
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		Gson gson = new Gson();
		String json = ((BasicDBObject) value).toJson();
		Map<String, Object> deserialized = gson.fromJson(json, Map.class);

		fixClasses(deserialized);

		deserialized.computeIfPresent("meta", ($, meta) ->
				ConfigurationSerialization.deserializeObject((Map<String, Object>) meta));

		return ItemStack.deserialize(deserialized);
	}

	// MongoDB deserializes some properties as the wrong class, do conversion
	public void fixClasses(Map<String, Object> deserialized) {
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

}
