package gg.projecteden.nexus.features.minigames.models.exceptions;

public class NotYourTurnException extends MinigameException {
	public NotYourTurnException() {
		super("Please wait until your turn");
	}
}
