package gg.projecteden.nexus.utils;

import fr.skytasul.glowingentities.GlowingEntities;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.parchment.OptionalPlayer;
import lombok.Builder;
import lombok.NonNull;
import org.bukkit.ChatColor;
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

@SuppressWarnings("deprecation")
public class GlowUtils {

	public static GlowingEntities API = new GlowingEntities(Nexus.getInstance());

	public static void shutdown() {
		API.disable();
	}

	public static GlowBuilder glow(@NonNull Entity entity) {
		return glow(List.of(entity));
	}

	public static GlowBuilder glow(@NonNull Collection<@NonNull ? extends Entity> entities) {
		return new GlowBuilder(entities).state(true);
	}

	public static GlowBuilder unglow(@NonNull Entity entity) {
		return unglow(List.of(entity));
	}

	public static GlowBuilder unglow(@NonNull Collection<@NonNull ? extends Entity> entities) {
		return new GlowBuilder(entities).state(false);
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
			if (true)
				return;

			Debug.log((state ? "Glowing" : "Unglowing") + " " + entities.size() + " entities for " + receivers.size() + " players");
			if (!state)
				color = null;

			ColorType colorType = ColorType.of(color);

			if (color != null && colorType == null)
				Nexus.warn("Could not find ColorType from GlowColor " + color.name());

			ChatColor chatColor = colorType == null ? ChatColor.WHITE : colorType.toBukkitChatColor();

			if (colorType != null && chatColor == null) {
				Nexus.warn("Could not find ChatColor from ColorType " + colorType.name());
				chatColor = ChatColor.WHITE;
			}

			try {
				for (Entity entity : entities)
					for (Player receiver : receivers)
						if (state) {
							API.setGlowing(entity, receiver, chatColor);
							Debug.log("glow(" + (entity instanceof Player player ? Nickname.of(player) : entity.getType().name()) + ", " + Nickname.of(receiver) + ", " + chatColor.name() + ")");
						} else {
							API.unsetGlowing(entity, receiver);
							Debug.log("unglow(" + (entity instanceof Player player ? Nickname.of(player) : entity.getType().name()) + ", " + Nickname.of(receiver) + ")");
						}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
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
