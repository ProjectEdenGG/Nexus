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
import java.util.Map;

@Getter
public enum ParticleType {
	CIRCLE(Material.ENDER_PEARL) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);

			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);
			Color color = (Color) settings.get(ParticleSetting.COLOR);
			Boolean whole = (Boolean) settings.get(ParticleSetting.CIRCLE_WHOLE);

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
			return new int[] {taskId};
		}
	},
	STAR(Material.FIREWORK_CHARGE) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double rotateSpeed = (Double) settings.get(ParticleSetting.STAR_ROTATE_SPEED);
			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);
			Color color = (Color) settings.get(ParticleSetting.COLOR);

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
			return new int[] {taskId};
		}
	},
	TRIANGLE(Material.PAPER) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			Boolean whole = null; // TODO Setting - Whole
			Double rotateSpeed = null; //TODO Setting - Polygon Speed
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);
			Color color = (Color) settings.get(ParticleSetting.COLOR);

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
			return new int[] {taskId};
		}
	},
	SQUARE(new ItemBuilder(Material.CARPET).dyeColor(ColorType.YELLOW).build()) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			Boolean whole = null; // TODO Setting - Whole
			Double rotateSpeed = null; //TODO Setting - Polygon Speed
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);
			Color color = (Color) settings.get(ParticleSetting.COLOR);

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
			return new int[] {taskId};
		}
	},
	PENTAGON(Material.PAPER) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			Boolean whole = null; // TODO Setting - Whole
			Double rotateSpeed = null; //TODO Setting - Polygon Speed
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);
			Color color = (Color) settings.get(ParticleSetting.COLOR);

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
			return new int[] {taskId};
		}
	},
	HEXAGON(Material.PAPER) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			Boolean whole = null; // TODO Setting - Whole
			Double rotateSpeed = null; //TODO Setting - Polygon Speed
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);
			Color color = (Color) settings.get(ParticleSetting.COLOR);

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
			return new int[] {taskId};
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
			return new int[] {taskId};
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
			return new int[] {taskId};
		}
	},
	HALO(Material.GOLD_HELMET) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);
			Color color = (Color) settings.get(ParticleSetting.COLOR);

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
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);
			Color color = (Color) settings.get(ParticleSetting.COLOR);

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
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			Color color = (Color) settings.get(ParticleSetting.COLOR);
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);

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
			return new int[] {taskId};
		}
	},
	GROWING_STARS(new ItemBuilder(Material.INK_SACK).dyeColor(ColorType.WHITE).build()) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double growthSpeed = (Double) settings.get(ParticleSetting.STAR_GROWTH_SPEED);
			Double rotateSpeed = (Double) settings.get(ParticleSetting.STAR_ROTATE_SPEED);
			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);
			Color color = (Color) settings.get(ParticleSetting.COLOR);

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
			return new int[] {taskId};
		}
	},
	SPIRAL(Material.STRING) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double radius = null; //TODO Custom Setting: .15 -> .35
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);
			Color color = (Color) settings.get(ParticleSetting.COLOR);

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
			return new int[] {taskId};
		}
	},
	SPRITE(Material.SOUL_SAND) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);
			Color color = (Color) settings.get(ParticleSetting.COLOR);

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
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			Boolean rainbow = (Boolean) settings.get(ParticleSetting.RAINBOW);
			Color color = (Color) settings.get(ParticleSetting.COLOR);

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
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double radius1 = (Double) settings.get(ParticleSetting.DOUBLE_CHAOS_RADIUS_ONE);
			Boolean rainbow1 = (Boolean) settings.get(ParticleSetting.DOUBLE_CHAOS_RAINBOW_ONE);
			Color color1 = (Color) settings.get(ParticleSetting.DOUBLE_CHAOS_COLOR_ONE);

			Double radius2 = (Double) settings.get(ParticleSetting.DOUBLE_CHAOS_RADIUS_TWO);
			Boolean rainbow2 = (Boolean) settings.get(ParticleSetting.DOUBLE_CHAOS_RAINBOW_TWO);
			Color color2 = (Color) settings.get(ParticleSetting.DOUBLE_CHAOS_COLOR_TWO);

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
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			StormEffect.RainPartile rainPartile = (StormEffect.RainPartile) settings.get(ParticleSetting.STORM_RAIN_PARTICLE);

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
			return new int[] {taskId};
		}
	},
	DISCO(Material.REDSTONE_LAMP_OFF) {
		@Override
		int[] start(ParticleOwner particleOwner) {
			Map<ParticleSetting, Object> settings = particleOwner.getSettings(this);
			DiscoEffect.Direction direction = (DiscoEffect.Direction) settings.get(ParticleSetting.DISCO_DIRECTION);
			DiscoEffect.RainbowOption rainbowOption = (DiscoEffect.RainbowOption) settings.get(ParticleSetting.DISCO_RAINBOW);
			Double radius = (Double) settings.get(ParticleSetting.RADIUS);
			Integer lineLength = (Integer) settings.get(ParticleSetting.DISCO_LINE_LENGTH);

			Color sphereColor = (Color) settings.get(ParticleSetting.DISCO_SPHERE_COLOR);
			Boolean sphereRainbow = (Boolean) settings.get(ParticleSetting.DISCO_SPHERE_RAINBOW);

			Color lineColor = (Color) settings.get(ParticleSetting.DISCO_LINE_COLOR);
			Boolean lineRainbow = (Boolean) settings.get(ParticleSetting.DISCO_LINE_RAINBOW);

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

			return new int[] {taskId};
		}
	};

	ItemStack itemStack;
	String commandName = name().replace("_", "").toLowerCase();
	String displayName = StringUtils.camelCase(name().replace("_", " "));

	ParticleType(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	ParticleType(Material material) {
		this.itemStack = new ItemStack(material);
	}

	ParticleType(ItemStack itemStack, String displayName) {
		this.itemStack = itemStack;
		this.displayName = displayName;
	}

	static ParticleType[] valuesExcept(ParticleType... particleTypes) {
		return Arrays.stream(particleTypes).filter(effectType -> !Arrays.asList(particleTypes).contains(effectType)).toArray(ParticleType[]::new);
	}

	abstract int[] start(ParticleOwner particleOwner);

	public void run(Player player) {
		ParticleOwner particleOwner = new ParticleService().get(player);
		particleOwner.addTasks(this, start(particleOwner));
	}
}
