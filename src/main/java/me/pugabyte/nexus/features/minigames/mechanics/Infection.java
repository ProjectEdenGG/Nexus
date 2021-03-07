package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.UnbalancedTeamMechanic;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class Infection extends UnbalancedTeamMechanic {

	@Override
	public String getName() {
		return "Infection";
	}

	@Override
	public String getDescription() {
		return "Zombies kill humans";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.ZOMBIE_HEAD);
	}

	@Override
	public boolean canDropItem(ItemStack item) {
		return item.getType() == Material.ARROW;
	}

	public List<Minigamer> getZombies(Match match) {
		return match.getAliveMinigamers().stream().filter(minigamer -> minigamer.getTeam().getColor() == ChatColor.RED).collect(Collectors.toList());
	}

	public List<Minigamer> getHumans(Match match) {
		return match.getAliveMinigamers().stream().filter(minigamer -> minigamer.getTeam().getColor() != ChatColor.RED).collect(Collectors.toList());
	}

	@Override
	public void onStart(MatchStartEvent event) {
		getZombies(event.getMatch()).forEach(Minigamer::respawn);
		super.onStart(event);
	}

	@Override
	public void announceWinners(Match match) {
		boolean humansAlive = getHumans(match).size() > 0;

		String broadcast = "";
		if (!humansAlive)
			broadcast = "The &czombies &3have won";
		else
			if (match.getTimer().getTime() != 0)
				broadcast = "The &czombies &3has won";
			else
				broadcast = "The &9humans &3have won";

		Minigames.broadcast(broadcast + " &e" + match.getArena().getDisplayName());
	}

	// TODO: Validation on start (e.g. only two teams, one has lives, balance percentages)

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		Minigamer victim = event.getMinigamer();
		Minigamer attacker = event.getAttacker();

		Match match = victim.getMatch();
		Team zombies = match.getArena().getTeams().stream()
				.filter(team -> team.getColor() == ChatColor.RED)
				.findFirst()
				.orElse(null);

		if (zombies == null) {
			Nexus.severe("Could not find zombie team on infection map, team color must be light red");
			return;
		}

		if (victim.getTeam() != zombies) {
			event.broadcastDeathMessage();
			event.setDeathMessage(null);
			victim.setTeam(zombies);
			match.broadcast(victim.getColoredName() + " has joined the " + victim.getTeam().getColoredName());
		}

		if (attacker != null)
			attacker.scored();

		super.onDeath(event);

	}

	@Override
	protected boolean renderTeamNames() {
		return false;
	}
}
