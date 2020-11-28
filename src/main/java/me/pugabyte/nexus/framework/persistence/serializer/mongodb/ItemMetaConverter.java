package me.pugabyte.nexus.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import me.pugabyte.nexus.utils.SerializationUtils.JSON;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemMetaConverter extends TypeConverter implements SimpleValueConverter {

	public ItemMetaConverter(Mapper mapper) {
		super(ItemMeta.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		return BasicDBObject.parse(JSON.serializeItemMeta((ItemMeta) value));
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		return JSON.deserializeItemMeta(((BasicDBObject) value).toMap());
	}

}
