package gg.projecteden.nexus.features.minigolf.models;

import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MiniGolfUser implements PlayerOwnedObject {
	@EqualsAndHashCode.Include
	@NonNull
	private UUID uuid;
	@NonNull
	private GolfBallColor golfBallColor;

	private GolfBall golfBall;

	private boolean debug;

	public void debug(boolean bool, String debug) {
		if (bool)
			debug(debug);
	}

	public void debug(String message) {
		if (debug)
			sendMessage(message);
	}

	public void giveKit() {
		PlayerUtils.giveItems(getOnlinePlayer(), MiniGolfUtils.getKit(this.golfBallColor));
	}

	public void setGolfBallColor(GolfBallColor color) {
		this.golfBallColor = color;
		if (golfBall != null)
			golfBall.setColor(color);
	}
}
