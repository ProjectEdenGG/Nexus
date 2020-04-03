package me.pugabyte.bncore.models.particle;

import lombok.Getter;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Getter
public enum ParticleSetting {
	COLOR(Material.LEATHER_CHESTPLATE, ParticleType.valuesExcept(ParticleType.DISCO, ParticleType.DOUBLE_CHAOS)),
	RAINBOW(Material.MAGMA_CREAM, ParticleType.valuesExcept(ParticleType.DISCO, ParticleType.DOUBLE_CHAOS)),
	RADIUS(Material.HOPPER, ParticleType.valuesExcept(ParticleType.BANDS, ParticleType.NYAN_CAT, ParticleType.CIRCLES, ParticleType.DOUBLE_CHAOS)) {
		@Override
		Object validate(ParticleType particleType, Object object) {
			Double value = (Double) object;
			if (value < 0.1)
				return 0.1;
			switch (particleType) {
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

	CIRCLE_WHOLE(Material.SNOW_BALL, ParticleType.CIRCLE),

	STORM_RAIN_PARTICLE(Material.PAPER, ParticleType.STORM),

	STAR_ROTATE_SPEED(Material.SUGAR, ParticleType.STAR, ParticleType.GROWING_STARS) {
		@Override
		Object validate(ParticleType particleType, Object object) {
			Double value = (Double) object;
			if (value > 0.5)
				return 0.5;
			if (value < 0.1)
				return 0.1;
			return value;
		}
	},
	STAR_GROWTH_SPEED(new ItemBuilder(Material.INK_SACK).dyeColor(ColorType.WHITE).build(), ParticleType.STAR, ParticleType.GROWING_STARS) {
		@Override
		Object validate(ParticleType particleType, Object object) {
			return ParticleSetting.STAR_ROTATE_SPEED.validate(particleType, object);
		}
	},

	DOUBLE_CHAOS_COLOR_ONE(Material.LEATHER_CHESTPLATE, ParticleType.DOUBLE_CHAOS),
	DOUBLE_CHAOS_RAINBOW_ONE(Material.MAGMA_CREAM, ParticleType.DOUBLE_CHAOS),
	DOUBLE_CHAOS_RADIUS_ONE(Material.HOPPER, ParticleType.DOUBLE_CHAOS) {
		@Override
		Object validate(ParticleType particleType, Object object) {
			return ParticleSetting.RADIUS.validate(particleType, object);
		}
	},
	DOUBLE_CHAOS_COLOR_TWO(Material.LEATHER_CHESTPLATE, ParticleType.DOUBLE_CHAOS),
	DOUBLE_CHAOS_RAINBOW_TWO(Material.MAGMA_CREAM, ParticleType.DOUBLE_CHAOS),
	DOUBLE_CHAOS_RADIUS_TWO(Material.HOPPER, ParticleType.DOUBLE_CHAOS) {
		@Override
		Object validate(ParticleType particleType, Object object) {
			return ParticleSetting.RADIUS.validate(particleType, object);
		}
	},

	DISCO_SPHERE_COLOR(Material.LEATHER_CHESTPLATE, ParticleType.DISCO),
	DISCO_SPHERE_RAINBOW(Material.MAGMA_CREAM, ParticleType.DISCO),
	DISCO_LINE_COLOR(Material.LEATHER_CHESTPLATE, ParticleType.DISCO),
	DISCO_LINE_RAINBOW(Material.END_ROD, ParticleType.DISCO),
	DISCO_DIRECTION(Material.MAGENTA_GLAZED_TERRACOTTA, ParticleType.DISCO),
	DISCO_RAINBOW(Material.SPECKLED_MELON, ParticleType.DISCO),
	DISCO_LINE_LENGTH(Material.DIODE, ParticleType.DISCO) {
		@Override
		Object validate(ParticleType particleType, Object object) {
			Integer value = (Integer) object;
			if (value < 1)
				return 1;
			if (value > 5)
				return 5;
			return value;
		}
	};

	List<ParticleType> applicableEffects;
	ItemStack itemStack;

	ParticleSetting(Material material, ParticleType... applicableEffects) {
		this.applicableEffects = Arrays.asList(applicableEffects);
		this.itemStack = new ItemStack(material);
	}

	ParticleSetting(ItemStack itemStack, ParticleType... applicableEffects) {
		this.applicableEffects = Arrays.asList(applicableEffects);
		this.itemStack = itemStack;
	}

	Object validate(ParticleType particleType, Object object) {
		return object;
	}
}
