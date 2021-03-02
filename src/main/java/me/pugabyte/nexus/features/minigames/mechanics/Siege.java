package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.matchdata.Flag;
import me.pugabyte.nexus.features.minigames.models.matchdata.OneFlagCaptureTheFlagMatchData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public final class Siege extends OneFlagCaptureTheFlag {

	@Override
	public String getName() {
		return "Siege";
	}

	@Override
	public String getDescription() {
		return "One team protects their flag while the other tries to capture it";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.GREEN_BANNER);
	}

	@Override
	protected void onFlagInteract(Minigamer minigamer, Sign sign) {
		Match match = minigamer.getMatch();
		OneFlagCaptureTheFlagMatchData matchData = match.getMatchData();

		if (!minigamer.isPlaying(this)) return;

		if (matchData.getFlag() == null)
			matchData.setFlag(new Flag(sign, match));

		if ((ChatColor.GREEN + "Capture").equalsIgnoreCase(sign.getLine(2))) {
			if (!minigamer.equals(matchData.getFlagCarrier()))
				return;

			captureFlag(minigamer);
		} else if (!minigamer.getTeam().getName().equalsIgnoreCase(stripColor(sign.getLine(2))))
			takeFlag(minigamer);
		else if (minigamer.getTeam().getName().equalsIgnoreCase(stripColor(sign.getLine(2))) && !matchData.getFlag().getSpawnLocation().equals(sign.getLocation()))
			returnFlag(minigamer);
	}

	private void returnFlag(Minigamer minigamer) {
		Match match = minigamer.getMatch();
		OneFlagCaptureTheFlagMatchData matchData = match.getMatchData();

		match.broadcast(minigamer.getColoredName() + " &3returned the flag");

		Flag flag = matchData.getFlag();
		if (flag != null) {
			flag.respawn();
			match.getTasks().cancel(flag.getTaskId());
		}
	}

}
