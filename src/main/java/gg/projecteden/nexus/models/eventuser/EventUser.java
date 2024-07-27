package gg.projecteden.nexus.models.eventuser;

import com.mongodb.DBObject;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PreLoad;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.Events;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.ActionBarUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.nexus.utils.StringUtils.plural;

@Data
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
	private List<ItemStack> rewardItems = new ArrayList<>();

	@PreLoad
	void fix(DBObject dbObject) {
		if (dbObject.containsField("tokensReceivedToday")) {
			dbObject.put("tokensReceivedByDate", dbObject.get("tokensReceivedToday"));
			dbObject.removeField("tokensReceivedToday");
		}
	}

	public void addRewardItem(ItemStack item) {
		rewardItems.add(item);
	}

	private Map<String, Map<LocalDate, Integer>> tokensReceivedByDate = new ConcurrentHashMap<>();

	private Map<LocalDate, Integer> getTokensReceived(String id) {
		return this.tokensReceivedByDate.computeIfAbsent(id, $ -> new ConcurrentHashMap<>());
	}

	public int getTokensReceivedToday(String id) {
		return getTokensReceived(id, LocalDate.now());
	}

	public int getTokensReceived(String id, LocalDate date) {
		return getTokensReceived(id).getOrDefault(date, 0);
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

		Map<LocalDate, Integer> today = getTokensReceived(id);
		today.put(LocalDate.now(), getTokensReceivedToday(id) + amount);
		this.tokensReceivedByDate.put(id, today);
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

	public void checkHasTokens(int tokens) {
		if (!hasTokens(tokens))
			throw new InvalidInputException("You do not have enough tokens");
	}

	public void charge(int tokens) {
		checkHasTokens(tokens);
		takeTokens(tokens);
	}

}
