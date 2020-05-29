package me.pugabyte.bncore.models.bearfair;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Location;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.Utils.sendActionBar;

@Data
@Entity("bearfair_user")
@NoArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class BearFairUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	// Points
	public transient static final int DAILY_SOURCE_MAX = 5;
	private Map<BFPointSource, Map<LocalDate, Integer>> pointsReceivedToday = new HashMap<>();
	private int totalPoints;
	// Easter Eggs
	@Property(concreteClass = Location.class)
	private List<Location> easterEggsLocs = new ArrayList<>();
	//

	public BearFairUser(UUID uuid) {
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
