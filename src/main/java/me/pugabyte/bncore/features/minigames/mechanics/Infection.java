package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.UnbalancedTeamMechanic;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;

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
		return new ItemBuilder(Material.SKULL_ITEM).skullType(SkullType.ZOMBIE).build();
	}

	@Override
	public void announceWinners(Match match) {
		boolean humansAlive = match.getAliveTeams().stream().anyMatch(team -> team.getColor() != ChatColor.RED);

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
			BNCore.severe("Could not find zombie team on infection map, team color must be light red");
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

}
