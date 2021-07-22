package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import net.md_5.bungee.api.ChatColor;

public class ChatColorConverter extends TypeConverter implements SimpleValueConverter {

	public ChatColorConverter(Mapper mapper) {
		super(ChatColor.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		return "&#" + Integer.toHexString(((ChatColor) value).getColor().getRGB()).substring(2);
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		return ChatColor.of(((String) value).replaceFirst("&", ""));
	}

}
