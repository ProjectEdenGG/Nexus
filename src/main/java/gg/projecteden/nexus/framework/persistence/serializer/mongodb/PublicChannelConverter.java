package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.nexus.features.chat.ChatManager;
import gg.projecteden.nexus.models.chat.PublicChannel;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PublicChannelConverter extends TypeConverter implements SimpleValueConverter {

	public PublicChannelConverter(Mapper mapper) {
		super(PublicChannel.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;

		final PublicChannel channel = (PublicChannel) value;
		if (!channel.isPersistent())
			return null;

		return channel.getName();
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		return ChatManager.getChannel(((String) value));
	}

}
