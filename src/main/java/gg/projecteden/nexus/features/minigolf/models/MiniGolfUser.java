package gg.projecteden.nexus.features.minigolf.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.bukkit.Location;

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

		DotEffect.debug(getPlayer(), location.clone(), color.getBukkitColor(), TickTime.SECOND.x(1));
	}

	public void giveKit() {
		PlayerUtils.giveItems(getOnlinePlayer(), MiniGolfUtils.getKit(this.golfBallColor));
	}

	public void setGolfBallColor(GolfBallColor color) {
		this.golfBallColor = color;
		if (golfBall != null)
			golfBall.setColor(color);
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
