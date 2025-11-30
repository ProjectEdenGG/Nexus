package gg.projecteden.nexus.models.minigolf;

import dev.morphia.annotations.Converters;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.minigolf.MiniGolf;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlockType;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfBallDeathEvent;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfBallDeathEvent.DeathCause;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse.MiniGolfHole;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Data
@NoArgsConstructor
@Converters(UUIDConverter.class)
public class GolfBall implements PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	private Snowball snowball;
	private Location lastLocation;
	private String courseId;
	private Integer holeId;
	private int strokes = 0;
	private boolean active = false;

	public GolfBall(@NotNull UUID uuid) {
		this.uuid = uuid;
	}

	public MiniGolfUser getUser() {
		return new MiniGolfUserService().get(uuid);
	}

	public MiniGolfCourse getCourse() {
		return new MiniGolfConfigService().get0().getCourse(courseId);
	}

	public MiniGolfHole getHole() {
		return getCourse().getHole(holeId);
	}

	public Vector getVelocity() {
		if (!isAlive()) return null;

		return this.snowball.getVelocity();
	}

	public void setVelocity(Vector vector) {
		if (!isAlive()) return;

		this.snowball.setVelocity(vector);
	}

	public @Nullable Location getLastLocation() {
		if (this.lastLocation == null)
			return null;

		return this.lastLocation.clone();
	}

	public void setLastLocation(Location location) {
		this.lastLocation = location;
		debug("lastLoc: " + StringUtils.xyzDecimal(this.lastLocation, 2));
	}

	public void setTicksLived(int ticks) {
		if (!isAlive()) return;

		this.snowball.setTicksLived(ticks);
	}

	public void setName(String name) {
		this.snowball.setCustomName(name);
		this.snowball.setCustomNameVisible(true);
	}

	public Location getBallLocation() {
		if (!isAlive()) return null;

		return this.snowball.getLocation();
	}

	public void setGravity(boolean bool) {
		if (!isAlive()) return;

		this.snowball.setGravity(bool);
	}

	public void setShooter(Player shooter) {
		if (!isAlive())
			return;

		this.snowball.setShooter(getPlayer());
	}

	public Player getShooter() {
		if (!isAlive()) return null;

		ProjectileSource source = this.snowball.getShooter();
		if (!(source instanceof Player player))
			return null;

		return player;
	}

	public void teleport(Location location) {
		if (!isAlive())
			return;

		this.snowball.teleport(location);
	}

	public boolean isAlive() {
		return this.snowball != null && this.snowball.isValid();
	}

	public Block getBlock() {
		if (!isAlive()) return null;

		return this.snowball.getLocation().getBlock();
	}

	public Block getBlockBelow() {
		if (!isAlive()) return null;

		Location location = getBallLocation().clone();
		location.setY(this.snowball.getLocation().getY() - 0.25);
		return location.getBlock();
	}

	public boolean isNotMaxVelocity() {
		if (!isAlive()) return false;

		return getVelocity().length() < MiniGolf.MAX_VELOCITY;
	}

	public boolean isMinVelocity() {
		if (!isAlive()) return false;

		return getVelocity().getY() >= 0.0 && getVelocity().length() <= MiniGolf.MIN_VELOCITY;
	}

	public void incStrokes() {
		this.strokes += 1;
	}

	public void reset() {
		if (!isAlive()) return;
		debug("resetting ball...");

		// Death Event
		MiniGolfBallDeathEvent ballDeathEvent = new MiniGolfBallDeathEvent(this, DeathCause.RECALLED);
		if (!ballDeathEvent.callEvent()) {
			debug("death event cancelled");
			return;
		}

		respawnBall();

		MiniGolfUtils.sendActionBar(getUser(), "&eReset ball");
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_CHIME).location(getPlayer()).volume(0.9).pitch(1.9).play();
	}

	public void respawn(DeathCause deathCause) {
		respawn("&cOut of bounds!", deathCause);
	}

	public void respawn(String reason, DeathCause deathCause) {
		if (!isAlive()) return;

		// Death Event
		MiniGolfBallDeathEvent ballDeathEvent = new MiniGolfBallDeathEvent(this, deathCause);
		if (!ballDeathEvent.callEvent()) {
			debug("death event cancelled");
			return;
		}

		debug("respawning ball...");

		respawnBall();

		MiniGolfUtils.sendActionBar(getUser(), reason);
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BASS).receiver(getPlayer()).pitchStep(0).play();
	}

	private void respawnBall() {
		this.snowball.setVelocity(new Vector(0, 0, 0));
		this.snowball.setGravity(false);
		this.snowball.teleport(lastLocation);
		this.snowball.setFireTicks(0);
		this.snowball.setTicksLived(1);
		this.active = false;
	}

	public void pickup() {
		debug("picking up ball...");

		remove();
		MiniGolfUtils.giveBall(getUser());
	}

	public void remove() {
		debug("removing ball...");

		if (this.snowball != null) {
			this.snowball.remove();
			this.snowball = null;
		}

		this.active = false;
	}

	public boolean isInBounds() {
		if (!isAlive()) {
			debug("is not inbounds: ball is not alive");
			return false;
		}

		boolean isNotMoving = isMinVelocity();

		if (isNotMoving) {
			Material material = getBlockBelow().getType();
			if (ModifierBlockType.DEATH.getModifierBlock().getMaterials().contains(material)) {
				debug("is not inbounds: ball is on death modifier block");
				return false;
			}
		}

		boolean isInRegion = new WorldGuardUtils(this.snowball).isInRegionLikeAt(getHoleRegionRegex(), getBallLocation());
		if (!isInRegion) {
			debug("is not inbounds: ball is not in region");
			return false;
		}

		if (!isNotMoving)
			debug("ball is in bounds");

		return true;
	}

	private @NotNull String getHoleRegionRegex() {
		return courseId.toLowerCase() + "_minigolf_hole_" + holeId + "(_[0-9]+)?";
	}

	public void debug(String message) {
		if (getUser() != null)
			getUser().debug(message);
	}

	public void spawn(Location location) {
		debug("spawning ball...");
		this.active = false;
		setLastLocation(location.toBlockLocation().add(0.5, 1 + MiniGolf.FLOOR_OFFSET, 0.5));

		this.snowball = (Snowball) lastLocation.getWorld().spawnEntity(lastLocation, EntityType.SNOWBALL, CreatureSpawnEvent.SpawnReason.CUSTOM, _entity -> ((Snowball) _entity).setItem(getDisplayItem()));
		this.setGravity(false);
		this.setShooter(this.getShooter());

		getUser().setGolfBall(this);

		this.setName(MiniGolfUtils.getStrokeString(getUser())); // must be after the set to user
	}

	public ItemStack getDisplayItem() {
		return MiniGolfUtils.getGolfBall(getUser().getStyle());
	}

	public void updateDisplayItem() {
		if (this.snowball == null)
			return;

		this.snowball.setItem(getDisplayItem());
	}

	public void updateScorecards(MiniGolfCourse course, int strokes) {
		updateCurrentScorecard(course, strokes);
		updateBestScorecard(course, strokes);
	}

	public void updateCurrentScorecard(MiniGolfCourse course, int strokes) {
		var user = getUser();
		var currentScorecard = user.getCurrentScorecard(course);
		if (!currentScorecard.containsKey(holeId))
			currentScorecard.put(holeId, strokes);
	}

	public void updateBestScorecard(MiniGolfCourse course, int strokes) {
		var user = getUser();
		var bestScorecard = user.getBestScorecard(course);
		if (!bestScorecard.containsKey(holeId))
			bestScorecard.put(holeId, strokes);
		else
			if (bestScorecard.get(holeId) > strokes)
				bestScorecard.put(holeId, strokes);
	}
}
