package me.pugabyte.bncore.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import me.pugabyte.bncore.utils.SerializationUtils.JSON;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemMetaConverter extends TypeConverter {

	public ItemMetaConverter() {
		super(ItemMeta.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null)
			return null;

		return BasicDBObject.parse(JSON.serializeItemMeta((ItemMeta) value));
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		return JSON.deserializeItemMeta(((BasicDBObject) value).toMap());
	}

}
