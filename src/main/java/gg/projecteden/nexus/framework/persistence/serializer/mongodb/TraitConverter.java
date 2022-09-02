package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import com.mongodb.DBObject;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.api.common.utils.ReflectionUtils;
import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.nexus.models.fakenpcs.npcs.Trait;
import gg.projecteden.nexus.utils.Utils;
import lombok.SneakyThrows;

import java.util.List;

public class TraitConverter extends TypeConverter implements SimpleValueConverter {

	public TraitConverter(Mapper mapper) {
		super(Utils.combine(List.of(Trait.class), ReflectionUtils.subTypesOf(Trait.class, Trait.class.getPackageName())).toArray(new Class[0]));
	}

	@Override
	@SneakyThrows
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (!(value instanceof Trait trait))
			return null;

		final DBObject serialize = MongoService.serialize(trait);
		serialize.put("className", trait.getClass().getName());
		return serialize;
	}

	@Override
	@SneakyThrows
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		return MongoService.deserialize((DBObject) value);
	}
}
