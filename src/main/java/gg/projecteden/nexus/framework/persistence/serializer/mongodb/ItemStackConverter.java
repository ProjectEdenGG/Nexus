package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import gg.projecteden.nexus.utils.SerializationUtils.NBT;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemStackConverter extends TypeConverter implements SimpleValueConverter {

	public ItemStackConverter(Mapper mapper) {
		super(ItemStack.class, CraftItemStack.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		return NBT.serializeItemStack((ItemStack) value);
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null)
			return null;

		if (value instanceof String string)
			return NBT.deserializeItemStack(string);
		else if (value instanceof BasicDBObject object)
			return Json.deserializeItemStack(object.toJson());

		throw new RuntimeException("Unknown class for serialized item stack " + value.getClass().getSimpleName());
	}

}
