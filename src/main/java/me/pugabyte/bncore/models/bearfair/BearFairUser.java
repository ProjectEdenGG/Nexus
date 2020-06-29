package me.pugabyte.bncore.models.bearfair;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

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
@Converters({UUIDConverter.class, LocationConverter.class, ItemStackConverter.class})
public class BearFairUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	// Points
	public transient static final int DAILY_SOURCE_MAX = 5;
	private Map<BFPointSource, Map<LocalDate, Integer>> pointsReceivedToday = new HashMap<>();
	private int totalPoints;
	// First Visit
	private boolean firstVisit = true;
	// Easter Eggs
	@Property(concreteClass = Location.class)
	private List<Location> easterEggsLocs = new ArrayList<>();
	// Quests
	private boolean Quest_Main_Start = false;
	private boolean Quest_Main_Finish = false;
	private int Quest_Main_Step = 0;
	private boolean Quest_Hive_Access = false;
	private boolean Quest_talkedWith_Collector = false;
	private boolean Quest_talkedWith_Miner = false;
	//
	private boolean Quest_SDU_Start = false;
	private boolean Quest_SDU_Finish = false;
	private int Quest_SDU_Step = 0;
	//
	private boolean Quest_MGN_Start = false;
	private boolean Quest_MGN_Finish = false;
	private int Quest_MGN_Step = 0;
	@Property(concreteClass = ItemStack.class)
	private List<ItemStack> arcadePieces = new ArrayList<>();
	//
	private boolean Quest_Halloween_Start = false;
	private boolean Quest_Halloween_Finish = false;
	private int Quest_Halloween_Step = 0;
	//
	private boolean Quest_Pugmas_Start = false;
	private boolean Quest_Pugmas_Finish = false;
	private int Quest_Pugmas_Step = 0;
	private boolean Quest_Pugmas_Switched = false;
	@Property(concreteClass = Location.class)
	private List<Location> presentLocs = new ArrayList<>();
	//

	public BearFairUser(UUID uuid) {
		this.uuid = uuid;
	}

	public void givePoints(int points, boolean actionBar) {
		if (actionBar)
			sendActionBar(getPlayer(), "+" + points + " point" + (points == 1 ? "" : "s"));
		givePoints(points);
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
