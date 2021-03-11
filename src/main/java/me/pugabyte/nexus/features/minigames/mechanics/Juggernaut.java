package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Loadout;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.matchdata.JuggernautMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Juggernaut extends TeamMechanic {
	private static final String TEAM_NAME = "Juggernaut";

	@Override
	public String getName() {
		return "Juggernaut";
	}

	@Override
	public String getDescription() {
		return "Kill as many players as you can as the Juggernaut, or kill the Juggernaut to become it!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.NETHERITE_HELMET); // kinda looks like halo's icon idk
	}

	@Override
	public boolean useAlternativeRegen() {
		return true;
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		Minigamer victim = event.getMinigamer();
		Minigamer attacker = event.getAttacker();
		JuggernautMatchData matchData = event.getMatch().getMatchData();
		if (attacker != null) {
			if (attacker.getTeam().getName().equals(TEAM_NAME)) {
				// attacker was the Juggernaut, give them a point
				attacker.scored();
				super.onDeath(event);
				return;
			}
			// if neither the attacker nor the victim are the juggernaut, we don't care
			if (!victim.getTeam().getName().equals(TEAM_NAME)) {
				super.onDeath(event);
				return;
			}
		}
		else if (matchData.getLastAttacker() != null) {
			attacker = matchData.getLastAttacker();
		}
		else {
			List<Minigamer> minigamers = new ArrayList<>(event.getMatch().getMinigamers()); // copy bc i think i'd modify the original list otherwise?
			Collections.shuffle(minigamers);
			Optional<Minigamer> minigamer = minigamers.stream().filter(minigamer1 -> !minigamer1.getTeam().getName().equals(TEAM_NAME)).findFirst();
			if (!minigamer.isPresent()) {
				criticalErrorAbort("Couldn't find a non-juggernaut player!", victim.getMatch());
				return;
			}
			attacker = minigamer.get();
		}

		matchData.setLastAttacker(null);
		super.onDeath(event);
		// set teams
		Team juggernautTeam = victim.getTeam();
		Team humanTeam = attacker.getTeam();
		victim.setTeam(humanTeam);
		attacker.setTeam(juggernautTeam);

		// apply loadout & reset health
		Loadout juggernautLoadout = juggernautTeam.getLoadout();
		if (juggernautLoadout != null) {
			juggernautLoadout.apply(attacker);
			Player player = attacker.getPlayer();
			player.setHealth(20d);
		}

		victim.getMatch().broadcast(attacker.getColoredName() + "&e has become the Juggernaut!");
		victim.scored();
	}

	@Override
	public void onDamage(MinigamerDamageEvent event) {
		if (event.getAttacker() != null && event.getMinigamer().getTeam().getName().equals(TEAM_NAME))
			((JuggernautMatchData) event.getMatch().getMatchData()).setLastAttacker(event.getAttacker());
		super.onDamage(event);
	}

	@Override
	public boolean shouldBeOver(Match match) {
		List<Minigamer> minigamers = match.getMinigamers();
		if (minigamers.size() == 1) {
			Nexus.log("Match has only one team left, ending");
			return true;
		}

		int winningScore = match.getArena().getCalculatedWinningScore(match);
		if (winningScore > 0)
			for (Minigamer minigamer : minigamers)
				if (minigamer.getScore() >= winningScore) {
					match.getMatchData().setWinnerPlayer(minigamer);
					Nexus.log("Team match has reached calculated winning score (" + winningScore + "), ending");
					return true;
				}

		return false;
	}

	@Override
	protected boolean renderTeamNames() {
		return false;
	}

	@Override
	public void announceWinners(Match match) {
		Arena arena = match.getArena();
		Map<Minigamer, Integer> scores = new HashMap<>();

		match.getAliveMinigamers().forEach(minigamer -> scores.put(minigamer, minigamer.getScore()));
		if (scores.size() == 0) return;
		int winningScore = getWinningScore(scores.values());
		List<Minigamer> winners = getWinners(winningScore, scores);

		String announcement;
		if (winningScore == 0 && winners.size() != 1)
			announcement = "No players scored in " + arena.getDisplayName();
		else {
			if (match.getAliveMinigamers().size() == winners.size() && match.getAliveMinigamers().size() > 1)
				announcement = "All players tied in " + arena.getDisplayName();
			else
				announcement = getWinnersString(winners) + "&e" + arena.getDisplayName();
			if (winningScore != 0)
				announcement += " (" + winningScore + ")";
		}
		Minigames.broadcast(announcement);
	}

	private String getWinnersString(List<Minigamer> winners) {
		if (winners.size() > 1) {
			String result = winners.stream()
					.map(minigamer -> minigamer.getColoredName() + "&3")
					.collect(Collectors.joining(", "));
			int lastCommaIndex = result.lastIndexOf(", ");
			if (lastCommaIndex >= 0) {
				result = new StringBuilder(result).replace(lastCommaIndex, lastCommaIndex + 2, " and ").toString();
			}
			return result + " have tied in ";
		} else {
			return winners.get(0).getColoredName() + " &3has won ";
		}
	}

	private List<Minigamer> getWinners(int winningScore, Map<Minigamer, Integer> scores) {
		List<Minigamer> winners = new ArrayList<>();

		for (Minigamer minigamer : scores.keySet()) {
			if (scores.get(minigamer).equals(winningScore)) {
				winners.add(minigamer);
			}
		}

		return winners;
	}
}
