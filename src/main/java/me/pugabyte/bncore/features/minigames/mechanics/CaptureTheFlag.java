package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.matchdata.CaptureTheFlagMatchData;
import me.pugabyte.bncore.features.minigames.models.matchdata.Flag;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.mechanics.CaptureTheFlagMechanic;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

import java.util.Map;
import java.util.Optional;

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
	public void onFlagInteract(Minigamer minigamer, Sign sign) {
		Match match = minigamer.getMatch();
		CaptureTheFlagMatchData matchData = (CaptureTheFlagMatchData) match.getMatchData();
		Arena arena = match.getArena();

		Optional<Team> optionalTeam = arena.getTeams().stream()
				.filter(team -> team.getName().equalsIgnoreCase(ChatColor.stripColor(sign.getLine(2))))
				.findFirst();

		if (optionalTeam.isPresent()) {
			Team clickedTeam = optionalTeam.get();
			Flag clickedFlag = matchData.getFlag(clickedTeam);

			if (clickedFlag == null) {
				clickedFlag = new Flag(sign.getLocation(), sign.getType(), /* 1.13 sign.getBlockData(),*/ sign.getLines(), clickedTeam);
				matchData.addFlag(clickedTeam, clickedFlag);
			}

			Flag carriedFlag = matchData.getFlagByCarrier(minigamer);

			if (clickedTeam == minigamer.getTeam()) {
				if (carriedFlag != null) {
					if (clickedFlag.getCurrentLocation() == null) {
						captureFlag(minigamer, carriedFlag.getTeam());
					}
				} else if (clickedFlag.getCurrentLocation() != null) {
					returnFlag(minigamer);
				}
			} else if (carriedFlag == null) {
				takeFlag(clickedFlag, minigamer);
			}
		}
	}

	private void returnFlag(Minigamer minigamer) {
		Match match = minigamer.getMatch();
		CaptureTheFlagMatchData matchData = (CaptureTheFlagMatchData) match.getMatchData();

		match.broadcast(minigamer.getTeam().getColor() + minigamer.getPlayer().getName() + " &3returned " +
				minigamer.getTeam().getColor() + minigamer.getTeam().getName() + "&3's flag");

		Flag flag = matchData.getFlag(minigamer.getTeam());
		flag.respawn();
	}

	private void captureFlag(Minigamer minigamer, Team team) {
		Match match = minigamer.getMatch();
		CaptureTheFlagMatchData matchData = (CaptureTheFlagMatchData) match.getMatchData();

		match.broadcast(minigamer.getTeam().getColor() + minigamer.getPlayer().getName() + " &3captured " +
				team.getColor() + team.getName() + "&3's flag");

		minigamer.scored();
		minigamer.getMatch().scored(minigamer.getTeam());

		matchData.removeFlagCarrier(minigamer);

		Flag flag = matchData.getFlag(team);
		flag.respawn();
	}

	private void takeFlag(Flag flag, Minigamer minigamer) {
		Match match = minigamer.getMatch();
		CaptureTheFlagMatchData matchData = (CaptureTheFlagMatchData) match.getMatchData();

		match.broadcast(minigamer.getTeam().getColor() + minigamer.getPlayer().getName() + " &3took " +
				flag.getTeam().getColor() + flag.getTeam().getName() + "&3's flag");

		matchData.addFlagCarrier(flag, minigamer);

		flag.despawn();
	}

	@Override
	public void onStart(Match match) {
		match.setMatchData(new CaptureTheFlagMatchData(match));
	}

	@Override
	public void onEnd(Match match) {
		super.onEnd(match);

		CaptureTheFlagMatchData matchData = (CaptureTheFlagMatchData) match.getMatchData();
		Map<Team, Flag> flags = matchData.getFlags();
		for (Map.Entry<Team, Flag> flagEntry : flags.entrySet()) {
			flagEntry.getValue().respawn();
		}
	}

}
