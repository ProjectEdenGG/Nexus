package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.EnumUtils;
import lombok.SneakyThrows;

public class IQuestTaskConverter extends TypeConverter implements SimpleValueConverter {

	public IQuestTaskConverter(Mapper mapper) {
		super(IQuestTask.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;

		final Enum<?> casted = (Enum<?>) value;
		return casted.getClass().getName() + "." + casted.name();
	}

	@Override
	@SneakyThrows
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		final String path = (String) value;
		final String name = StringUtils.listLast(path, ".");
		return EnumUtils.valueOf(Class.forName(StringUtils.replaceLast(path, "." + name, "")), name);
	}

}
