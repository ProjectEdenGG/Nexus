package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.features.minigames.mechanics.common.CaptureTheFlagMechanic;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.Flag;
import me.pugabyte.nexus.features.minigames.models.matchdata.OneFlagCaptureTheFlagMatchData;
import me.pugabyte.nexus.utils.Tasks;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public class OneFlagCaptureTheFlag extends CaptureTheFlagMechanic {

	@Override
	public String getName() {
		return "One Flag Capture the Flag";
	}

	@Override
	public String getDescription() {
		return "Find the flag and capture it at the other team's base";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.BLUE_BANNER);
	}

	@Override
	protected void onFlagInteract(Minigamer minigamer, Sign sign) {
		Match match = minigamer.getMatch();
		OneFlagCaptureTheFlagMatchData matchData = match.getMatchData();

		if (!minigamer.isPlaying(this)) return;

		if ((ChatColor.GRAY + "Neutral").equalsIgnoreCase(sign.getLine(2))) {
			if (matchData.getFlag() == null)
				matchData.setFlag(new Flag(sign, match));

			takeFlag(minigamer);
		} else if ((ChatColor.GREEN + "Capture").equalsIgnoreCase(sign.getLine(2)))
			if (minigamer.equals(matchData.getFlagCarrier()))
				if (stripColor(sign.getLine(3)).equalsIgnoreCase(minigamer.getTeam().getName()))
					minigamer.tell("&cYou must capture the flag at the other team's base");
				else
					captureFlag(minigamer);
	}

	@Override
	protected void onEnterKillRegion(Minigamer minigamer) {
		OneFlagCaptureTheFlagMatchData matchData = minigamer.getMatch().getMatchData();
		Flag flag = matchData.getFlag();

		if (flag != null) {
			flag.drop(minigamer.getPlayer().getLocation());

			matchData.setFlagCarrier(null);

			Tasks.wait(5, () -> minigamer.getMatch().broadcast(minigamer.getColoredName() + " &3dropped the flag outside the map"));
		}
	}

	@Override
	protected void doFlagParticles(Match match) {
		OneFlagCaptureTheFlagMatchData matchData = match.getMatchData();
		if (matchData.getFlagCarrier() == null)
			return;

		Flag.particle(matchData.getFlagCarrier());
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		Minigamer minigamer = event.getMinigamer();
		OneFlagCaptureTheFlagMatchData matchData = minigamer.getMatch().getMatchData();
		if (minigamer.equals(matchData.getFlagCarrier())) {
			Flag flag = matchData.getFlag();
			if (flag != null) {
				flag.drop(minigamer.getPlayer().getLocation());

				matchData.setFlagCarrier(null);

				event.getMatch().broadcast(minigamer.getColoredName() + " &3dropped the flag");
			}
		}

		super.onDeath(event);
	}

	protected void captureFlag(Minigamer minigamer) {
		Match match = minigamer.getMatch();
		OneFlagCaptureTheFlagMatchData matchData = match.getMatchData();

		match.broadcast(minigamer.getColoredName() + " &3captured the flag");

		minigamer.scored();
		minigamer.getMatch().scored(minigamer.getTeam());

		matchData.setFlagCarrier(null);

		matchData.getFlag().respawn();
		if (shouldBeOver(match))
			match.end();
	}

	protected void takeFlag(Minigamer minigamer) {
		Match match = minigamer.getMatch();
		OneFlagCaptureTheFlagMatchData matchData = match.getMatchData();

		match.broadcast(minigamer.getColoredName() + " &3took the flag");

		matchData.setFlagCarrier(minigamer);

		matchData.getFlag().despawn();

		match.getTasks().cancel(matchData.getFlag().getTaskId());
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);

		OneFlagCaptureTheFlagMatchData matchData = event.getMatch().getMatchData();
		if (matchData.getFlag() != null)
			matchData.getFlag().respawn();
	}

}
