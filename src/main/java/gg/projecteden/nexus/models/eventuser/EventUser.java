package gg.projecteden.nexus.models.eventuser;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.Events;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.ActionBarUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.utils.StringUtils.plural;

@Data
@Builder
@Entity(value = "event_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class EventUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int tokens;

	private Map<String, Map<LocalDate, Integer>> tokensReceivedToday = new HashMap<>();

	public int getTokensReceivedToday(String id) {
		return getTokensRecieved(id, LocalDate.now());
	}

	public int getTokensRecieved(String id, LocalDate date) {
		return tokensReceivedToday.getOrDefault(id, new HashMap<>()).getOrDefault(date, 0);
	}

	public int getDailyTokensLeft(String id, int amount, Map<String, Integer> maxes) {
		int max = maxes.getOrDefault(id, 0);
		int tokensReceivedToday = getTokensReceivedToday(id);
		return (tokensReceivedToday + amount) - max;
	}

	public void giveTokens(String id, int amount, Map<String, Integer> maxes) {
		int excess = getDailyTokensLeft(id, amount, maxes);
		if (excess > 0)
			amount -= excess;

		tokens += amount;

		Map<LocalDate, Integer> today = this.tokensReceivedToday.getOrDefault(id, new HashMap<>());
		today.put(LocalDate.now(), getTokensReceivedToday(id) + amount);
		this.tokensReceivedToday.put(id, today);
	}

	public void giveTokens(int tokens) {
		giveTokens(tokens, true);
	}

	public void giveTokens(int tokens, boolean actionBar) {
		this.tokens += tokens;
		if (isOnline()) {
			sendMessage(Events.PREFIX + "You have &areceived &e" + tokens + " event tokens&3. New balance: &e" + this.tokens);

			if (actionBar)
				ActionBarUtils.sendActionBar(getOnlinePlayer(), "&e+" + tokens + plural(" event token", tokens));
		}
	}

	public void takeTokens(int tokens) {
		this.tokens -= tokens;
		sendMessage(Events.STORE_PREFIX + "You have &cspent &e" + tokens + " event tokens&3. New balance: &e" + this.tokens);
	}

	public boolean hasTokens(int tokens) {
		return this.tokens >= tokens;
	}

}
