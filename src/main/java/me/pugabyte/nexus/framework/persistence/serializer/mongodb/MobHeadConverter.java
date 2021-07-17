package me.pugabyte.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import eden.utils.EnumUtils;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.mobheads.MobHeadType;
import me.pugabyte.nexus.features.mobheads.common.MobHead;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;

import static eden.utils.StringUtils.isNullOrEmpty;

@NoArgsConstructor
public class MobHeadConverter extends TypeConverter implements SimpleValueConverter {

	public MobHeadConverter(Mapper mapper) {
		super(MobHead.class, MobHeadType.class, MobHeadVariant.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;

		MobHead mobHead = (MobHead) value;

		String key = mobHead.name();

		if (mobHead.getVariant() != null)
			key = mobHead.getType().name() + "." + mobHead.getVariant().name();

		return key;
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;

		String key = (String) value;

		String[] split = key.split("\\.");
		String entityTypeName = split[0];
		EntityType entityType = EntityType.valueOf(entityTypeName);
		MobHeadType mobHeadType = MobHeadType.of(entityType);

		if (split.length > 1) {
			String variantName = split[1];

			if (!isNullOrEmpty(variantName))
				return EnumUtils.valueOf(mobHeadType.getVariantClass(), variantName);
		}

		return mobHeadType;
	}

}
