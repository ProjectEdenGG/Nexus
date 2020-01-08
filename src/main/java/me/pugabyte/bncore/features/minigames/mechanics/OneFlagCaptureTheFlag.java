package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.mechanics.common.CaptureTheFlagMechanic;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

public final class OneFlagCaptureTheFlag extends CaptureTheFlagMechanic {

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public void onFlagInteract(Minigamer minigamer, Sign sign) {
		Match match = minigamer.getMatch();
		Arena arena = match.getArena();
		Mechanic mechanic = arena.getMechanic();

		if (!minigamer.isPlaying(OneFlagCaptureTheFlag.class)) return;

		if ((ChatColor.GRAY + "Neutral").equalsIgnoreCase(sign.getLine(2))) {
			// TODO: Taking neutral flag
		} else if ((ChatColor.GREEN + "Capture").equalsIgnoreCase(sign.getLine(2))) {
			// TODO: Capturing neutral flag
		}
	}

}
