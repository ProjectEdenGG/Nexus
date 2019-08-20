package me.pugabyte.bncore.features.sideways.stairs.models;

import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.entity.Player;

/**
 * @author shannon
 */
public class SidewaysStairsPlayer {
	private final Player player;
	private boolean enabled = false;
	private String action = "";
	private byte angle = 0;

	public SidewaysStairsPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public byte getAngle() {
		return angle;
	}

	public void setAngle(byte angle) {
		this.angle = angle;
	}

	public void trySetAngle(byte angle) {
		if (angle > 7 || angle < 0)
			throw new InvalidInputException("Invalid angle (Must be a number between 0-7)");

		this.angle = angle;
	}

	public int rotate() {
		this.angle = (byte) ((angle + 1) % 8);
		return angle;
	}

}

