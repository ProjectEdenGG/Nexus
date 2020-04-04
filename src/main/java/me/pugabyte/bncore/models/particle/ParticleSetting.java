package me.pugabyte.bncore.models.particle;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.particles.effects.DiscoEffect;
import me.pugabyte.bncore.features.particles.effects.StormEffect;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum ParticleSetting {
	COLOR(Material.LEATHER_CHESTPLATE, Color.class, ParticleType.valuesExcept(ParticleType.DISCO, ParticleType.DOUBLE_CHAOS)),
	RAINBOW(Material.MAGMA_CREAM, Boolean.class, ParticleType.valuesExcept(ParticleType.DISCO, ParticleType.DOUBLE_CHAOS)),
	RADIUS(Material.HOPPER, Double.class, ParticleType.valuesExcept(ParticleType.BANDS, ParticleType.NYAN_CAT, ParticleType.CIRCLES, ParticleType.DOUBLE_CHAOS)) {
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

	CIRCLE_WHOLE(Material.SNOW_BALL, Boolean.class, ParticleType.CIRCLE),

	STORM_RAIN_PARTICLE(Material.PAPER, StormEffect.RainPartile.class, ParticleType.STORM),

	STAR_ROTATE_SPEED(Material.SUGAR, Double.class, ParticleType.STAR, ParticleType.GROWING_STARS) {
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
	STAR_GROWTH_SPEED(new ItemBuilder(Material.INK_SACK).dyeColor(ColorType.WHITE).build(), Double.class, ParticleType.STAR, ParticleType.GROWING_STARS) {
		@Override
		Object validate(ParticleType particleType, Object object) {
			return ParticleSetting.STAR_ROTATE_SPEED.validate(particleType, object);
		}
	},

	DOUBLE_CHAOS_COLOR_ONE(Material.LEATHER_CHESTPLATE, Color.class, ParticleType.DOUBLE_CHAOS),
	DOUBLE_CHAOS_RAINBOW_ONE(Material.MAGMA_CREAM, Boolean.class, ParticleType.DOUBLE_CHAOS),
	DOUBLE_CHAOS_RADIUS_ONE(Material.HOPPER, Double.class, ParticleType.DOUBLE_CHAOS) {
		@Override
		Object validate(ParticleType particleType, Object object) {
			return ParticleSetting.RADIUS.validate(particleType, object);
		}
	},
	DOUBLE_CHAOS_COLOR_TWO(Material.LEATHER_CHESTPLATE, Color.class, ParticleType.DOUBLE_CHAOS),
	DOUBLE_CHAOS_RAINBOW_TWO(Material.MAGMA_CREAM, Boolean.class, ParticleType.DOUBLE_CHAOS),
	DOUBLE_CHAOS_RADIUS_TWO(Material.HOPPER, Double.class, ParticleType.DOUBLE_CHAOS) {
		@Override
		Object validate(ParticleType particleType, Object object) {
			return ParticleSetting.RADIUS.validate(particleType, object);
		}
	},

	DISCO_SPHERE_COLOR(Material.LEATHER_CHESTPLATE, Color.class, ParticleType.DISCO),
	DISCO_SPHERE_RAINBOW(Material.MAGMA_CREAM, Boolean.class, ParticleType.DISCO),
	DISCO_LINE_COLOR(Material.LEATHER_CHESTPLATE, Color.class, ParticleType.DISCO),
	DISCO_LINE_RAINBOW(Material.END_ROD, Boolean.class, ParticleType.DISCO),
	DISCO_DIRECTION(Material.MAGENTA_GLAZED_TERRACOTTA, DiscoEffect.Direction.class, ParticleType.DISCO),
	DISCO_RAINBOW_OPTION(Material.SPECKLED_MELON, DiscoEffect.RainbowOption.class, ParticleType.DISCO),
	DISCO_LINE_LENGTH(Material.DIODE, Integer.class, ParticleType.DISCO) {
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
	Class<?> value;

	ParticleSetting(Material material, Class<?> value, ParticleType... applicableEffects) {
		this.applicableEffects = Arrays.asList(applicableEffects);
		this.itemStack = new ItemStack(material);
		this.value = value;
	}

	ParticleSetting(ItemStack itemStack, Class<?> value, ParticleType... applicableEffects) {
		this.applicableEffects = Arrays.asList(applicableEffects);
		this.itemStack = itemStack;
		this.value = value;
	}

	Object validate(ParticleType particleType, Object object) {
		return object;
	}

	public String getTitle() {
		return StringUtils.camelCase(name());
	}

	public String getLore(Player player, ParticleType type) {
		return "||&eCurrent value: &3" + getter(player, type);
	}

	public void onClick(Player player, ParticleType type) {
		BNCore.getSignMenuFactory().lines("", "^ ^ ^ ^ ^ ^", "Enter new value for", getTitle())
				.response((player1, response) ->
						setter(player, type, response[0]))
				.open(player);
	}

	private PropertyDescriptor getPropertyDescriptor() throws IntrospectionException {
		return new PropertyDescriptor(Character.toLowerCase(name().charAt(0)) + StringUtils.camelCase(name()).substring(1).replaceAll(" ", ""), ParticleType.class);
	}

	String getter(Player player, ParticleType type) {
		ParticleService service = new ParticleService();
		ParticleOwner owner = service.get(player);
		Object value = owner.getSettings(type).get(this);
		if (value != null)
			return "" + value;
		return "";
	}

	void setter(Player player, ParticleType type, String text) {
		try {
			Object value = null;
			if (this.value == Double.class)
				value = Double.valueOf(text);
			else if (this.value == Boolean.class)
				value = Boolean.valueOf(text);
			else if (this.value == Integer.class)
				value = Integer.valueOf(text);
			else if (Enum.class.isAssignableFrom(this.value))
				value = Enum.valueOf((Class<Enum>) this.value, text);

		} catch (Exception ignore) {
		}

		ParticleService service = new ParticleService();
		ParticleOwner owner = service.get(player);
		Map<ParticleType, Map<ParticleSetting, Object>> map = owner.getSettings();
		Map<ParticleSetting, Object> map2 = map.getOrDefault(type, new HashMap<>());
		map2.put(this, value);
		map.put(type, map2);
		owner.setSettings(map);
		service.save(owner);
	}
}
