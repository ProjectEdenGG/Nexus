package gg.projecteden.nexus.features.minigames.models.exceptions;

public class YourTurnIsOverException extends MinigameException {
	public YourTurnIsOverException() {
		super("Your turn is already over");
	}
}
