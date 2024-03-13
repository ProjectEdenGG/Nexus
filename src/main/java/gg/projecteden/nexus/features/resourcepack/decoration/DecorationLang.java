package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

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

			PlayerUtils.send(player, message);
		}
	}

	@AllArgsConstructor
	public enum DecorationCooldown {
		LOCKED("decoration-locked"),
		INTERACT("decoration-interact"),
		PLACE("decoration-place"),
		DESTROY("decoration-destroy"),
		IMPROPER_PLACEMENT("decoration-improper-place");

		private final String key;
		private final long ticks = TickTime.TICK.x(5);

		public boolean isOnCooldown(Player player) {
			return isOnCooldown(player, this.ticks);
		}

		public boolean isOnCooldown(Player player, long ticks) {
			return !new CooldownService().check(player, this.key, ticks);
		}

		public boolean isOnCooldown(Player player, UUID uuid) {
			return isOnCooldown(player, uuid, this.ticks);
		}

		public boolean isOnCooldown(Player player, UUID uuid, long ticks) {
			return !new CooldownService().check(player, this.key + "-" + uuid, ticks);
		}
	}


}
