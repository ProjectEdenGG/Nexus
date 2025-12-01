package gg.projecteden.nexus.models.minigolf;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.DebugDotCommand;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.GolfBallParticle;
import gg.projecteden.nexus.features.minigolf.models.GolfBallStyle;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.api.common.utils.EnumUtils.valuesExcept;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

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
	private GolfBall golfBall;
	private boolean debug;

	private Map<String, GolfBallStyle> styles = new ConcurrentHashMap<>();
	private Set<GolfBallStyle> unlockedStyles = new HashSet<>();
	private Map<String, Set<GolfBallStyle>> unlockedCourseStyles = new ConcurrentHashMap<>();

	private Map<String, GolfBallParticle> particles = new ConcurrentHashMap<>();
	private Set<GolfBallParticle> unlockedParticles = new HashSet<>();
	private Map<String, Set<GolfBallParticle>> unlockedCourseParticles = new ConcurrentHashMap<>();

	private Map<String, Map<Integer, Integer>> currentScorecard = new ConcurrentHashMap<>();
	private Map<String, Map<Integer, Integer>> bestScorecard = new ConcurrentHashMap<>();

	public Map<Integer, Integer> getCurrentScorecard(MiniGolfCourse course) {
		return currentScorecard.computeIfAbsent(course.getName(), $ -> new ConcurrentHashMap<>());
	}

	public Map<Integer, Integer> getBestScorecard(MiniGolfCourse course) {
		return bestScorecard.computeIfAbsent(course.getName(), $ -> new ConcurrentHashMap<>());
	}

	public GolfBallStyle getStyle() {
		var course = getCurrentCourse();

		if (course == null)
			return GolfBallStyle.WHITE;

		return styles.computeIfAbsent(course.getName(), $ -> GolfBallStyle.WHITE);
	}

	public void setStyle(MiniGolfCourse course, GolfBallStyle style) {
		this.styles.put(course.getName(), style);
		if (golfBall != null)
			golfBall.updateDisplayItem();
	}

	public void unlockStyle(GolfBallStyle golfBallStyle) {
		unlockedStyles.add(golfBallStyle);
	}

	public void unlockStyle(MiniGolfCourse course, GolfBallStyle golfBallStyle) {
		unlockedCourseStyles.computeIfAbsent(course.getName(), $ -> new HashSet<>()).add(golfBallStyle);
	}

	public List<GolfBallStyle> getAvailableStyles(MiniGolfCourse course) {
		var styles = new ArrayList<GolfBallStyle>();

		for (GolfBallStyle style : GolfBallStyle.values()) {
			if (style.isDefault())
				styles.add(style);
			else if (unlockedStyles.contains(style))
				styles.add(style);
			else if (course != null) {
				var courseStyles = unlockedCourseStyles.computeIfAbsent(course.getName(), $ -> new HashSet<>());
				if (courseStyles.contains(style))
					styles.add(style);
			}
		}

		return styles;
	}

	public GolfBallParticle getParticle() {
		var course = getCurrentCourse();

		if (course == null)
			return GolfBallParticle.NONE;

		return particles.computeIfAbsent(course.getName(), $ -> GolfBallParticle.NONE);
	}

	public void setParticle(MiniGolfCourse course, GolfBallParticle particle) {
		this.particles.put(course.getName(), particle);
	}

	public void unlockParticle(GolfBallParticle golfBallParticle) {
		unlockedParticles.add(golfBallParticle);
	}

	public void unlockParticle(MiniGolfCourse course, GolfBallParticle golfBallParticle) {
		unlockedCourseParticles.computeIfAbsent(course.getName(), $ -> new HashSet<>()).add(golfBallParticle);
	}

	public List<GolfBallParticle> getAvailableParticles(MiniGolfCourse course) {
		var particles = new ArrayList<GolfBallParticle>();

		for (GolfBallParticle particle : GolfBallParticle.values()) {
			if (particle.isDefault())
				particles.add(particle);
			else if (unlockedParticles.contains(particle))
				particles.add(particle);
			else if (course != null) {
				var courseParticles = unlockedCourseParticles.computeIfAbsent(course.getName(), $ -> new HashSet<>());
				if (courseParticles.contains(particle))
					particles.add(particle);
			}
		}

		return particles;
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
		removeOtherGolfBallStylesFromInventory();

		for (ItemStack itemStack : MiniGolfUtils.KIT)
			if (!PlayerUtils.playerHas(this, itemStack) && PlayerUtils.hasRoomFor(this, itemStack))
				PlayerUtils.giveItem(getOnlinePlayer(), itemStack);

		giveGolfBall();
	}

	public void takeKit() {
		for (ItemStack content : getOnlinePlayer().getInventory().getContents()) {
			for (var model : MiniGolfUtils.KIT_MODELS)
				if (model.is(content))
					content.setAmount(0);

			for (GolfBallStyle style : GolfBallStyle.values())
				if (style.getModel().is(content))
					content.setAmount(0);
		}
	}

	public void giveGolfBall() {
		removeOtherGolfBallStylesFromInventory();

		var itemStack = MiniGolfUtils.getGolfBall(getStyle());
		if (PlayerUtils.playerHas(this, itemStack))
			return;

		if (!PlayerUtils.hasRoomFor(this, itemStack))
			return;

		PlayerUtils.giveItem(getOnlinePlayer(), itemStack);
	}

	private void removeOtherGolfBallStylesFromInventory() {
		for (ItemStack content : getOnlinePlayer().getInventory().getContents()) {
			if (isNullOrAir(content))
				continue;

			for (GolfBallStyle style : valuesExcept(GolfBallStyle.class, getStyle()))
				if (style.getModel().is(content))
					content.setAmount(0);
		}
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

	public MiniGolfCourse getCurrentCourseRequired() {
		var course = getCurrentCourse();
		if (course == null)
			throw new InvalidInputException("You must be in a MiniGolf course");
		return course;
	}
}
