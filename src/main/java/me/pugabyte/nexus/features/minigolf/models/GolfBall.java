package me.pugabyte.nexus.features.minigolf.models;

import lombok.Data;
import me.pugabyte.nexus.features.minigolf.MiniGolf;
import me.pugabyte.nexus.features.minigolf.models.blocks.ModifierBlockType;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Data
public class GolfBall {
	private MiniGolfUser user;
	private Snowball ball;
	private Location lastLocation;
	private ItemStack displayItem;
	private String holeRegion;
	private int strokes = 0;
	private int par = 0;

	public Vector getVelocity() {
		return ball.getVelocity();
	}

	public void setVelocity(Vector vector) {
		ball.setVelocity(vector);
	}

	public boolean isAlive() {
		return ball != null && !ball.isValid();
	}

	public Block getBlockBelow() {
		return ball.getLocation().subtract(0, 0.1, 0).getBlock();
	}

	public boolean isNotMaxVelocity() {
		return getVelocity().length() >= MiniGolf.getMaxVelocity();
	}

	public boolean isMinVelocity() {
		return getVelocity().getY() >= 0.0 && getVelocity().length() <= MiniGolf.getMinVelocity();
	}

	public void setGravity(boolean bool) {
		getBall().setGravity(bool);
	}

	public void teleport(Location location) {
		getBall().teleport(location);
	}

	public Location getLocation() {
		return getBall().getLocation();
	}

	public boolean isInBounds() {
		if (isMinVelocity()) {
			Material material = getBlockBelow().getType();
			if (!ModifierBlockType.DEATH.getModifierBlock().getMaterials().contains(material))
				return false;
		}

		return new WorldGuardUtils(ball).isInRegion(getLocation(), holeRegion);
	}
}
