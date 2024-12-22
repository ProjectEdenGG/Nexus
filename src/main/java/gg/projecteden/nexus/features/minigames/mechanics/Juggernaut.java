package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.models.*;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.JuggernautMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Juggernaut extends TeamMechanic {
	private static final String TEAM_NAME = "Juggernaut";

	@Override
	public @NotNull String getName() {
		return "Juggernaut";
	}

	@Override
	public @NotNull String getDescription() {
		return "Kill as many players as you can as the Juggernaut, or kill the Juggernaut to become it!";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.NETHERITE_HELMET); // kinda looks like halo's icon idk
	}

	@Override
	public RegenType getRegenType() {
		return RegenType.TIER_1;
	}

	@Override
	public boolean usesAutoBalancing() {
		return false;
	}

	@Override
	public boolean usesTeamChannels() {
		return false;
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
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
				error("Couldn't find a non-juggernaut player!", victim.getMatch());
				return;
			}
			attacker = minigamer.get();
		}

//		victim.scored(); // This really should either not be here, or be attacker.scored() instead. This gives a point when anyone dies?
		matchData.setLastAttacker(null);
		super.onDeath(event);

		if (event.getMatch().isEnded())
			return;

		// set teams
		Team juggernautTeam = victim.getTeam();
		Team humanTeam = attacker.getTeam();
		victim.setTeam(humanTeam);
		attacker.setTeam(juggernautTeam);

		// apply loadout & reset health
		Loadout juggernautLoadout = juggernautTeam.getLoadout();
		if (juggernautLoadout != null) {
			juggernautLoadout.apply(attacker);
			attacker.getPlayer().setHealth(20d);
		}

		victim.getMatch().broadcast(attacker.getColoredName() + "&e has become the Juggernaut!");
	}

	@Override
	public void onDamage(@NotNull MinigamerDamageEvent event) {
		if (event.getAttacker() != null && event.getMinigamer().getTeam().getName().equals(TEAM_NAME))
			((JuggernautMatchData) event.getMatch().getMatchData()).setLastAttacker(event.getAttacker());
		super.onDamage(event);
	}

	@Override
	public boolean shouldBeOver(@NotNull Match match) {
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
	public void announceWinners(@NotNull Match match) {
		announceTeamlessWinners(match);
	}
}
