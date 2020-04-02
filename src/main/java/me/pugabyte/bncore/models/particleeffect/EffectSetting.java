package me.pugabyte.bncore.models.particleeffect;

import lombok.Getter;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Getter
public enum EffectSetting {
	COLOR(Material.LEATHER_CHESTPLATE, EffectType.valuesExcept(EffectType.DISCO, EffectType.DOUBLE_CHAOS)),
	RAINBOW(Material.MAGMA_CREAM, EffectType.valuesExcept(EffectType.DISCO, EffectType.DOUBLE_CHAOS)),
	RADIUS(Material.HOPPER, EffectType.valuesExcept(EffectType.BANDS, EffectType.NYAN_CAT, EffectType.CIRCLES, EffectType.DOUBLE_CHAOS)) {
		@Override
		Object validate(EffectType effectType, Object object) {
			Double value = (Double) object;
			if (value < 0.1)
				return 0.1;
			switch (effectType) {
				case DISCO:
				case HALO:
					if (value > 1.0)
						return 1.0;
					break;
				default:
					if (value > 2.0)
						return 2.0;
			}
			return value;
		}
	},

	CIRCLE_WHOLE(Material.SNOW_BALL, EffectType.CIRCLE),

	STAR_ROTATE_SPEED(Material.SUGAR, EffectType.STAR, EffectType.GROWING_STARS) {
		@Override
		Object validate(EffectType effectType, Object object) {
			Double value = (Double) object;
			if (value > 0.5)
				return 0.5;
			if (value < 0.1)
				return 0.1;
			return value;
		}
	},
	STAR_GROWTH_SPEED(new ItemBuilder(Material.INK_SACK).dyeColor(ColorType.WHITE).build(), EffectType.STAR, EffectType.GROWING_STARS) {
		@Override
		Object validate(EffectType effectType, Object object) {
			return EffectSetting.STAR_ROTATE_SPEED.validate(effectType, object);
		}
	},

	DOUBLE_CHAOS_COLOR_ONE(Material.LEATHER_CHESTPLATE, EffectType.DOUBLE_CHAOS),
	DOUBLE_CHAOS_RAINBOW_ONE(Material.MAGMA_CREAM, EffectType.DOUBLE_CHAOS),
	DOUBLE_CHAOS_RADIUS_ONE(Material.HOPPER, EffectType.DOUBLE_CHAOS) {
		@Override
		Object validate(EffectType effectType, Object object) {
			return EffectSetting.RADIUS.validate(effectType, object);
		}
	},
	DOUBLE_CHAOS_COLOR_TWO(Material.LEATHER_CHESTPLATE, EffectType.DOUBLE_CHAOS),
	DOUBLE_CHAOS_RAINBOW_TWO(Material.MAGMA_CREAM, EffectType.DOUBLE_CHAOS),
	DOUBLE_CHAOS_RADIUS_TWO(Material.HOPPER, EffectType.DOUBLE_CHAOS) {
		@Override
		Object validate(EffectType effectType, Object object) {
			return EffectSetting.RADIUS.validate(effectType, object);
		}
	},

	DISCO_SPHERE_COLOR(Material.LEATHER_CHESTPLATE, EffectType.DISCO),
	DISCO_SPHERE_RAINBOW(Material.MAGMA_CREAM, EffectType.DISCO),
	DISCO_LINE_COLOR(Material.LEATHER_CHESTPLATE, EffectType.DISCO),
	DISCO_LINE_RAINBOW(Material.END_ROD, EffectType.DISCO),
	DISCO_DIRECTION(Material.MAGENTA_GLAZED_TERRACOTTA, EffectType.DISCO),
	DISCO_RAINBOW(Material.SPECKLED_MELON, EffectType.DISCO),
	DISCO_LINE_LENGTH(Material.DIODE, EffectType.DISCO) {
		@Override
		Object validate(EffectType effectType, Object object) {
			Integer value = (Integer) object;
			if (value < 1)
				return 1;
			if (value > 5)
				return 5;
			return value;
		}
	};

	List<EffectType> applicableEffects;
	ItemStack itemStack;

	EffectSetting(Material material, EffectType... applicableEffects) {
		this.applicableEffects = Arrays.asList(applicableEffects);
		this.itemStack = new ItemStack(material);
	}

	EffectSetting(ItemStack itemStack, EffectType... applicableEffects) {
		this.applicableEffects = Arrays.asList(applicableEffects);
		this.itemStack = itemStack;
	}

	Object validate(EffectType effectType, Object object) {
		return object;
	}
}
