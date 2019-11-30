package me.pugabyte.bncore.features.rainbowarmour.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class RainbowArmourPlayer {
	private final Player player;
	private int taskId;
	private boolean enabled = false;
	@Getter
	@Setter
	private int r = 255;
	@Getter
	@Setter
	private int g, b = 0;

	public RainbowArmourPlayer(Player player, int taskId) {
		this.player = player;
		this.taskId = taskId;
		this.enabled = taskId > 0;
	}

	public Player getPlayer() {
		return player;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskID(int taskId) {
		this.taskId = taskId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setRGB(Color color) {
		r = color.getRed();
		g = color.getGreen();
		b = color.getBlue();
	}
}
