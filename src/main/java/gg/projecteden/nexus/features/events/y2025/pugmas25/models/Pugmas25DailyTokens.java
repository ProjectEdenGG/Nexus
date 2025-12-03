package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
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

		EventUserService eventUserService = new EventUserService();
		EventUser eventUser = eventUserService.get(player);

		// Brain-dead fix
		int tokensLeft = _getDailyTokensLeft(player, source);
		if (tokensLeft == 0) {
			ActionBarUtils.sendActionBar(player, "&cDaily token limit reached");
			return;
		}

		eventUser.giveTokens(amount);
		eventUserService.save(eventUser);

		int tokensSum = _getCurrentDailyTokens(player, source) + amount;

		Pugmas25UserService pugmasUserService = new Pugmas25UserService();
		Pugmas25User pugmasUser = pugmasUserService.get(player);
		switch (source) {
			case FROGGER -> pugmasUser.setDailyFroggerTokens(tokensSum);
			case MINIGOLF -> pugmasUser.setDailyMiniGolfTokens(tokensSum);
			case WHACAMOLE -> pugmasUser.setDailyWhacAWakkaTokens(tokensSum);
			case REFLECTION -> pugmasUser.setDailyReflectionTokens(tokensSum);
		}
		pugmasUserService.save(pugmasUser);

		ActionBarUtils.sendActionBar(player, "&a+" + amount + " Event Tokens");
		//

		// Busted AF
//		final int dailyTokensLeft = Math.abs(getDailyTokensLeft(player, source, 0));
//
//		if (dailyTokensLeft == 0) {
//			ActionBarUtils.sendActionBar(player, "&cDaily token limit reached");
//		} else {
//			user.giveTokens(source.getId(), amount, Pugmas25DailyTokenSource.getMaxes());
//			service.save(user);
//
//			ActionBarUtils.sendActionBar(player, "&a+" + amount + " Event Tokens");
//		}
	}

	public static int _getCurrentDailyTokens(OfflinePlayer player, Pugmas25DailyTokenSource source) {
		Pugmas25UserService pugmasUserService = new Pugmas25UserService();
		Pugmas25User pugmasUser = pugmasUserService.get(player);

		return switch (source) {
			case FROGGER -> pugmasUser.getDailyFroggerTokens();
			case MINIGOLF -> pugmasUser.getDailyMiniGolfTokens();
			case WHACAMOLE -> pugmasUser.getDailyWhacAWakkaTokens();
			case REFLECTION -> pugmasUser.getDailyReflectionTokens();
		};
	}

	public static int _getDailyTokensLeft(OfflinePlayer player, Pugmas25DailyTokenSource source) {
		return source.getMaxDailyTokens() - _getCurrentDailyTokens(player, source);
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

		public static Map<String, Integer> getMaxes() {
			Map<String, Integer> maxes = new HashMap<>();
			for (Pugmas25DailyTokenSource source : values()) {
				maxes.put(source.getId(), source.getMaxDailyTokens());
			}

			return maxes;
		}


	}
}
