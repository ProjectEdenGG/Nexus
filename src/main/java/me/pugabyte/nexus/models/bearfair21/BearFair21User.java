package me.pugabyte.nexus.models.bearfair21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.nexus.utils.ActionBarUtils.sendActionBar;
import static me.pugabyte.nexus.utils.StringUtils.plural;

@Data
@Entity("bearfair21_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class BearFair21User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	// Points
	public transient static final int DAILY_SOURCE_POINTS = 5;
	public transient static final int DAILY_SOURCE_MAX = 5;
	private Map<BF21PointSource, Map<LocalDate, Integer>> pointsReceivedToday = new HashMap<>();
	//

	public void giveDailyPoints(BF21PointSource source) {
		pointsReceivedToday.putIfAbsent(source, new HashMap<>() {{
			put(LocalDate.now(), 0);
		}});

		int timesCompleted = pointsReceivedToday.get(source).getOrDefault(LocalDate.now(), 0);

		// TODO BF21: Uncomment
//		if (timesCompleted == DAILY_SOURCE_MAX)
//			return;
//
//		if ((timesCompleted + 1) == DAILY_SOURCE_MAX)
//			send(BearFair20.PREFIX + "Max daily points reached for &e" + StringUtils.camelCase(source.name()));

		getPointsReceivedToday().get(source).put(LocalDate.now(), timesCompleted + 1);

		givePoints(DAILY_SOURCE_POINTS, true);
	}

	public void givePoints(int points, boolean actionBar) {
		if (actionBar)
			sendActionBar(getPlayer(), "&e+" + points + plural(" point", points));
		givePoints(points);
	}

	public void givePoints(int points) {
		send("TODO BF21: +" + points);
	}

	public enum BF21PointSource {
		ARCHERY,
		MINIGOLF,
		FROGGER,
		SEEKER,
		REFLECTION
	}
}
