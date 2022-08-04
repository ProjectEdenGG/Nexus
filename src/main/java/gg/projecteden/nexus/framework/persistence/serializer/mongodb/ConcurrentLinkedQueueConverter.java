package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import com.mongodb.BasicDBList;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.api.mongodb.MongoService;
import lombok.SneakyThrows;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentLinkedQueueConverter extends TypeConverter implements SimpleValueConverter {

	public ConcurrentLinkedQueueConverter(Mapper mapper) {
		super(ConcurrentLinkedQueue.class);
	}

	@Override
	@SneakyThrows
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value instanceof ConcurrentLinkedQueue<?> queue)
			return queue.stream().map(MongoService::serialize).toList();

		return null;
	}

	@Override
	@SneakyThrows
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value instanceof BasicDBList list)
			return new ConcurrentLinkedQueue<>(list);

		return null;
	}
}
