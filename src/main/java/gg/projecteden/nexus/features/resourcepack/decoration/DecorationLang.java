package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.DebugDotCommand;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Debug.DebugType;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DecorationLang {
	@Getter
	private static final String PREFIX = StringUtils.getPrefix("Decoration");

	@AllArgsConstructor
	public enum DecorationError {
		LOCKED(PREFIX + "&cThis decoration is locked"),
		SEAT_OCCUPIED(PREFIX + "&cSeat is occupied"),
		IMPROPER_PLACEMENT(PREFIX + "&cYou cannot place this decoration in this way"),
		LACKING_FUNDS(PREFIX + "&cYou don't have enough money to buy this"),

		// Special
		UNKNOWN_TARGET_DECORATION("&CYou are not looking at a decoration!"),
		DISABLED(PREFIX + "&cThis feature has temporarily been disabled"),
		WORLDGUARD_USAGE("&c&lHey! &7Sorry, but you can't use that here."),
		UNRELEASED_FEATURE(PREFIX + "&cYou cannot use this feature yet"),
		CONTACT_ADMINS(PREFIX + "&cContact an admin if you see this message"),
		;

		@Getter
		final String message;

		public void send(Player player) {
			send(player, null);
		}

		public void send(Player player, String extra) {
			if (player == null || !player.isOnline())
				return;

			String message = this.message;
			if (extra != null)
				message += extra;

			sendFinal(player, message);
		}

		public void sendCustom(Player player, @Nullable String message) {
			if (player == null || !player.isOnline())
				return;

			if (message == null)
				message = this.message;

			sendFinal(player, message);
		}

		private void sendFinal(Player player, String message) {
			PlayerUtils.send(player, message);
		}
	}

	public static final int DEFAULT_COOLDOWN_TICKS = 5;

	@AllArgsConstructor
	public enum DecorationCooldown {
		LOCKED("decoration-locked"),
		INTERACT("decoration-interact"),
		PLACE("decoration-place"),
		DESTROY("decoration-destroy"),
		IMPROPER_PLACEMENT("decoration-improper-place");

		private final String key;
		private final long ticks = TickTime.TICK.x(DEFAULT_COOLDOWN_TICKS);

		public boolean isOnCooldown(Player player) {
			return isOnCooldown(player, this.ticks);
		}

		public boolean isOnCooldown(Player player, long ticks) {
			return CooldownService.isOnCooldown(player, this.key, ticks);
		}

		public boolean isOnCooldown(Player player, UUID uuid) {
			return isOnCooldown(player, uuid, this.ticks);
		}

		public boolean isOnCooldown(Player player, UUID uuid, long ticks) {
			return CooldownService.isOnCooldown(player, this.key + "-" + uuid, ticks);
		}
	}

	public static void debug(Player player, String message) {
		Debug.log(player, DebugType.DECORATION, message);
	}

	public static void debug(Player player, JsonBuilder json) {
		Debug.log(player, DebugType.DECORATION, json);
	}

	public static void deepDebug(Player player, String message) {
		Debug.log(player, DebugType.DECORATION_FINDER, message);
	}

	public static void debugDot(Player player, Location location, ColorType color) {
		if (!Debug.isEnabled(player, DebugType.DECORATION_FINDER))
			return;

		DebugDotCommand.play(player, location.clone().toCenterLocation(), color, TickTime.SECOND.x(1));
	}
}
