package me.pugabyte.bncore.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import me.pugabyte.bncore.utils.SerializationUtils.JSON;
import org.bukkit.inventory.ItemStack;

public class ItemStackConverter extends TypeConverter implements SimpleValueConverter {

	public ItemStackConverter(Mapper mapper) {
		super(ItemStack.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null)
			return null;

		return BasicDBObject.parse(JSON.serializeItemStack((ItemStack) value));
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		return JSON.deserializeItemStack(((BasicDBObject) value).toJson());
	}

}
