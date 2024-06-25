package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.quests.tasks.common.IQuest;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.SneakyThrows;

public class EnumPathConverter extends TypeConverter implements SimpleValueConverter {

	public EnumPathConverter(Mapper mapper) {
		super(IQuest.class, IQuestTask.class);
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
