package me.pugabyte.bncore.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.utils.SerializationUtils.json_deserializeItem;
import static me.pugabyte.bncore.utils.SerializationUtils.json_serializeItem;

public class ItemStackConverter extends TypeConverter {

	public ItemStackConverter() {
		super(ItemStack.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null)
			return null;

		return BasicDBObject.parse(json_serializeItem((ItemStack) value));
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		return json_deserializeItem(((BasicDBObject) value).toJson());
	}

}
