package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.nexus.features.events.models.Quest;
import gg.projecteden.api.common.utils.EnumUtils;
import lombok.SneakyThrows;

public class QuestConverter extends TypeConverter implements SimpleValueConverter {

	public QuestConverter(Mapper mapper) {
		super(Quest.class);
	}

	@Override
	@SneakyThrows
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		Quest quest = (Quest) value;
		return new BasicDBObject() {{
			put("className", quest.getClass().getName());
			put("value", quest.getClass().getMethod("name").invoke(quest));
		}};
	}

	@Override
	@SneakyThrows
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		BasicDBObject deserialized = (BasicDBObject) value;
		return EnumUtils.valueOf(Class.forName(deserialized.getString("className")), deserialized.getString("value"));
	}

}
