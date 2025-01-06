package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemStackConverter extends TypeConverter implements SimpleValueConverter {

	public ItemStackConverter(Mapper mapper) {
		super(ItemStack.class, CraftItemStack.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;

		return BasicDBObject.parse(Json.toString(Json.serialize((ItemStack) value)));
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		return Json.deserializeItemStack(((BasicDBObject) value).toJson());
	}

}
