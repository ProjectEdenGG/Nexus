package me.pugabyte.bncore.features.tameables.models;

import org.bukkit.OfflinePlayer;

public enum TameablesAction {
	TRANSFER,
	UNTAME,
	INFO;

	private OfflinePlayer player;

	public TameablesAction withPlayer(OfflinePlayer player) {
		this.player = player;
		return this;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}

}
