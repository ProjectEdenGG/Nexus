package me.pugabyte.bncore.features.holidays.bearfair20.BFPoints;

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
import org.bukkit.entity.Player;

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

	public void givePoints(BFPointsUser user, int points) {
		int total = user.getTotalPoints();
		user.setTotalPoints(total + points);
		// TODO: Uncomment this
//		new BFPointsService().save(user);
	}

	public void takePoints(BFPointsUser user, int points) {
		int total = user.getTotalPoints();
		user.setTotalPoints(total - points);
		// TODO: Uncomment this
//		new BFPointsService().save(user);
	}

	public static void giveDailyPoints(Player player, int points, BFPointSource source) {
		BFPointsService service = new BFPointsService();
		BFPointsUser user = service.get(player.getUniqueId());
		Map<LocalDate, Integer> dailyMap = user.getPointsReceivedToday().get(source);
		int sourcePoints = 0;
		if (dailyMap != null)
			sourcePoints = dailyMap.get(LocalDate.now());

		if (sourcePoints == DAILY_SOURCE_MAX)
			return;

		// Only shows the message once, would repeat if >=
		if ((sourcePoints + points) == DAILY_SOURCE_MAX)
			player.sendMessage(colorize("Max daily points reached for " + StringUtils.camelCase(source.name())));

		sourcePoints += points;
		int finalSourcePoints = sourcePoints;

		user.getPointsReceivedToday().put(source, new HashMap<LocalDate, Integer>() {{
			put(LocalDate.now(), finalSourcePoints);
		}});

		// TODO: Uncomment this
//		service.save(user);

		String plural = points == 1 ? " point" : " points";
		sendActionBar(player, "+" + points + plural);
	}

}
