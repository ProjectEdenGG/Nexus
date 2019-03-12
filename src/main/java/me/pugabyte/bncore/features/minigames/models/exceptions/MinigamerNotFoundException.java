package me.pugabyte.bncore.features.minigames.models.exceptions;

public class MinigamerNotFoundException extends MinigameException {
	public MinigamerNotFoundException() {
		super("Player not found");
	}
}
