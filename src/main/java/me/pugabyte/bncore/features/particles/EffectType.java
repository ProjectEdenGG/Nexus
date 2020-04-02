package me.pugabyte.bncore.features.particles;

import me.pugabyte.bncore.features.particles.effects.BandsEffect;
import me.pugabyte.bncore.features.particles.effects.CircleEffect;
import me.pugabyte.bncore.features.particles.effects.NyanCatEffect;
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

public enum EffectType {
	CIRCLES(Material.EYE_OF_ENDER) {
		@Override
		int[] start(Player player) {
			int taskId1 = CircleEffect.builder().player(player).location(player.getLocation()).density(10).radius(0.333).ticks(-1).whole(true).updateLoc(true).rainbow(true).start().getTaskId();
			int taskId2 = CircleEffect.builder().player(player).location(player.getLocation()).density(20).radius(0.666).ticks(-1).whole(true).updateLoc(true).rainbow(true).startDelay(20).start().getTaskId();
			int taskId3 = CircleEffect.builder().player(player).location(player.getLocation()).density(40).radius(0.999).ticks(-1).whole(true).updateLoc(true).rainbow(true).startDelay(40).start().getTaskId();
			int taskId4 = CircleEffect.builder().player(player).location(player.getLocation()).density(60).radius(1.333).ticks(-1).whole(true).updateLoc(true).rainbow(true).startDelay(60).start().getTaskId();
			return new int[] {taskId1, taskId2, taskId3, taskId4};
		}
	},
	STARS(Material.FIREWORK_CHARGE) {
		@Override
		int[] start(Player player) {
			int taskId = -1;
			return new int[] {taskId};
		}
	},
	GROWING_STARS(new ItemBuilder(Material.INK_SACK).dyeColor(ColorType.WHITE).build()) {
		@Override
		int[] start(Player player) {
			int taskId = -1;
			return new int[] {taskId};
		}
	},
	SPHERE(Material.SLIME_BALL) {
		@Override
		int[] start(Player player) {
			int taskId = -1;
			return new int[] {taskId};
		}
	},
	DISCO(Material.REDSTONE_LAMP_OFF) {
		@Override
		int[] start(Player player) {
			int taskId = -1;
			return new int[] {taskId};
		}
	},
	BANDS(Material.END_ROD) {
		@Override
		int[] start(Player player) {
			int taskId = BandsEffect.builder().player(player).ticks(-1).rainbow(true).start().getTaskId();
			return new int[] {taskId};
		}
	},
	NYAN_CAT(new ItemBuilder(Material.MONSTER_EGG).spawnEgg(EntityType.OCELOT).build()) {
		@Override
		int[] start(Player player) {
			int taskId = NyanCatEffect.builder().player(player).ticks(-1).start().getTaskId();
			return new int[] {taskId};
		}
	},
	HALO(Material.GOLD_HELMET) {
		@Override
		int[] start(Player player) {
			Vector vector = new Vector(0, 2.1, 0);
			Location loc = player.getLocation().add(vector);
			int taskId = CircleEffect.builder().player(player).location(loc).updateVector(vector).density(20).radius(0.5).ticks(-1).rainbow(true).start().getTaskId();
			return new int[] {taskId};
		}
	},
	CIRCLE(Material.ENDER_PEARL) {
		@Override
		int[] start(Player player) {
			Vector vector = new Vector(0, 1, 0);
			Location loc = player.getLocation().add(vector);
			int taskId = CircleEffect.builder().player(player).location(loc).updateVector(vector).density(100).radius(1.5).ticks(-1).rainbow(true).start().getTaskId();
			return new int[] {taskId};
		}
	},
	SPHERE_SLOW(Material.SOUL_SAND) {
		@Override
		int[] start(Player player) {
			Vector vector = new Vector(0, 1.5, 0);
			Location loc = player.getLocation().add(vector);
			int taskId = CircleEffect.builder().player(player).location(loc).updateVector(vector).density(100).radius(1.5).ticks(-1).randomRotation(true).rainbow(true).start().getTaskId();
			return new int[] {taskId};
		}
	},
	SPHERE_FAST(Material.FEATHER) {
		@Override
		int[] start(Player player) {
			Vector vector = new Vector(0, 1.5, 0);
			Location loc = player.getLocation().add(vector);
			int taskId = CircleEffect.builder().player(player).location(loc).updateVector(vector).density(100).radius(1.5).ticks(-1).randomRotation(true).rainbow(true).fast(true).start().getTaskId();
			return new int[] {taskId};
		}
	},
	BN_RINGS(new ItemBuilder(Material.WOOL).color(ColorType.CYAN).build(), "BN Rings") {
		@Override
		int[] start(Player player) {
			Vector vector = new Vector(0, 1.5, 0);
			Location loc = player.getLocation().add(vector);
			int taskId1 = CircleEffect.builder().player(player).location(loc).updateVector(vector).density(100).radius(1.5).ticks(-1).randomRotation(true).color(Color.TEAL).fast(true).start().getTaskId();
			int taskId2 = CircleEffect.builder().player(player).location(loc).updateVector(vector).density(100).radius(1.5).ticks(-1).randomRotation(true).color(Color.YELLOW).fast(true).startDelay(20).start().getTaskId();
			return new int[] {taskId1, taskId2};
		}
	},
	SPIRAL(Material.STRING) {
		@Override
		int[] start(Player player) {
			int taskId = -1;
			return new int[] {taskId};
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

	abstract int[] start(Player player);

	public void run(Player player) {
		int[] taskIds = start(player);
		ParticleUtils.addEffectTask(player, this, taskIds);
	}
}
