package me.pugabyte.bncore.models.eventuser;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.bncore.utils.ActionBarUtils.sendActionBar;
import static me.pugabyte.bncore.utils.StringUtils.plural;

@Data
@Builder
@Entity("event_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class EventUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int tokens;

	private Map<String, Map<LocalDate, Integer>> tokensReceivedToday = new HashMap<>();

	public int getTokensReceivedToday(String id) {
		return tokensReceivedToday.getOrDefault(id, new HashMap<>()).getOrDefault(LocalDate.now(), 0);
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
