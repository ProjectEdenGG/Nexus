package gg.projecteden.nexus.features.minigolf.models;

import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class MiniGolfUser implements PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	private boolean debug;
	private GolfBall golfBall;

	public void debug(boolean bool, String debug) {
		if (bool)
			debug(debug);
	}

	public void debug(String message) {
		if (debug)
			sendMessage(message);
	}

}
