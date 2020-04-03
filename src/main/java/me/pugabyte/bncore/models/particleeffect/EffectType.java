package me.pugabyte.bncore.models.particleeffect;

import lombok.Getter;
import me.pugabyte.bncore.features.particles.effects.BandsEffect;
import me.pugabyte.bncore.features.particles.effects.CircleEffect;
import me.pugabyte.bncore.features.particles.effects.DiscoEffect;
import me.pugabyte.bncore.features.particles.effects.NyanCatEffect;
import me.pugabyte.bncore.features.particles.effects.SphereEffect;
import me.pugabyte.bncore.features.particles.effects.SpiralEffect;
import me.pugabyte.bncore.features.particles.effects.SquareEffect;
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

// TODO: SQUARE, SPHERE, SPIRAL, STORM
@Getter
public enum EffectType {
	SQUARE(new ItemBuilder(Material.CARPET).dyeColor(ColorType.YELLOW).build()) {
		@Override
		int[] start(EffectOwner effectOwner) {
			int taskId = SquareEffect.builder().start().getTaskId();
			return new int[]{taskId};
		}
	},
	CIRCLE(Material.ENDER_PEARL) {
		@Override
		int[] start(EffectOwner effectOwner) {
			Double radius = null;
			Boolean rainbow = null;
			Boolean whole = null;
			Color color = null;

			if (radius == null) radius = 2.0;
			if (rainbow == null) rainbow = true;
			if (whole == null) whole = true;
			if (color == null) color = Color.RED;

			int taskId = CircleEffect.builder()
					.player(effectOwner.getPlayer())
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
	STAR(Material.FIREWORK_CHARGE) {
		@Override
		int[] start(EffectOwner effectOwner) {
			Double radius = null;
			Double rotateSpeed = null;
			Boolean rainbow = null;
			Color color = null;

			if (radius == null) radius = 2.0;
			if (rotateSpeed == null) rotateSpeed = 0.2;
			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;

			int taskId = StarEffect.builder()
					.player(effectOwner.getPlayer())
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
	NYAN_CAT(new ItemBuilder(Material.MONSTER_EGG).spawnEgg(EntityType.OCELOT).build()) {
		@Override
		int[] start(EffectOwner effectOwner) {
			int taskId = NyanCatEffect.builder()
					.player(effectOwner.getPlayer())
					.ticks(-1)
					.start()
					.getTaskId();
			return new int[]{taskId};
		}
	},
	BANDS(Material.END_ROD) {
		@Override
		int[] start(EffectOwner effectOwner) {
			int taskId = BandsEffect.builder()
					.player(effectOwner.getPlayer())
					.ticks(-1)
					.rainbow(true)
					.start()
					.getTaskId();
			return new int[]{taskId};
		}
	},
	HALO(Material.GOLD_HELMET) {
		@Override
		int[] start(EffectOwner effectOwner) {
			Double radius = null;
			Boolean rainbow = null;
			Color color = null;

			if (radius == null) radius = 0.5;
			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;

			Vector vector = new Vector(0, 2.1, 0);
			Location loc = effectOwner.getPlayer().getLocation().add(vector);
			int taskId = CircleEffect.builder()
					.player(effectOwner.getPlayer())
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
		int[] start(EffectOwner effectOwner) {
			// TODO: Edit rainbow/color of each circle?
			Boolean rainbow = null;
			Color color = null;

			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;

			int taskId1 = CircleEffect.builder()
					.player(effectOwner.getPlayer())
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
					.player(effectOwner.getPlayer())
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
					.player(effectOwner.getPlayer())
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
					.player(effectOwner.getPlayer())
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
		int[] start(EffectOwner effectOwner) {
			int taskId = SphereEffect.builder().start().getTaskId();
			;
			return new int[]{taskId};
		}
	},
	GROWING_STARS(new ItemBuilder(Material.INK_SACK).dyeColor(ColorType.WHITE).build()) {
		@Override
		int[] start(EffectOwner effectOwner) {
			Double growthSpeed = null;
			Double rotateSpeed = null;
			Boolean rainbow = null;
			Color color = null;
			Double radius = null;

			if (growthSpeed == null) growthSpeed = 0.1;
			if (rotateSpeed == null) rotateSpeed = 0.2;
			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;
			if (radius == null) radius = 2.0;

			int taskId = StarEffect.builder()
					.player(effectOwner.getPlayer())
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
		int[] start(EffectOwner effectOwner) {
			int taskId = SpiralEffect.builder().start().getTaskId();
			return new int[]{taskId};
		}
	},
	SPRITE(Material.SOUL_SAND) {
		@Override
		int[] start(EffectOwner effectOwner) {
			Double radius = null;
			Boolean rainbow = null;
			Color color = null;

			if (radius == null) radius = 1.5;
			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;

			Vector vector = new Vector(0, 1.5, 0);
			Location loc = effectOwner.getPlayer().getLocation().add(vector);
			int taskId = CircleEffect.builder()
					.player(effectOwner.getPlayer())
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
		int[] start(EffectOwner effectOwner) {
			Double radius = null;
			Boolean rainbow = null;
			Color color = null;

			if (radius == null) radius = 1.5;
			if (rainbow == null) rainbow = true;
			if (color == null) color = Color.RED;

			Vector vector = new Vector(0, 1.5, 0);
			Location loc = effectOwner.getPlayer().getLocation().add(vector);
			int taskId = CircleEffect.builder()
					.player(effectOwner.getPlayer())
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
		int[] start(EffectOwner effectOwner) {
			Double radius1 = null;
			Boolean rainbow1 = null;
			Color color1 = null;

			Double radius2 = null;
			Boolean rainbow2 = null;
			Color color2 = null;

			if (radius1 == null) radius1 = 1.5;
			if (radius2 == null) radius2 = 1.5;
			if (rainbow1 == null) rainbow1 = false;
			if (rainbow2 == null) rainbow2 = false;
			if (color1 == null) color1 = Color.TEAL;
			if (color2 == null) color2 = Color.YELLOW;

			Vector vector = new Vector(0, 1.5, 0);
			Location loc = effectOwner.getPlayer().getLocation().add(vector);
			int taskId1 = CircleEffect.builder()
					.player(effectOwner.getPlayer())
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
					.player(effectOwner.getPlayer())
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
		int[] start(EffectOwner effectOwner) {
			int taskId = StormEffect.builder().start().getTaskId();
			return new int[]{taskId};
		}
	},
	DISCO(Material.REDSTONE_LAMP_OFF) {
		@Override
		int[] start(EffectOwner effectOwner) {
			DiscoEffect.Direction direction = (DiscoEffect.Direction) effectOwner.getSettings(this).get(EffectSetting.DISCO_DIRECTION);
			DiscoEffect.RainbowOption rainbowOption = (DiscoEffect.RainbowOption) effectOwner.getSettings(this).get(EffectSetting.DISCO_RAINBOW);
			Double radius = null;
			Integer lineLength = null;

			Color sphereColor = null;
			Boolean sphereRainbow = null;

			Color lineColor = null;
			Boolean lineRainbow = null;

			if (direction == null) direction = DiscoEffect.Direction.BOTH;
			if (rainbowOption == null) rainbowOption = DiscoEffect.RainbowOption.SLOW;
			if (radius == null) radius = 0.5;
			if (lineLength == null) lineLength = 5;
			if (sphereColor == null) sphereColor = Color.WHITE;
			if (sphereRainbow == null) sphereRainbow = false;
			if (lineColor == null) lineColor = Color.RED;
			if (lineRainbow == null) lineRainbow = true;


			int taskId = DiscoEffect.builder()
					.player(effectOwner.getPlayer())
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
	String commandName = name().replace("_", "").toLowerCase();
	String displayName = StringUtils.camelCase(name().replace("_", " "));

	EffectType(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	EffectType(Material material) {
		this.itemStack = new ItemStack(material);
	}

	EffectType(ItemStack itemStack, String displayName) {
		this.itemStack = itemStack;
		this.displayName = displayName;
	}

	static EffectType[] valuesExcept(EffectType... effectTypes) {
		return Arrays.stream(effectTypes).filter(effectType -> !Arrays.asList(effectTypes).contains(effectType)).toArray(EffectType[]::new);
	}

	abstract int[] start(EffectOwner effectOwner);

	public void run(Player player) {
		EffectOwner effectOwner = new EffectService().get(player);
		effectOwner.addTasks(this, start(effectOwner));
	}
}
