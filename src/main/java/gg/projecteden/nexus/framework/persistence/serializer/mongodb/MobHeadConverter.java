package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.mobheads.MobHeadType;
import gg.projecteden.nexus.features.mobheads.common.MobHead;
import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Nullables;
import lombok.NoArgsConstructor;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
public class MobHeadConverter extends TypeConverter implements SimpleValueConverter {

	public MobHeadConverter(Mapper mapper) {
		super(MobHead.class, MobHeadType.class, MobHeadVariant.class);
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		return encode((MobHead) value);
	}

	@Override
	public Object decode(Class<?> aClass, Object value, MappedField mappedField) {
		if (value == null) return null;
		return decode((String) value);
	}

	@NotNull
	public static String encode(MobHead mobHead) {
		String key = mobHead.getType().name();

		if (mobHead.getVariant() != null)
			key += "." + mobHead.getVariant().name();

		return key;
	}

	@Nullable
	public static MobHead decode(String key) {
		String[] split = key.split("\\.");
		String entityTypeName = split[0].toUpperCase();

		MobHeadType mobHeadType = null;
		try {
			mobHeadType = MobHeadType.of(EntityType.valueOf(entityTypeName));
		} catch (Exception ignored) {}

		if (mobHeadType == null)
			throw new InvalidInputException("Unknown MobHead: " + key);

		if (split.length > 1) {
			String variantName = split[1];

			if (!Nullables.isNullOrEmpty(variantName) && mobHeadType.getVariantClass() != null)
				return EnumUtils.valueOf(mobHeadType.getVariantClass(), variantName);
		}

		return mobHeadType;
	}

}
