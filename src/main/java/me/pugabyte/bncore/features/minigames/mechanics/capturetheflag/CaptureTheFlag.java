package me.pugabyte.bncore.features.minigames.mechanics.capturetheflag;

import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.matchdata.CaptureTheFlagMatchData;
import me.pugabyte.bncore.features.minigames.models.matchdata.Flag;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.entity.PlayerDeathEvent;

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
		Arena arena = match.getArena();

		if (!minigamer.isPlaying(CaptureTheFlag.class)) return;

		Optional<Team> optionalTeam = arena.getTeams().stream()
				.filter(team -> team.getName().equalsIgnoreCase(sign.getLine(2)))
				.findFirst();

		if (optionalTeam.isPresent()) {
			Team team = optionalTeam.get();
			CaptureTheFlagMatchData matchData = (CaptureTheFlagMatchData) match.getMatchData();
			Flag flag = null;
			if (matchData.getFlag(team) == null) {
				flag = new Flag(sign.getLocation(), sign.getData(), sign.getBlock().getState(), sign.getLines());
			}

			if (minigamer.getTeam() == team) {
				if (flag != null && minigamer.equals(flag.getCarrier())) {
//					Team otherTeam = matchData.getFlagCarriers().get(minigamer);
//					captureFlag(minigamer, otherTeam);
				}
			} else {
				takeFlag(minigamer, team);
			}
			matchData.addFlag(team, flag);
		}
	}

	private void captureFlag(Minigamer minigamer, Team team) {
		Match match = minigamer.getMatch();
		CaptureTheFlagMatchData matchData = (CaptureTheFlagMatchData) match.getMatchData();

		minigamer.tell("You captured " + team.getName() + "'s " + ChatColor.DARK_AQUA + "flag");

		minigamer.scored();
		minigamer.getMatch().scored(minigamer.getTeam());

//		matchData.removeFlagCarrier(minigamer);

		// TODO: Respawn sign
	}

	private void takeFlag(Minigamer minigamer, Team team) {
		Match match = minigamer.getMatch();
		CaptureTheFlagMatchData matchData = (CaptureTheFlagMatchData) match.getMatchData();

		minigamer.tell("You took " + team.getName() + "'s " + ChatColor.DARK_AQUA + "flag");

//		matchData.addFlagCarrier(minigamer, team);


	}

	@Override
	public void dropFlag(PlayerDeathEvent event) {

	}

	@Override
	public void onStart(Match match) {
		match.setMatchData(new CaptureTheFlagMatchData(match));
	}

}
