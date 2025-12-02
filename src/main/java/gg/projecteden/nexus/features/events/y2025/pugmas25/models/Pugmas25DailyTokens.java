package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Pugmas25DailyTokens {

	public static void giveDailyTokens(Player player, Pugmas25DailyTokenSource source, int amount) {
		if (!Pugmas25.get().isEventActive())
			return;

		EventUserService service = new EventUserService();
		EventUser user = service.get(player);

		final int dailyTokensLeft = Math.abs(getDailyTokensLeft(player, source, 0));

		if (dailyTokensLeft == 0) {
			ActionBarUtils.sendActionBar(player, "&cDaily token limit reached");
		} else {
			user.giveTokens(source.getId(), amount, Pugmas25DailyTokenSource.getMaxes());
			service.save(user);

			ActionBarUtils.sendActionBar(player, "&a+" + amount + " Event Tokens");
		}

		service.save(user);
	}

	public static int getDailyTokensLeft(OfflinePlayer player, Pugmas25DailyTokenSource source, int amount) {
		EventUserService service = new EventUserService();
		EventUser user = service.get(player);

		return user.getDailyTokensLeft(source.getId(), amount, Pugmas25DailyTokenSource.getMaxes());
	}

	@Getter
	@AllArgsConstructor
	public enum Pugmas25DailyTokenSource {
		FROGGER(15),
		MINIGOLF(15),
		REFLECTION(15),
		WHACAMOLE(15),
		;

		private final int maxDailyTokens;

		public String getId() {
			return "pugmas25_" + name().toLowerCase();
		}

		public String getName() {
			return StringUtils.camelCase(this);
		}

		private static Map<String, Integer> maxes = null;

		public static Map<String, Integer> getMaxes() {
			if (maxes != null)
				return maxes;

			maxes = new HashMap<>();
			for (Pugmas25DailyTokenSource source : values()) {
				maxes.put(source.getId(), source.getMaxDailyTokens());
			}

			return maxes;
		}


	}
}
