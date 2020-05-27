package me.pugabyte.bncore.models.bearfair;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.Utils.sendActionBar;

@Data
@Entity("bfpoints_user")
@NoArgsConstructor
@AllArgsConstructor
@Converters(UUIDConverter.class)
public class BFPointsUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<BFPointSource, Map<LocalDate, Integer>> pointsReceivedToday = new HashMap<>();
	private int totalPoints;

	public transient static final int DAILY_SOURCE_MAX = 5;

	public BFPointsUser(UUID uuid) {
		this.uuid = uuid;
	}

	public void givePoints(int points) {
		totalPoints += points;
	}

	public void takePoints(int points) {
		totalPoints -= points;
	}

	public void giveDailyPoints(int points, BFPointSource source) {
		pointsReceivedToday.putIfAbsent(source, new HashMap<LocalDate, Integer>() {{
			put(LocalDate.now(), 0);
		}});

		int sourcePoints = pointsReceivedToday.get(source).get(LocalDate.now());

		if (sourcePoints == DAILY_SOURCE_MAX)
			return;

		// Only shows the message once, would repeat if >=
		if ((sourcePoints + points) == DAILY_SOURCE_MAX)
			getPlayer().sendMessage(colorize("Max daily points reached for " + StringUtils.camelCase(source.name())));

		givePoints(points);

		getPointsReceivedToday().get(source).put(LocalDate.now(), sourcePoints + points);

		sendActionBar(getPlayer(), "+" + points + " point" + (points == 1 ? "" : "s"));
	}

	public enum BFPointSource {
		ARCHERY,
		BASKETBALL,
		FROGGER,
		PUGDUNK,
		REFLECTION
	}

}
