package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.features.minigames.mechanics.common.CaptureTheFlagMechanic;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.CaptureTheFlagMatchData;
import me.pugabyte.nexus.features.minigames.models.matchdata.Flag;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public final class CaptureTheFlag extends CaptureTheFlagMechanic {
	@Override
	public String getName() {
		return "Capture the Flag";
	}

	@Override
	public String getDescription() {
		return "Capture the other team's flag to win the game";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.RED_BANNER);
	}

	@Override
	public void onFlagInteract(Minigamer minigamer, Sign sign) {
		Match match = minigamer.getMatch();
		CaptureTheFlagMatchData matchData = match.getMatchData();
		Arena arena = match.getArena();

		Optional<Team> optionalTeam = arena.getTeams().stream()
				.filter(team -> team.getName().equalsIgnoreCase(stripColor(sign.getLine(2))))
				.findFirst();

		if (optionalTeam.isPresent()) {
			Team clickedTeam = optionalTeam.get();
			Flag clickedFlag = matchData.getFlag(clickedTeam);

			if (clickedFlag == null) {
				clickedFlag = new Flag(sign, match, clickedTeam);
				matchData.addFlag(clickedTeam, clickedFlag);
			}

			Flag carriedFlag = matchData.getFlagByCarrier(minigamer);

			if (clickedTeam == minigamer.getTeam()) {
				if (carriedFlag != null) {
					if (clickedFlag.getCurrentLocation() == null)
						captureFlag(minigamer, carriedFlag.getTeam());
				} else if (clickedFlag.getCurrentLocation() != null)
					returnFlag(minigamer);
			} else if (carriedFlag == null)
				takeFlag(clickedFlag, minigamer);
		}
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		Minigamer minigamer = event.getMinigamer();
		CaptureTheFlagMatchData matchData = minigamer.getMatch().getMatchData();
		Flag carriedFlag = matchData.getFlagByCarrier(minigamer);
		if (carriedFlag != null) {
			carriedFlag.drop(minigamer.getPlayer().getLocation());

			matchData.removeFlagCarrier(minigamer);

			event.getMatch().broadcast(minigamer.getColoredName() + " &3dropped " + carriedFlag.getTeam().getColoredName() + "&3's flag");
		}
		super.onDeath(event);
	}

	private void returnFlag(Minigamer minigamer) {
		Match match = minigamer.getMatch();
		CaptureTheFlagMatchData matchData = match.getMatchData();

		match.broadcast(minigamer.getColoredName() + " &3returned " + minigamer.getTeam().getColoredName() + "&3's flag");

		Flag flag = matchData.getFlag(minigamer.getTeam());
		flag.respawn();
		match.getTasks().cancel(flag.getTaskId());
	}

	private void captureFlag(Minigamer minigamer, Team team) {
		Match match = minigamer.getMatch();
		CaptureTheFlagMatchData matchData = match.getMatchData();

		match.broadcast(minigamer.getColoredName() + " &3captured " + team.getColoredName() + "&3's flag");

		minigamer.scored();
		minigamer.getMatch().scored(minigamer.getTeam());

		matchData.removeFlagCarrier(minigamer);

		Flag flag = matchData.getFlag(team);
		flag.respawn();
		if (shouldBeOver(match))
			match.end();
	}

	private void takeFlag(Flag flag, Minigamer minigamer) {
		Match match = minigamer.getMatch();
		CaptureTheFlagMatchData matchData = match.getMatchData();

		match.broadcast(minigamer.getColoredName() + " &3took " + flag.getTeam().getColoredName() + "&3's flag");

		matchData.addFlagCarrier(flag, minigamer);

		flag.despawn();

		match.getTasks().cancel(flag.getTaskId());
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);

		CaptureTheFlagMatchData matchData = event.getMatch().getMatchData();
		Map<Team, Flag> flags = matchData.getFlags();
		for (Map.Entry<Team, Flag> flagEntry : flags.entrySet())
			flagEntry.getValue().respawn();
	}

}
