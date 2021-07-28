package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.models.scheduledjobs.common.AbstractJob;
import lombok.SneakyThrows;

public class JobConverter extends TypeConverter implements SimpleValueConverter {

	public JobConverter(Mapper mapper) {
		super(AbstractJob.class);
	}

	@Override
	@SneakyThrows
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		final DBObject dbObject = MongoService.serialize(value);
		dbObject.put("className", value.getClass().getName());
		return dbObject;
	}

	@Override
	@SneakyThrows
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		return MongoService.deserialize((BasicDBObject) value);
	}

}
