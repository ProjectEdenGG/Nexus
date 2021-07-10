package me.pugabyte.nexus.features.minigolf.models;

import de.tr7zw.nbtapi.NBTEntity;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.nexus.features.minigolf.MiniGolf;
import me.pugabyte.nexus.features.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.minigolf.models.blocks.ModifierBlockType;
import me.pugabyte.nexus.utils.SoundBuilder;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
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

	public GolfBall(@NotNull UUID userUuid) {
		this.userUuid = userUuid;
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

		return snowball.getVelocity();
	}

	public void setVelocity(Vector vector) {
		if (!isAlive())
			return;

		snowball.setVelocity(vector);
	}

	public Location getLocation() {
		if (!isAlive())
			return null;

		return snowball.getLocation();
	}

	public void setGravity(boolean bool) {
		if (!isAlive())
			return;

		snowball.setGravity(bool);
	}

	public void setShooter() {
		if (!isAlive())
			return;

		snowball.setShooter(getPlayer());
	}

	public Player getShooter() {
		if (!isAlive())
			return null;

		ProjectileSource source = snowball.getShooter();
		if (!(source instanceof Player player))
			return null;

		return player;
	}

	public void teleport(Location location) {
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

		snowball.teleport(location);
	}

	public boolean isAlive() {
		if (snowball == null) {
			debug("snowball is null");
			return false;
		}

		if (!snowball.isValid()) {
			debug("snowball is not valid");
			return false;
		}

		return true;
	}

	public Block getBlockBelow() {
		if (!isAlive())
			return null;

		return snowball.getLocation().subtract(0, 0.1, 0).getBlock();
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

	public void respawn() {
		debug("respawning ball...");

		if (!isAlive())
			return;

		snowball.setVelocity(new Vector(0, 0, 0));
		snowball.setGravity(false);
		snowball.teleport(lastLocation.add(0, MiniGolf.getFloorOffset(), 0));
		snowball.setFireTicks(0);
		snowball.setTicksLived(1);

		MiniGolfUtils.sendActionBar(getUser(), "&cOut of bounds!");
		new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BASS).receiver(getPlayer()).pitchStep(0).play();
	}

	public void remove() {
		debug("removing ball...");

		if (!isAlive())
			return;

		snowball.remove();
		snowball = null;
	}

	public boolean isInBounds() {
		if (!isAlive()) {
			debug("is not inbounds: ball is not alive");
			return false;
		}

		if (isMinVelocity()) {
			Material material = getBlockBelow().getType();
			if (ModifierBlockType.DEATH.getModifierBlock().getMaterials().contains(material)) {
				debug("is not inbounds: ball is on death modifier block");
				return false;
			}
		}

		boolean isInRegion = new WorldGuardUtils(snowball).isInRegion(getLocation(), holeRegion);
		if (!isInRegion) {
			debug("is not inbounds: ball is not in region");
			return false;
		}

		debug("ball is in bounds");
		return true;
	}

	public void debug(String message) {
		getUser().debug(message);
	}

	public void spawn(Location location) {
		debug("spawning ball...");
		this.lastLocation = location.toBlockLocation().add(0.5, 1 + MiniGolf.getFloorOffset(), 0.5);

		this.snowball = (Snowball) lastLocation.getWorld().spawnEntity(lastLocation, EntityType.SNOWBALL);
		setGravity(false);
		setShooter();
		applyDisplayItem();

		getUser().setGolfBall(this);

		this.snowball.setCustomName(MiniGolfUtils.getStrokeString(getUser()));
		this.snowball.setCustomNameVisible(true);

		debug("Snowball: " + new NBTEntity(snowball).asNBTString());
	}

	public void applyDisplayItem() {
		if (displayItem != null)
			this.snowball.setItem(displayItem);
	}
}
