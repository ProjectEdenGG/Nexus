package me.pugabyte.bncore.models.particle;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.particles.effects.DiscoEffect;
import me.pugabyte.bncore.features.particles.effects.StormEffect;
import me.pugabyte.bncore.features.particles.effects.WingsEffect;
import me.pugabyte.bncore.features.particles.menu.ParticleMenu;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public enum ParticleSetting {
	COLOR(2, 2, Material.LEATHER_CHESTPLATE, Color.class, ParticleType.valuesExcept(ParticleType.DISCO, ParticleType.DOUBLE_CHAOS, ParticleType.NYAN_CAT, ParticleType.BANDS, ParticleType.WINGS)) {
		@Override
		Object getDefault(ParticleType particleType) {
			return Color.RED;
		}
	},
	RAINBOW(3, 2, Material.MAGMA_CREAM, Boolean.class, ParticleType.valuesExcept(ParticleType.DISCO, ParticleType.DOUBLE_CHAOS, ParticleType.NYAN_CAT, ParticleType.BANDS, ParticleType.WINGS)) {
		@Override
		Object getDefault(ParticleType particleType) {
			return true;
		}
	},
	RADIUS(2, 4, Material.HOPPER, Double.class, ParticleType.valuesExcept(ParticleType.BANDS, ParticleType.NYAN_CAT, ParticleType.CIRCLES, ParticleType.DOUBLE_CHAOS, ParticleType.SPIRAL, ParticleType.WINGS)) {
		@Override
		Object getDefault(ParticleType particleType) {
			switch (particleType) {
				case CIRCLE:
				case STAR:
				case GROWING_STARS:
					return 2.0;
				case TRIANGLE:
				case SQUARE:
				case PENTAGON:
				case HEXAGON:
				case SPHERE:
				case SPRITE:
				case CHAOS:
				case STORM:
					return 1.5;
				case HALO:
				case DISCO:
					return 0.5;
				default:
					throw new InvalidInputException("Particle type does not support this particle setting");
			}
		}

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

	SPIRAL_RADIUS(2, 4, Material.HOPPER, Double.class, ParticleType.SPIRAL) {
		@Override
		Object getDefault(ParticleType particleType) {
			return .15;
		}

		@Override
		Object validate(ParticleType particleType, Object object) {
			Double value = (Double) object;
			if (value < 0.15)
				return 0.15;
			if (value > 0.35) {
				return 0.35;
			}
			return value;
		}
	},

	WHOLE(2, 5, Material.SNOWBALL, Boolean.class, ParticleType.CIRCLE, ParticleType.TRIANGLE, ParticleType.SQUARE, ParticleType.PENTAGON, ParticleType.HEXAGON) {
		@Override
		Object getDefault(ParticleType particleType) {
			return true;
		}
	},
	ROTATE_SPEED(2, 6, Material.SUGAR, Double.class, ParticleType.STAR, ParticleType.GROWING_STARS, ParticleType.TRIANGLE, ParticleType.SQUARE, ParticleType.PENTAGON, ParticleType.HEXAGON) {
		@Override
		Object getDefault(ParticleType particleType) {
			switch (particleType) {
				case STAR:
				case GROWING_STARS:
					return 0.2;
				case TRIANGLE:
				case SQUARE:
				case PENTAGON:
				case HEXAGON:
					return 1.5;
				default:
					throw new InvalidInputException("Particle type does not support this particle setting");
			}
		}

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

	STAR_GROWTH_SPEED(2, 5, Material.BONE_MEAL, Double.class, ParticleType.STAR, ParticleType.GROWING_STARS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return .1;
		}

		@Override
		Object validate(ParticleType particleType, Object object) {
			return ParticleSetting.ROTATE_SPEED.validate(particleType, object);
		}
	},

	STORM_RAIN_PARTICLE(2, 5, Material.PAPER, StormEffect.RainPartile.class, ParticleType.STORM) {
		@Override
		Object getDefault(ParticleType particleType) {
			return StormEffect.RainPartile.RAIN;
		}
	},

	DOUBLE_CHAOS_COLOR_ONE(2, 1, Material.LEATHER_CHESTPLATE, Color.class, ParticleType.DOUBLE_CHAOS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return Color.RED;
		}
	},
	DOUBLE_CHAOS_RAINBOW_ONE(3, 1, Material.MAGMA_CREAM, Boolean.class, ParticleType.DOUBLE_CHAOS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return true;
		}
	},
	DOUBLE_CHAOS_RADIUS_ONE(2, 4, Material.HOPPER, Double.class, ParticleType.DOUBLE_CHAOS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return 1.5;
		}

		@Override
		Object validate(ParticleType particleType, Object object) {
			return ParticleSetting.RADIUS.validate(particleType, object);
		}
	},
	DOUBLE_CHAOS_COLOR_TWO(2, 2, Material.LEATHER_CHESTPLATE, Color.class, ParticleType.DOUBLE_CHAOS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return Color.RED;
		}
	},
	DOUBLE_CHAOS_RAINBOW_TWO(3, 2, Material.MAGMA_CREAM, Boolean.class, ParticleType.DOUBLE_CHAOS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return true;
		}
	},
	DOUBLE_CHAOS_RADIUS_TWO(2, 5, Material.HOPPER, Double.class, ParticleType.DOUBLE_CHAOS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return 1.5;
		}

		@Override
		Object validate(ParticleType particleType, Object object) {
			return ParticleSetting.RADIUS.validate(particleType, object);
		}
	},

	DISCO_SPHERE_COLOR(2, 1, Material.LEATHER_CHESTPLATE, Color.class, ParticleType.DISCO) {
		@Override
		Object getDefault(ParticleType particleType) {
			return Color.WHITE;
		}
	},
	DISCO_SPHERE_RAINBOW(3, 1, Material.MAGMA_CREAM, Boolean.class, ParticleType.DISCO) {
		@Override
		Object getDefault(ParticleType particleType) {
			return false;
		}
	},
	DISCO_LINE_COLOR(2, 2, Material.LEATHER_CHESTPLATE, Color.class, ParticleType.DISCO) {
		@Override
		Object getDefault(ParticleType particleType) {
			return Color.RED;
		}
	},
	DISCO_LINE_RAINBOW(3, 2, Material.MAGMA_CREAM, Boolean.class, ParticleType.DISCO) {
		@Override
		Object getDefault(ParticleType particleType) {
			return true;
		}
	},
	DISCO_DIRECTION(2, 5, Material.MAGENTA_GLAZED_TERRACOTTA, DiscoEffect.Direction.class, ParticleType.DISCO) {
		@Override
		Object getDefault(ParticleType particleType) {
			return DiscoEffect.Direction.BOTH;
		}
	},
	DISCO_RAINBOW_OPTION(2, 6, Material.GLISTERING_MELON_SLICE, DiscoEffect.RainbowOption.class, ParticleType.DISCO) {
		@Override
		Object getDefault(ParticleType particleType) {
			return DiscoEffect.RainbowOption.SLOW;
		}
	},
	DISCO_LINE_LENGTH(2, 7, Material.REPEATER, Integer.class, ParticleType.DISCO) {
		@Override
		Object getDefault(ParticleType particleType) {
			return 5;
		}

		@Override
		Object validate(ParticleType particleType, Object object) {
			Double value = (Double) object;
			if (value < 1.0)
				return 1;
			if (value > 5.0)
				return 5;
			return value;
		}
	},
	WINGS_STYLE(2, 7, Material.ELYTRA, WingsEffect.WingStyle.class, ParticleType.WINGS) {
		@Override
		public void onClick(Player player, ParticleType type) {
			ParticleMenu.openWingsStyle(player);
		}

		@Override
		Object getDefault(ParticleType particleType) {
			return WingsEffect.WingStyle.ONE;
		}
	},
	WINGS_FLAP_MODE(2, 4, Material.FEATHER, Boolean.class, ParticleType.WINGS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return true;
		}
	},
	WINGS_FLAP_SPEED(2, 5, Material.SUGAR, Integer.class, ParticleType.WINGS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return 1;
		}

		@Override
		Object validate(ParticleType particleType, Object object) {
			Double value = (Double) object;
			if (value < 1.0)
				return 1;
			if (value > 4.0)
				return 4;
			return value;
		}
	},
	WINGS_COLOR_ONE(1, 1, Material.LEATHER_CHESTPLATE, Color.class, ParticleType.WINGS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return Color.RED;
		}
	},
	WINGS_COLOR_TWO(2, 1, Material.LEATHER_CHESTPLATE, Color.class, ParticleType.WINGS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return Color.GREEN;
		}
	},
	WINGS_COLOR_THREE(3, 1, Material.LEATHER_CHESTPLATE, Color.class, ParticleType.WINGS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return Color.BLUE;
		}
	},
	WINGS_RAINBOW_ONE(1, 2, Material.MAGMA_CREAM, Boolean.class, ParticleType.WINGS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return true;
		}
	},
	WINGS_RAINBOW_TWO(2, 2, Material.MAGMA_CREAM, Boolean.class, ParticleType.WINGS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return true;
		}
	},
	WINGS_RAINBOW_THREE(3, 2, Material.MAGMA_CREAM, Boolean.class, ParticleType.WINGS) {
		@Override
		Object getDefault(ParticleType particleType) {
			return true;
		}
	};

	List<ParticleType> applicableEffects;
	ItemStack itemStack;
	Class<?> value;
	int row, column;

	ParticleSetting(int row, int column, Material material, Class<?> value, ParticleType... applicableEffects) {
		this.applicableEffects = Arrays.asList(applicableEffects);
		this.itemStack = new ItemStack(material);
		this.value = value;
		this.row = row;
		this.column = column;
	}

	ParticleSetting(int row, int column, ItemStack itemStack, Class<?> value, ParticleType... applicableEffects) {
		this.applicableEffects = Arrays.asList(applicableEffects);
		this.itemStack = itemStack;
		this.value = value;
		this.row = row;
		this.column = column;
	}

	Object validate(ParticleType particleType, Object object) {
		return object;
	}

	public String getTitle() {
		return StringUtils.camelCase(name());
	}

	public String getLore(Player player, ParticleType type) {
		if (this.value == Boolean.class)
			return (Boolean.parseBoolean(getter(player, type))) ? "&aEnabled" : "&cDisabled";
		if (this.value == Color.class) {
			Color color = get(new ParticleService().get(player), type);
			return "||&cR: " + color.getRed() + "||&aG: " + color.getGreen() + "||&bB: " + color.getBlue();
		}
		Object min = 0.0;
		Object max = 10.0;
		return "||&eCurrent value: &3" + getter(player, type) +
				((validate(type, min).equals(min)) ? "" : "|| ||&eMin Value:&3 " + validate(type, min)) +
				((validate(type, max).equals(max)) ? "" : "||&eMax Value:&3 " + validate(type, max));

	}

	public void onClick(Player player, ParticleType type) {
		if (value == Double.class || value == Integer.class)
			BNCore.getSignMenuFactory().lines("", "^ ^ ^ ^ ^ ^", "Enter new value for", getTitle())
					.response(lines -> {
						setter(player, type, lines[0]);
						ParticleMenu.openSettingEditor(player, type);
					})
					.open(player);
		else if (value == Boolean.class) {
			Boolean bool = !Boolean.parseBoolean(getter(player, type));
			setter(player, type, bool.toString());
			ParticleMenu.openSettingEditor(player, type);
		} else if (value == Color.class) {
			ParticleMenu.openColor(player, type, this);
		} else if (Enum.class.isAssignableFrom(value)) {
			Enum<?> val = get(new ParticleService().get(player), type);
			Enum<?> next = Utils.EnumUtils.nextWithLoop(val.getClass(), val.ordinal());
			setter(player, type, next.name());
			ParticleMenu.openSettingEditor(player, type);
		}
	}

	String getter(Player player, ParticleType type) {
		return get(new ParticleService().get(player), type).toString();
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

			ParticleService service = new ParticleService();
			ParticleOwner owner = service.get(player);
			Map<ParticleSetting, Object> settings = owner.getSettings(type);
			settings.put(this, this.validate(type, value));
			service.save(owner);
		} catch (Exception ignore) {
		}
	}

	public <T> T get(ParticleOwner particleOwner, ParticleType particleType) {
		Map<ParticleSetting, Object> settings = particleOwner.getSettings(particleType);
		if (settings != null && settings.containsKey(this)) {
			return (T) settings.get(this);
		}
		return (T) getDefault(particleType);
	}

	abstract Object getDefault(ParticleType particleType);

}
