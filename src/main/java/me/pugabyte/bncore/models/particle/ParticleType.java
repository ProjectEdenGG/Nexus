package me.pugabyte.bncore.models.particle;

import lombok.Getter;
import me.pugabyte.bncore.features.particles.effects.BandsEffect;
import me.pugabyte.bncore.features.particles.effects.CircleEffect;
import me.pugabyte.bncore.features.particles.effects.DiscoEffect;
import me.pugabyte.bncore.features.particles.effects.NyanCatEffect;
import me.pugabyte.bncore.features.particles.effects.PolygonEffect;
import me.pugabyte.bncore.features.particles.effects.SphereEffect;
import me.pugabyte.bncore.features.particles.effects.SpiralEffect;
import me.pugabyte.bncore.features.particles.effects.StarEffect;
import me.pugabyte.bncore.features.particles.effects.StormEffect;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;

@Getter
public enum ParticleType {
	CIRCLE(Material.ENDER_PEARL, true) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);
			Boolean whole = ParticleSetting.WHOLE.get(particleOwner, this);

			if (radius == null) radius = 2.0;
			if (rainbow == null) rainbow = true;
			if (whole == null) whole = true;
			if (color == null) color = Color.RED;

			int taskId = CircleEffect.builder()
					.player(particleOwner.getPlayer())
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
	STAR(Material.FIREWORK_CHARGE, true) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Double rotateSpeed = ParticleSetting.ROTATE_SPEED.get(particleOwner, this);
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			if (radius == null) radius = 2.0;
			if (rotateSpeed == null) rotateSpeed = 0.2;
			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;

			int taskId = StarEffect.builder()
					.player(particleOwner.getPlayer())
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
	TRIANGLE(Material.PAPER, true) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean whole = ParticleSetting.WHOLE.get(particleOwner, this);
			Double rotateSpeed = ParticleSetting.ROTATE_SPEED.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			if (radius == null) radius = 1.5;
			if (whole == null) whole = true;
			if (rotateSpeed == null) rotateSpeed = 0.1;
			if (color == null) color = Color.RED;
			if (rainbow == null) rainbow = true;

			int taskId = PolygonEffect.builder()
					.player(particleOwner.getPlayer())
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
	SQUARE(new ItemBuilder(Material.CARPET).dyeColor(ColorType.YELLOW).build(), true) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean whole = ParticleSetting.WHOLE.get(particleOwner, this);
			Double rotateSpeed = ParticleSetting.ROTATE_SPEED.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			if (radius == null) radius = 1.5;
			if (whole == null) whole = true;
			if (rotateSpeed == null) rotateSpeed = 0.1;
			if (color == null) color = Color.RED;
			if (rainbow == null) rainbow = true;

			int taskId = PolygonEffect.builder()
					.player(particleOwner.getPlayer())
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
		int[] start(ParticleOwner particleOwner) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean whole = ParticleSetting.WHOLE.get(particleOwner, this);
			Double rotateSpeed = ParticleSetting.ROTATE_SPEED.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			if (radius == null) radius = 1.5;
			if (whole == null) whole = true;
			if (rotateSpeed == null) rotateSpeed = 0.1;
			if (color == null) color = Color.RED;
			if (rainbow == null) rainbow = true;

			int taskId = PolygonEffect.builder()
					.player(particleOwner.getPlayer())
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
		int[] start(ParticleOwner particleOwner) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean whole = ParticleSetting.WHOLE.get(particleOwner, this);
			Double rotateSpeed = ParticleSetting.ROTATE_SPEED.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			if (radius == null) radius = 1.5;
			if (whole == null) whole = true;
			if (rotateSpeed == null) rotateSpeed = 0.1;
			if (color == null) color = Color.RED;
			if (rainbow == null) rainbow = true;

			int taskId = PolygonEffect.builder()
					.player(particleOwner.getPlayer())
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
	NYAN_CAT(new ItemBuilder(Material.MONSTER_EGG).spawnEgg(EntityType.OCELOT).build()) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			int taskId = NyanCatEffect.builder()
					.player(particleOwner.getPlayer())
					.ticks(-1)
					.start()
					.getTaskId();
			return new int[]{taskId};
		}
	},
	BANDS(Material.END_ROD) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			int taskId = BandsEffect.builder()
					.player(particleOwner.getPlayer())
					.ticks(-1)
					.rainbow(true)
					.start()
					.getTaskId();
			return new int[]{taskId};
		}
	},
	HALO(Material.GOLD_HELMET) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			if (radius == null) radius = 0.5;
			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;

			Vector vector = new Vector(0, 2.1, 0);
			Location loc = particleOwner.getPlayer().getLocation().add(vector);
			int taskId = CircleEffect.builder()
					.player(particleOwner.getPlayer())
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
	CIRCLES(Material.EYE_OF_ENDER) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;

			int taskId1 = CircleEffect.builder()
					.player(particleOwner.getPlayer())
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
					.player(particleOwner.getPlayer())
					.density(20)
					.radius(0.666)
					.ticks(-1)
					.whole(true)
					.updateLoc(true)
					.color(color)
					.rainbow(true)
					.startDelay(20)
					.start()
					.getTaskId();
			int taskId3 = CircleEffect.builder()
					.player(particleOwner.getPlayer())
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
					.player(particleOwner.getPlayer())
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
		int[] start(ParticleOwner particleOwner) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);

			if (radius == null) radius = 1.5;
			if (color == null) color = Color.RED;
			if (rainbow == null) rainbow = true;

			int taskId = SphereEffect.builder()
					.player(particleOwner.getPlayer())
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
	GROWING_STARS(new ItemBuilder(Material.INK_SACK).dyeColor(ColorType.WHITE).build()) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Double growthSpeed = ParticleSetting.STAR_GROWTH_SPEED.get(particleOwner, this);
			Double rotateSpeed = ParticleSetting.ROTATE_SPEED.get(particleOwner, this);
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			if (growthSpeed == null) growthSpeed = 0.1;
			if (rotateSpeed == null) rotateSpeed = 0.2;
			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;
			if (radius == null) radius = 2.0;

			int taskId = StarEffect.builder()
					.player(particleOwner.getPlayer())
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
		int[] start(ParticleOwner particleOwner) {
			Double radius = ParticleSetting.SPIRAL_RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			if (radius == null) radius = .15;
			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;

			int taskId = SpiralEffect.builder()
					.player(particleOwner.getPlayer())
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
		int[] start(ParticleOwner particleOwner) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			if (radius == null) radius = 1.5;
			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;

			Vector vector = new Vector(0, 1.5, 0);
			Location loc = particleOwner.getPlayer().getLocation().add(vector);
			int taskId = CircleEffect.builder()
					.player(particleOwner.getPlayer())
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
		int[] start(ParticleOwner particleOwner) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Boolean rainbow = ParticleSetting.RAINBOW.get(particleOwner, this);
			Color color = ParticleSetting.COLOR.get(particleOwner, this);

			if (radius == null) radius = 1.5;
			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;

			Vector vector = new Vector(0, 1.5, 0);
			Location loc = particleOwner.getPlayer().getLocation().add(vector);
			int taskId = CircleEffect.builder()
					.player(particleOwner.getPlayer())
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
	DOUBLE_CHAOS(new ItemBuilder(Material.WOOL).color(ColorType.CYAN).build()) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Double radius1 = ParticleSetting.DOUBLE_CHAOS_RADIUS_ONE.get(particleOwner, this);
			Boolean rainbow1 = ParticleSetting.DOUBLE_CHAOS_RAINBOW_ONE.get(particleOwner, this);
			Color color1 = ParticleSetting.DOUBLE_CHAOS_COLOR_ONE.get(particleOwner, this);

			Double radius2 = ParticleSetting.DOUBLE_CHAOS_RADIUS_TWO.get(particleOwner, this);
			Boolean rainbow2 = ParticleSetting.DOUBLE_CHAOS_RAINBOW_TWO.get(particleOwner, this);
			Color color2 = ParticleSetting.DOUBLE_CHAOS_COLOR_TWO.get(particleOwner, this);

			if (radius1 == null) radius1 = 1.5;
			if (radius2 == null) radius2 = 1.5;
			if (rainbow1 == null) rainbow1 = false;
			if (rainbow2 == null) rainbow2 = false;
			if (color1 == null) color1 = Color.TEAL;
			if (color2 == null) color2 = Color.YELLOW;

			Vector vector = new Vector(0, 1.5, 0);
			Location loc = particleOwner.getPlayer().getLocation().add(vector);
			int taskId1 = CircleEffect.builder()
					.player(particleOwner.getPlayer())
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
					.player(particleOwner.getPlayer())
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
	STORM(Material.WEB) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			StormEffect.RainPartile rainPartile = ParticleSetting.STORM_RAIN_PARTICLE.get(particleOwner, this);

			if (radius == null) radius = 1.5;
			if (rainPartile == null) rainPartile = StormEffect.RainPartile.RAIN;

			int taskId = StormEffect.builder()
					.player(particleOwner.getPlayer())
					.ticks(-1)
					.updateLoc(true)
					.radius(radius)
					.rainParticle(rainPartile)
					.start()
					.getTaskId();
			return new int[]{taskId};
		}
	},
	DISCO(Material.REDSTONE_LAMP_OFF) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			DiscoEffect.Direction direction = ParticleSetting.DISCO_DIRECTION.get(particleOwner, this);
			DiscoEffect.RainbowOption rainbowOption = ParticleSetting.DISCO_RAINBOW_OPTION.get(particleOwner, this);
			Double radius = ParticleSetting.RADIUS.get(particleOwner, this);
			Integer lineLength = ParticleSetting.DISCO_LINE_LENGTH.get(particleOwner, this);

			Color sphereColor = ParticleSetting.DISCO_SPHERE_COLOR.get(particleOwner, this);
			Boolean sphereRainbow = ParticleSetting.DISCO_SPHERE_RAINBOW.get(particleOwner, this);

			Color lineColor = ParticleSetting.DISCO_LINE_COLOR.get(particleOwner, this);
			Boolean lineRainbow = ParticleSetting.DISCO_LINE_RAINBOW.get(particleOwner, this);

			if (direction == null) direction = DiscoEffect.Direction.BOTH;
			if (rainbowOption == null) rainbowOption = DiscoEffect.RainbowOption.SLOW;
			if (radius == null) radius = 0.5;
			if (lineLength == null) lineLength = 5;
			if (sphereColor == null) sphereColor = Color.WHITE;
			if (sphereRainbow == null) sphereRainbow = false;
			if (lineColor == null) lineColor = Color.RED;
			if (lineRainbow == null) lineRainbow = true;

			int taskId = DiscoEffect.builder()
					.player(particleOwner.getPlayer())
					.ticks(-1)
					.lineLength(lineLength)
					.maxLines(4)
					.sphereRadius(radius)
					.direction(direction)
					.sphereColor(sphereColor)
					.sphereRainbow(sphereRainbow)
					.lineColor(lineColor)
					.lineRainbow(lineRainbow)
					.rainbowOption(rainbowOption)
					.start()
					.getTaskId();

			return new int[]{taskId};
		}
	};

	ItemStack itemStack;
	boolean isShape = false;
	String commandName = name().replace("_", "").toLowerCase();
	String displayName = StringUtils.camelCase(name().replace("_", " "));

	ParticleType(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	ParticleType(Material material) {
		this.itemStack = new ItemStack(material);
	}

	ParticleType(Material material, boolean isShape) {
		this.itemStack = new ItemStack(material);
		this.isShape = isShape;
	}

	ParticleType(ItemStack itemStack, boolean isShape) {
		this.itemStack = itemStack;
		this.isShape = isShape;
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

	abstract int[] start(ParticleOwner particleOwner);

	public void run(Player player) {
		ParticleOwner particleOwner = new ParticleService().get(player);
		particleOwner.addTasks(this, start(particleOwner));
	}
}
