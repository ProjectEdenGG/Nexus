package me.pugabyte.bncore.features.tameables.models;

import org.bukkit.entity.Player;

public enum TameablesAction {
	TRANSFER,
	UNTAME,
	INFO;

	private Player player;

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
