package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import com.mongodb.DBObject;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.api.common.utils.ReflectionUtils;
import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.nexus.models.fakenpcs.npcs.FakeNPC;
import gg.projecteden.nexus.utils.Utils;
import lombok.SneakyThrows;

import java.util.List;

public class FakeNPCConverter extends TypeConverter implements SimpleValueConverter {

	public FakeNPCConverter(Mapper mapper) {
		super(Utils.combine(List.of(FakeNPC.class), ReflectionUtils.subTypesOf(FakeNPC.class, FakeNPC.class.getPackageName())).toArray(new Class[0]));
	}

	@Override
	@SneakyThrows
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (!(value instanceof FakeNPC npc))
			return null;

		final DBObject serialize = MongoService.serialize(npc);
		serialize.put("className", npc.getClass().getName());
		return serialize;
	}

	@Override
	@SneakyThrows
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		return MongoService.deserialize((DBObject) value);
	}

}
