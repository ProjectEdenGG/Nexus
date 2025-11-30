package gg.projecteden.nexus.models.minigolf;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.DebugDotCommand;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.GolfBallColor;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.UUID;

@Data
@Entity(value = "minigolf_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MiniGolfUser implements PlayerOwnedObject {
	@Id
	@NonNull
	@EqualsAndHashCode.Include
	private UUID uuid;
	private boolean playing;
	private GolfBallColor golfBallColor = GolfBallColor.WHITE;
	private GolfBall golfBall;
	private boolean debug;

	public void debug(boolean bool, String debug) {
		if (!bool)
			return;

		debug(debug);
	}

	public void debug(String message) {
		if (!debug)
			return;

		sendMessage(message);
	}

	public void debugDot(Location location, ColorType color) {
		if (!debug)
			return;

		DebugDotCommand.play(getPlayer(), location.clone(), color, TickTime.SECOND.x(1));
	}

	public void giveKit() {
		PlayerUtils.giveItems(getOnlinePlayer(), MiniGolfUtils.getKit(this.golfBallColor));
	}

	public void setGolfBallColor(GolfBallColor color) {
		this.golfBallColor = color;
		if (golfBall != null)
			golfBall.updateDisplayItem();
	}

	public boolean canHitBall() {
		return this.isOnline()
			&& golfBall != null
			&& !golfBall.isActive()
			&& golfBall.isAlive()
			&& golfBall.isMinVelocity()
			&& MiniGolfUtils.isClub(ItemUtils.getTool(getOnlinePlayer()));
	}
}
