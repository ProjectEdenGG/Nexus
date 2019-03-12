package me.pugabyte.bncore.features.sideways.stairs.models;

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

	public boolean trySetAngle(String angleString) {
		try {
			byte angle = Byte.valueOf(angleString);
			if (angle > 7 || angle < 0) {
				this.angle = 0;
				return false;
			} else {
				this.angle = angle;
				return true;
			}
		} catch (Exception ex) {
			player.sendMessage(ex.toString());
			return false;
		}
	}

	public int rotate() {
		this.angle = (byte) ((angle + 1) % 8);
		return angle;
	}

}

