package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

/*
import com.mongodb.BasicDBObject;
import dev.morphia.Morphia;
import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.Map;

public class IClientSideEntityConverter extends TypeConverter implements SimpleValueConverter {

	public IClientSideEntityConverter(Mapper mapper) {
		super(IClientSideEntity.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		return new BasicDBObject() {{
			put("className", value.getClass().getName());
			put("value", MongoService.serialize(value));
		}};
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		BasicDBObject deserialized = (BasicDBObject) value;
		return MongoService.deserialize(deserialized);
	}

}
*/
