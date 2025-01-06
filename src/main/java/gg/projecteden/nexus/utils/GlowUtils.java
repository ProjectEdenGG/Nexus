package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.hooks.Hook;
import gg.projecteden.parchment.OptionalPlayer;
import lombok.Builder;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.CheckReturnValue;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GlowUtils {

	public static GlowBuilder glow(@NonNull Entity entity) {
		return glow(List.of(entity));
	}

	public static GlowBuilder glow(@NonNull Collection<@NonNull ? extends Entity> entities) {
		return new GlowBuilder(entities).state(false);
	}

	public static GlowBuilder unglow(@NonNull Entity entity) {
		return unglow(List.of(entity));
	}

	public static GlowBuilder unglow(@NonNull Collection<@NonNull ? extends Entity> entities) {
		return new GlowBuilder(entities).state(true);
	}

	public static void glow(Block block, long ticks, OptionalPlayer viewer) {
		glow(block, ticks, viewer, GlowColor.RED);
	}

	public static void glow(Block block, long ticks, OptionalPlayer viewer, GlowColor color) {
		glow(block, ticks, Collections.singletonList(viewer), color);
	}

	public static void glow(Block block, long ticks, List<? extends OptionalPlayer> viewers, GlowColor color) {
		List<Player> _viewers = PlayerUtils.getNonNullPlayers(viewers);

		Material material = block.getType();
		if (Nullables.isNullOrAir(material))
			material = Material.WHITE_CONCRETE;

		Location location = block.getLocation();
		World blockWorld = block.getWorld();
		FallingBlock fallingBlock = blockWorld.spawnFallingBlock(LocationUtils.getCenteredLocation(location), material.createBlockData());
		fallingBlock.setDropItem(false);
		fallingBlock.setGravity(false);
		fallingBlock.setInvulnerable(true);
		fallingBlock.setVelocity(new Vector(0, 0, 0));

		GlowTask.builder()
				.duration(ticks)
				.entity(fallingBlock)
				.color(color)
				.viewers(_viewers)
				.onComplete(() -> {
					fallingBlock.remove();
					for (Player viewer : _viewers)
						viewer.sendBlockChange(location, block.getType().createBlockData());
				})
				.start();
	}

	public static class GlowBuilder {
		private Collection<? extends Entity> entities;
		private boolean state;
		private GlowColor color;
		private Collection<? extends Player> receivers;

		public GlowBuilder(Collection<? extends Entity> entities) {
			this.entities = entities;
		}

		@CheckReturnValue
		public GlowBuilder state(boolean state) {
			this.state = state;
			return this;
		}

		@CheckReturnValue
		public GlowBuilder enable() {
			return state(true);
		}

		@CheckReturnValue
		public GlowBuilder disable() {
			return state(false);
		}

		@CheckReturnValue
		public GlowBuilder color(GlowColor color) {
			this.color = color;
			return this;
		}

		@CheckReturnValue
		public GlowBuilder receivers(@NonNull Player player) {
			return receivers(List.of(player));
		}

		@CheckReturnValue
		public GlowBuilder receivers(Collection<? extends OptionalPlayer> receivers) {
			this.receivers = PlayerUtils.getNonNullPlayers(receivers);
			return this;
		}

		public void run() {
			if (!state)
				color = null;

			Hook.GLOWAPI.setGlowing(entities, color, receivers);
		}

	}

	public static class GlowTask {

		@Builder(buildMethodName = "start")
		public GlowTask(long duration, Entity entity, GlowColor color, List<? extends OptionalPlayer> viewers, Runnable onComplete) {
			GlowUtils.glow(entity).color(color).receivers(viewers).run();
			Tasks.wait(duration, () -> GlowUtils.unglow(entity).receivers(viewers).run());
			if (onComplete != null)
				Tasks.wait(duration + 1, onComplete);
		}

	}

	public enum GlowColor {
		BLACK,
		DARK_BLUE,
		DARK_GREEN,
		DARK_AQUA,
		DARK_RED,
		DARK_PURPLE,
		GOLD,
		GRAY,
		DARK_GRAY,
		BLUE,
		GREEN,
		AQUA,
		RED,
		PURPLE,
		YELLOW,
		WHITE,
		NONE,
		;
	}

}
