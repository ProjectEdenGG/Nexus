package gg.projecteden.nexus.models.particle;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.particles.effects.*;
import gg.projecteden.nexus.features.particles.effects.WingsEffect.WingsEffectBuilder;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.util.Vector;

import java.util.Arrays;

@Getter
public enum ParticleType {
	CIRCLE(Material.ENDER_PEARL, true) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);
			Boolean whole = ParticleSetting.WHOLE.get(particleOwner, this);

			int taskId = CircleEffect.builder()
				.player(entity)
				.updateLoc(true)
				.density(100)
				.radius(radius)
				.ticks(-1)
				.whole(whole)
				.color(color)
				.rainbow(rainbow)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	STAR(Material.FIREWORK_STAR, true) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double rotateSpeed = ParticleSetting.ROTATE_SPEED.get(particleOwner, this);
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			int taskId = StarEffect.builder()
				.player(entity)
				.radius(radius)
				.ticks(-1)
				.updateLoc(true)
				.color(color)
				.rainbow(rainbow)
				.rotateSpeed(rotateSpeed)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	TRIANGLE(Material.REDSTONE, true) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean whole = ParticleSetting.WHOLE.get(particleOwner, this);
			Double rotateSpeed = ParticleSetting.ROTATE_SPEED.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			int taskId = PolygonEffect.builder()
				.player(entity)
				.updateLoc(true)
				.whole(whole)
				.polygon(PolygonEffect.Polygon.TRIANGLE)
				.radius(radius)
				.ticks(-1)
				.color(color)
				.rainbow(rainbow)
				.rotateSpeed(rotateSpeed)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	SQUARE(Material.YELLOW_CARPET, true) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean whole = ParticleSetting.WHOLE.get(particleOwner, this);
			Double rotateSpeed = ParticleSetting.ROTATE_SPEED.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			int taskId = PolygonEffect.builder()
				.player(entity)
				.updateLoc(true)
				.whole(whole)
				.polygon(PolygonEffect.Polygon.SQUARE)
				.radius(radius)
				.ticks(-1)
				.color(color)
				.rainbow(rainbow)
				.rotateSpeed(rotateSpeed)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	PENTAGON(Material.PAPER, true) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean whole = ParticleSetting.WHOLE.get(particleOwner, this);
			Double rotateSpeed = ParticleSetting.ROTATE_SPEED.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			int taskId = PolygonEffect.builder()
				.player(entity)
				.updateLoc(true)
				.whole(whole)
				.polygon(PolygonEffect.Polygon.PENTAGON)
				.radius(radius)
				.ticks(-1)
				.color(color)
				.rainbow(rainbow)
				.rotateSpeed(rotateSpeed)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	HEXAGON(Material.PAPER, true) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean whole = ParticleSetting.WHOLE.get(particleOwner, this);
			Double rotateSpeed = ParticleSetting.ROTATE_SPEED.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			int taskId = PolygonEffect.builder()
				.player(entity)
				.updateLoc(true)
				.whole(whole)
				.polygon(PolygonEffect.Polygon.HEXAGON)
				.radius(radius)
				.ticks(-1)
				.color(color)
				.rainbow(rainbow)
				.rotateSpeed(rotateSpeed)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	NYAN_CAT(Material.OCELOT_SPAWN_EGG) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			int taskId = NyanCatEffect.builder()
				.player(entity)
				.ticks(-1)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	BANDS(Material.END_ROD) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			int taskId = BandsEffect.builder()
				.player(entity)
				.ticks(-1)
				.rainbow(true)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	HALO(Material.GOLDEN_HELMET) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			Vector vector = new Vector(0, 2.1, 0);
			Location loc = particleOwner.getOnlinePlayer().getLocation().add(vector);
			int taskId = CircleEffect.builder()
				.player(entity)
				.location(loc)
				.updateVector(vector)
				.density(20)
				.radius(radius)
				.ticks(-1)
				.color(color)
				.rainbow(rainbow)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	CIRCLES(Material.ENDER_EYE) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			int taskId1 = CircleEffect.builder()
				.player(entity)
				.density(10)
				.radius(0.333)
				.ticks(-1)
				.whole(true)
				.updateLoc(true)
				.color(color)
				.rainbow(rainbow)
				.start()
				.getTaskId();
			int taskId2 = CircleEffect.builder()
				.player(entity)
				.density(20)
				.radius(0.666)
				.ticks(-1)
				.whole(true)
				.updateLoc(true)
				.color(color)
				.rainbow(rainbow)
				.startDelay(20)
				.start()
				.getTaskId();
			int taskId3 = CircleEffect.builder()
				.player(entity)
				.density(40)
				.radius(0.999)
				.ticks(-1)
				.whole(true)
				.updateLoc(true)
				.color(color)
				.rainbow(rainbow)
				.startDelay(40)
				.start()
				.getTaskId();
			int taskId4 = CircleEffect.builder()
				.player(entity)
				.density(60)
				.radius(1.333)
				.ticks(-1)
				.whole(true)
				.updateLoc(true)
				.color(color)
				.rainbow(rainbow)
				.startDelay(60)
				.start()
				.getTaskId();
			return new int[]{taskId1, taskId2, taskId3, taskId4};
		}
	},
	SPHERE(Material.SLIME_BALL) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);

			int taskId = SphereEffect.builder()
				.player(entity)
				.radius(radius)
				.ticks(-1)
				.updateLoc(true)
				.color(color)
				.rainbow(rainbow)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	GROWING_STARS(Material.BONE_MEAL) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double growthSpeed = ParticleSetting.STAR_GROWTH_SPEED.get(particleOwner, this);
			Double rotateSpeed = ParticleSetting.ROTATE_SPEED.get(particleOwner, this);
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			int taskId = StarEffect.builder()
				.player(entity)
				.radius(radius)
				.ticks(-1)
				.updateLoc(true)
				.color(color)
				.rainbow(rainbow)
				.growthSpeed(growthSpeed)
				.rotateSpeed(rotateSpeed)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	SPIRAL(Material.STRING) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double radius = ParticleSetting.SPIRAL_RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			int taskId = SpiralEffect.builder()
				.player(entity)
				.radius(radius)
				.ticks(-1)
				.pulseDelay(2)
				.color(color)
				.rainbow(rainbow)
				.updateLoc(true)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	SPRITE(Material.SOUL_SAND) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			Vector vector = new Vector(0, 1.5, 0);
			Location loc = particleOwner.getOnlinePlayer().getLocation().add(vector);
			int taskId = CircleEffect.builder()
				.player(entity)
				.location(loc)
				.updateVector(vector)
				.density(100)
				.radius(radius)
				.ticks(-1)
				.randomRotation(true)
				.color(color)
				.rainbow(rainbow)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	CHAOS(Material.FEATHER) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			Vector vector = new Vector(0, 1.5, 0);
			Location loc = particleOwner.getOnlinePlayer().getLocation().add(vector);
			int taskId = CircleEffect.builder()
				.player(entity)
				.location(loc)
				.updateVector(vector)
				.density(100)
				.radius(radius)
				.ticks(-1)
				.randomRotation(true)
				.color(color)
				.rainbow(rainbow)
				.fast(true)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	DOUBLE_CHAOS(Material.CYAN_WOOL) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double radius1 = ParticleSetting.DOUBLE_CHAOS_RADIUS_ONE.get(particleOwner, this);
			Boolean rainbow1 = ParticleSetting.DOUBLE_CHAOS_RAINBOW_ONE.get(particleOwner, this);
			Color color1 = ParticleSetting.DOUBLE_CHAOS_COLOR_ONE.get(particleOwner, this);

			Double radius2 = ParticleSetting.DOUBLE_CHAOS_RADIUS_TWO.get(particleOwner, this);
			Boolean rainbow2 = ParticleSetting.DOUBLE_CHAOS_RAINBOW_TWO.get(particleOwner, this);
			Color color2 = ParticleSetting.DOUBLE_CHAOS_COLOR_TWO.get(particleOwner, this);

			Vector vector = new Vector(0, 1.5, 0);
			Location loc = particleOwner.getOnlinePlayer().getLocation().add(vector);
			int taskId1 = CircleEffect.builder()
				.player(entity)
				.location(loc)
				.updateVector(vector)
				.density(100)
				.radius(radius1)
				.ticks(-1)
				.randomRotation(true)
				.color(color1)
				.rainbow(rainbow1)
				.fast(true)
				.start()
				.getTaskId();
			int taskId2 = CircleEffect.builder()
				.player(entity)
				.location(loc)
				.updateVector(vector)
				.density(100)
				.radius(radius2)
				.ticks(-1)
				.randomRotation(true)
				.color(color2)
				.rainbow(rainbow2)
				.fast(true)
				.startDelay(20)
				.start()
				.getTaskId();
			return new int[]{taskId1, taskId2};
		}
	},
	STORM(Material.COBWEB) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			StormEffect.RainPartile rainPartile = ParticleSetting.STORM_RAIN_PARTICLE.get(particleOwner, this);

			int taskId = StormEffect.builder()
				.player(entity)
				.ticks(-1)
				.updateLoc(true)
				.radius(radius)
				.rainParticle(rainPartile)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	DISCO(Material.REDSTONE_LAMP) {
		@Override
		int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			DiscoEffect.Direction direction = ParticleSetting.DISCO_DIRECTION.get(particleOwner, this);
			DiscoEffect.RainbowOption rainbowOption = ParticleSetting.DISCO_RAINBOW_OPTION.get(particleOwner, this);
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Integer lineLength = ParticleSetting.DISCO_LINE_LENGTH.get(particleOwner, this);

			Color sphereColor = ParticleSetting.DISCO_SPHERE_COLOR.get(particleOwner, this);
			Boolean sphereRainbow = ParticleSetting.DISCO_SPHERE_RAINBOW.get(particleOwner, this);

			Color lineColor = ParticleSetting.DISCO_LINE_COLOR.get(particleOwner, this);
			Boolean lineRainbow = ParticleSetting.DISCO_LINE_RAINBOW.get(particleOwner, this);

			Vector vector = new Vector(0, 4, 0);
			Location loc = particleOwner.getOnlinePlayer().getLocation().add(vector);

			int taskId = DiscoEffect.builder()
				.player(entity)
				.lineLength(lineLength)
				.maxLines(4)
				.sphereRadius(radius)
				.direction(direction)
				.sphereColor(sphereColor)
				.sphereRainbow(sphereRainbow)
				.lineColor(lineColor)
				.lineRainbow(lineRainbow)
				.rainbowOption(rainbowOption)
				.location(loc)
				.ticks(-1)
				.start()
				.getTaskId();
			return new int[]{taskId};
		}
	},
	WINGS(Material.ELYTRA) {
		@Override
		public int[] start(ParticleOwner particleOwner, HumanEntity entity) {
			int taskId = builder(particleOwner, entity)
				.ticks(PlayerUtils.isSelf(particleOwner, entity) ? -1 : TickTime.SECOND.x(15))
				.start()
				.getTaskId();
			return new int[]{taskId};
		}

		@Override
		public WingsEffectBuilder builder(ParticleOwner particleOwner, HumanEntity entity) {
			Boolean flapMode = ParticleSetting.WINGS_FLAP_MODE.get(particleOwner, this);
			Integer flapSpeed = ParticleSetting.WINGS_FLAP_SPEED.get(particleOwner, this);
			WingsEffect.WingStyle style = ParticleSetting.WINGS_STYLE.get(particleOwner, this);
			Color color1 = ParticleSetting.WINGS_COLOR_ONE.get(particleOwner, this);
			Color color2 = ParticleSetting.WINGS_COLOR_TWO.get(particleOwner, this);
			Color color3 = ParticleSetting.WINGS_COLOR_THREE.get(particleOwner, this);
			Boolean rainbow1 = ParticleSetting.WINGS_RAINBOW_ONE.get(particleOwner, this);
			Boolean rainbow2 = ParticleSetting.WINGS_RAINBOW_TWO.get(particleOwner, this);
			Boolean rainbow3 = ParticleSetting.WINGS_RAINBOW_THREE.get(particleOwner, this);

			return WingsEffect.builder()
				.owner(particleOwner)
				.entity(entity)
				.flapMode(flapMode)
				.flapSpeed(flapSpeed)
				.color1(color1)
				.rainbow1(rainbow1)
				.color2(color2)
				.rainbow2(rainbow2)
				.color3(color3)
				.rainbow3(rainbow3)
				.wingStyle(style);
		}

		@Override
		public String getPermission() {
			return "wings.use";
		}
	};

	private final Material material;
	private final int modelId;
	private final boolean isShape;
	private final String commandName = name().replace("_", "").toLowerCase();
	private final String displayName = StringUtils.camelCase(name().replace("_", " "));

	ParticleType(Material material) {
		this(material, 0, false);
	}

	ParticleType(Material material, boolean isShape) {
		this(material, 0, isShape);
	}

	ParticleType(CustomMaterial material) {
		this(material.getMaterial(), material.getModelId(), false);
	}

	ParticleType(CustomMaterial material, boolean isShape) {
		this(material.getMaterial(), material.getModelId(), isShape);
	}

	ParticleType(Material material, int modelId, boolean isShape) {
		this.material = material;
		this.modelId = modelId;
		this.isShape = isShape;
	}

	public ItemBuilder getDisplayItem() {
		return new ItemBuilder(material).modelId(modelId).itemFlags(ItemFlag.HIDE_ATTRIBUTES).name("&3" + getDisplayName());
	}

	public static ParticleType[] getShapes() {
		return Arrays.stream(ParticleType.values()).filter(effectType -> effectType.isShape).toArray(ParticleType[]::new);
	}

	public static ParticleType[] getPresets() {
		return Arrays.stream(ParticleType.values()).filter(effectType -> !effectType.isShape).toArray(ParticleType[]::new);
	}

	static ParticleType[] valuesExcept(ParticleType... particleTypes) {
		return Arrays.stream(ParticleType.values()).filter(effectType -> !Arrays.asList(particleTypes).contains(effectType)).toArray(ParticleType[]::new);
	}

	public int[] start(ParticleOwner particleOwner) {
		return start(particleOwner, (HumanEntity) Bukkit.getEntity(particleOwner.getUuid()));
	}

	abstract int[] start(ParticleOwner particleOwner, HumanEntity entity);

	public void run(Player player) {
		run(new ParticleService().get(player));
	}

	public void run(Player player, HumanEntity entity) {
		run(new ParticleService().get(player), entity);
	}

	public void run(ParticleOwner particleOwner) {
		particleOwner.start(this, start(particleOwner));
	}

	public void run(ParticleOwner particleOwner, HumanEntity entity) {
		if (PlayerUtils.isSelf(particleOwner, entity))
			particleOwner.start(this, start(particleOwner, entity));
		else
			particleOwner.addTaskIds(this, start(particleOwner, entity));
	}

	public Object builder(ParticleOwner particleOwner, HumanEntity entity) {
		return null;
	}

	public boolean canBeUsedBy(Player player) {
		return player.hasPermission(getPermission());
	}

	public String getPermission() {
		return "particles." + commandName;
	}
}
