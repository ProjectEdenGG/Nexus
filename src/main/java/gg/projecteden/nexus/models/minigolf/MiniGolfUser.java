package gg.projecteden.nexus.models.minigolf;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.DebugDotCommand;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.GolfBallColor;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse.MiniGolfHole;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "minigolf_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MiniGolfUser implements PlayerOwnedObject {
	@Id
	@NonNull
	@EqualsAndHashCode.Include
	private UUID uuid;
	private boolean playing;
	private GolfBallColor golfBallColor = GolfBallColor.WHITE;
	private GolfBall golfBall;
	private boolean debug;

	private Map<String, Map<Integer, Integer>> currentScorecard = new ConcurrentHashMap<>();
	private Map<String, Map<Integer, Integer>> bestScorecard = new ConcurrentHashMap<>();

	public Map<Integer, Integer> getCurrentScorecard(MiniGolfCourse course) {
		return currentScorecard.computeIfAbsent(course.getName(), $ -> new ConcurrentHashMap<>());
	}

	public Map<Integer, Integer> getBestScorecard(MiniGolfCourse course) {
		return bestScorecard.computeIfAbsent(course.getName(), $ -> new ConcurrentHashMap<>());
	}

	public void debug(boolean bool, String debug) {
		if (!bool)
			return;

		debug(debug);
	}

	public void debug(String message) {
		if (!debug)
			return;

		sendMessage(message);
	}

	public void debugDot(Location location, ColorType color) {
		if (!debug)
			return;

		DebugDotCommand.play(getPlayer(), location.clone(), color, TickTime.SECOND.x(1));
	}

	public void giveKit() {
		for (ItemStack itemStack : MiniGolfUtils.getKit(this.golfBallColor))
			if (!PlayerUtils.playerHas(this, itemStack))
				PlayerUtils.giveItem(getOnlinePlayer(), itemStack);
	}

	public void setGolfBallColor(GolfBallColor color) {
		this.golfBallColor = color;
		if (golfBall != null)
			golfBall.updateDisplayItem();
	}

	public boolean canHitBall() {
		return this.isOnline()
			&& golfBall != null
			&& !golfBall.isActive()
			&& golfBall.isAlive()
			&& golfBall.isMinVelocity()
			&& MiniGolfUtils.isClub(ItemUtils.getTool(getOnlinePlayer()));
	}

	public int getHolesInOne(MiniGolfCourse course) {
		var bestScorecard = getBestScorecard(course);
		int holesInOne = 0;
		for (MiniGolfHole hole : course.getHoles()) {
			if (!bestScorecard.containsKey(hole.getId()))
				continue;

			var strokes = bestScorecard.get(hole.getId());
			if (strokes != 1)
				continue;

			++holesInOne;
		}

		return holesInOne;
	}

	public boolean hasAllHolesInOne(MiniGolfCourse course) {
		return getHolesInOne(course) == course.getHoles().size();
	}

	public MiniGolfCourse getCurrentCourse() {
		var worldguard = new WorldGuardUtils(getOnlinePlayer());
		var regions = worldguard.getRegionsLikeAt(".*_minigolf_course.*", getOnlinePlayer().getLocation());
		if (regions.isEmpty())
			return null;

		var first = regions.iterator().next();
		var id = first.getId().split("_minigolf_course_?")[0];
		return new MiniGolfConfigService().get0().getCourse(id);
	}
}
