package me.pugabyte.bncore.features.connect4.models;

import org.bukkit.ChatColor;

public enum Connect4Team {
	RED(0, ChatColor.RED),
	BLUE(1, ChatColor.BLUE);

	private int id;
	private ChatColor color;

	Connect4Team(int id, ChatColor color) {
		this.id = id;
		this.color = color;
	}

	public int getId() {
		return id;
	}

	public ChatColor getColor() {
		return color;
	}
}
