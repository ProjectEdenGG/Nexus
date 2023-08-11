package gg.projecteden.nexus.features.minigolf.models;

import gg.projecteden.nexus.features.minigolf.MiniGolf;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlockType;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
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

import java.util.UUID;

@Data
public class GolfBall {
	@NonNull
	private UUID userUuid;
	private Snowball snowball;
	private Location lastLocation;
	private ItemStack displayItem;
	private String holeRegion;
	private int strokes = 0;
	private int par = 0;

	public GolfBall(@NotNull UUID userUuid, GolfBallColor color) {
		this.userUuid = userUuid;
		this.displayItem = MiniGolfUtils.getGolfBall(color);
	}

	public Player getPlayer() {
		return MiniGolfUtils.getUser(userUuid).getPlayer();
	}

	public MiniGolfUser getUser() {
		return MiniGolfUtils.getUser(userUuid);
	}

	public Vector getVelocity() {
		if (!isAlive())
			return null;

		return this.snowball.getVelocity();
	}

	public void setVelocity(Vector vector) {
		if (!isAlive())
			return;

		this.snowball.setVelocity(vector);
	}

	public void setTicksLived(int ticks) {
		if (!isAlive())
			return;

		this.snowball.setTicksLived(ticks);
	}

	public void setName(String name) {
		this.snowball.setCustomName(name);
		this.snowball.setCustomNameVisible(true);
	}

	public Location getLocation() {
		if (!isAlive())
			return null;

		return this.snowball.getLocation();
	}

	public void setGravity(boolean bool) {
		if (!isAlive())
			return;

		this.snowball.setGravity(bool);
	}

	public void setShooter(Player shooter) {
		if (!isAlive())
			return;

		this.snowball.setShooter(getPlayer());
	}

	public Player getShooter() {
		if (!isAlive())
			return null;

		ProjectileSource source = this.snowball.getShooter();
		if (!(source instanceof Player player))
			return null;

		return player;
	}

	public void teleportAsync(Location location) {
		if (!isAlive())
			return;

		// TODO: test with this some more, it actually seemed to fix some issues
//		Block block = location.getBlock();
//		if(!BlockUtils.isNullOrAir(block)){
//			if(MiniGolfUtils.isBottomSlab(block))
//				if(block.getLocation().getY() + 0.5 + MiniGolf.getFloorOffset() != location.getY())
//					location.add(0, 0.5, 0);
//			else if(MiniGolfUtils.isTopSlab(block))
//				if(block.getLocation().getY() + MiniGolf.getFloorOffset() != location.getY())
//					location.subtract(0, 0.5, 0);
//		}

		this.snowball.teleportAsync(location);
	}

	public boolean isAlive() {
		if (this.snowball == null) {
			debug("snowball is null");
			return false;
		}

		if (!this.snowball.isValid()) {
			debug("snowball is not valid");
			return false;
		}

		return true;
	}

	public Block getBlockBelow() {
		if (!isAlive())
			return null;

		return this.snowball.getLocation().subtract(0, 0.1, 0).getBlock();
	}

	public boolean isNotMaxVelocity() {
		if (!isAlive())
			return false;

		return getVelocity().length() < MiniGolf.getMaxVelocity();
	}

	public boolean isMinVelocity() {
		if (!isAlive())
			return false;

		return getVelocity().getY() >= 0.0 && getVelocity().length() <= MiniGolf.getMinVelocity();
	}

	public void incStrokes() {
		this.strokes += 1;
	}

	public void recall() {
		debug("recalling ball...");
		if (!isAlive())
			return;

		respawnBall();

		MiniGolfUtils.sendActionBar(getUser(), "&eReset ball");
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_CHIME).location(getPlayer()).volume(0.9).pitch(1.9).play();
	}

	public void respawn() {
		debug("respawning ball...");
		if (!isAlive())
			return;

		respawnBall();

		MiniGolfUtils.sendActionBar(getUser(), "&cOut of bounds!");
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BASS).receiver(getPlayer()).pitchStep(0).play();
	}

	private void respawnBall() {
		this.snowball.setVelocity(new Vector(0, 0, 0));
		this.snowball.setGravity(false);
		this.snowball.teleportAsync(lastLocation.add(0, MiniGolf.getFloorOffset(), 0));
		this.snowball.setFireTicks(0);
		this.snowball.setTicksLived(1);
	}

	public void pickup() {
		debug("picking up ball...");

		remove();
		MiniGolfUtils.giveBall(getUser());
	}

	public void remove() {
		debug("removing ball...");

		if (!isAlive())
			return;

		this.snowball.remove();
		this.snowball = null;
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

		boolean isInRegion = new WorldGuardUtils(this.snowball).isInRegion(getLocation(), holeRegion);
		if (!isInRegion) {
			debug("is not inbounds: ball is not in region");
			return false;
		}

		if (!isNotMoving)
			debug("ball is in bounds");

		return true;
	}

	public void debug(String message) {
		getUser().debug(message);
	}

	public void spawn(Location location) {
		debug("spawning ball...");
		this.lastLocation = location.toBlockLocation().add(0.5, 1 + MiniGolf.getFloorOffset(), 0.5);

		this.snowball = (Snowball) lastLocation.getWorld().spawnEntity(lastLocation, EntityType.SNOWBALL, CreatureSpawnEvent.SpawnReason.CUSTOM, _entity -> ((Snowball) _entity).setItem(getDisplayItem()));
		setGravity(false);
		setShooter(this.getShooter());
		applyDisplayItem();

		getUser().setGolfBall(this);

		setName(MiniGolfUtils.getStrokeString(getUser()));

		//debug("Snowball: " + new NBTEntity(this.snowball).asNBTString());
	}

	public void setColor(GolfBallColor color) {
		this.displayItem = MiniGolfUtils.getGolfBall(color);
		applyDisplayItem();
	}

	public void applyDisplayItem() {
		if (!isAlive())
			return;

		if (displayItem != null) {
			this.snowball.setItem(displayItem);
		}
	}
}
