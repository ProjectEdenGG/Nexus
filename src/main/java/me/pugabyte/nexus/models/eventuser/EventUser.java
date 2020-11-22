package me.pugabyte.nexus.models.eventuser;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.nexus.utils.ActionBarUtils.sendActionBar;
import static me.pugabyte.nexus.utils.StringUtils.plural;

@Data
@Builder
@Entity("event_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
/*
	TODO
		Create an EventType enum
		Use Pugmas20 inventory store to manage event items between events
		Add a way to clear the eventtype inventory once upon entering the world
		Add a way to differentiate between event-world only items and survival items
		Add a way to send survival items back to survival (delivery service)
 */
public class EventUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int tokens;

	private Map<String, Map<LocalDate, Integer>> tokensReceivedToday = new HashMap<>();

	public int getTokensReceivedToday(String id) {
		return tokensReceivedToday.getOrDefault(id, new HashMap<>()).getOrDefault(LocalDate.now(), 0);
	}

	public void giveTokens(String id, int amount, Map<String, Integer> maxes) {
		Map<LocalDate, Integer> today = tokensReceivedToday.getOrDefault(id, new HashMap<>());
		int max = maxes.getOrDefault(id, 0);
		int newAmount = Math.min(max, getTokensReceivedToday(id) + amount);
		today.put(LocalDate.now(), newAmount);
		tokensReceivedToday.put(id, today);
	}

	public void giveTokens(int tokens) {
		giveTokens(tokens, true);
	}

	public void giveTokens(int tokens, boolean actionBar) {
		this.tokens += tokens;

		if (actionBar)
			sendActionBar(getPlayer(), "&e+" + tokens + plural(" event token", tokens));
	}

	public void takeTokens(int tokens) {
		this.tokens -= tokens;
	}

}
