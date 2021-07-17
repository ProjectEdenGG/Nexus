package me.pugabyte.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import eden.utils.EnumUtils;
import me.pugabyte.nexus.features.mobheads.MobHeadType;
import me.pugabyte.nexus.features.mobheads.common.MobHead;
import org.bukkit.entity.EntityType;

import static eden.utils.StringUtils.isNullOrEmpty;

public class MobHeadConverter extends TypeConverter implements SimpleValueConverter {

	public MobHeadConverter(Mapper mapper) {
		super(MobHead.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;

		MobHead mobHead = (MobHead) value;

		String key = mobHead.name();

		if (mobHead.getVariant() != null)
			key += "." + mobHead.getVariant().name();

		return key;
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;

		String key = (String) value;

		String[] split = key.split("\\.");
		String entityTypeName = split[0];
		String variantName = split[1];

		EntityType entityType = EntityType.valueOf(entityTypeName);
		MobHeadType mobHeadType = MobHeadType.of(entityType);

		if (isNullOrEmpty(variantName))
			return mobHeadType;

		return EnumUtils.valueOf(mobHeadType.getVariantClass(), variantName);
	}

}
