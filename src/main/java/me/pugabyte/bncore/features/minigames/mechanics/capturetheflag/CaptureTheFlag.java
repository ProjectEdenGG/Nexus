package me.pugabyte.bncore.features.minigames.mechanics.capturetheflag;

import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.matchdata.CaptureTheFlagMatchData;
import me.pugabyte.bncore.features.minigames.models.matchdata.Flag;
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
		CaptureTheFlagMatchData matchData = (CaptureTheFlagMatchData) match.getMatchData();
		Arena arena = match.getArena();

		if (!minigamer.isPlaying(CaptureTheFlag.class)) return;

		Optional<Team> optionalTeam = arena.getTeams().stream()
				.filter(team -> team.getName().equalsIgnoreCase(sign.getLine(2)))
				.findFirst();

		if (optionalTeam.isPresent()) {
			Team clickedTeam = optionalTeam.get();
			Flag clickedFlag = matchData.getFlag(clickedTeam);
			if (clickedFlag == null) {
				clickedFlag = new Flag(sign.getLocation(), sign.getData(), sign.getBlock().getState(), sign.getLines(), clickedTeam);
				matchData.addFlag(clickedTeam, clickedFlag);
			}

			Flag carriedFlag = matchData.getFlagFromCarrier(minigamer);

			if (clickedTeam == minigamer.getTeam()) {
				if (carriedFlag != null) {
					captureFlag(minigamer, carriedFlag.getTeam());
				}
			} else if (carriedFlag == null) {
				takeFlag(clickedFlag, minigamer);
			}
		}
	}

	private void captureFlag(Minigamer minigamer, Team team) {
		Match match = minigamer.getMatch();
		CaptureTheFlagMatchData matchData = (CaptureTheFlagMatchData) match.getMatchData();

		match.broadcast(minigamer.getTeam().getColor() + minigamer.getPlayer().getName() + " &3captured " +
				team.getName() + "&3's flag");

		minigamer.scored();
		minigamer.getMatch().scored(minigamer.getTeam());

		matchData.removeFlagCarrier(minigamer);

		// TODO: Respawn sign
	}

	private void takeFlag(Flag flag, Minigamer minigamer) {
		Match match = minigamer.getMatch();
		CaptureTheFlagMatchData matchData = (CaptureTheFlagMatchData) match.getMatchData();

		match.broadcast(minigamer.getTeam().getColor() + minigamer.getPlayer().getName() + " &3took " +
				flag.getTeam().getName() + "&3's flag");

		matchData.addFlagCarrier(flag, minigamer);

		// TODO: Despawn sign (client side only?)
	}

	@Override
	public void dropFlag(PlayerDeathEvent event) {

	}

	@Override
	public void onStart(Match match) {
		match.setMatchData(new CaptureTheFlagMatchData(match));
	}

}
