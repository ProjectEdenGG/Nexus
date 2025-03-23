package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchStatisticsClass;
import gg.projecteden.nexus.features.minigames.models.annotations.Regenerating;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.minigames.models.statistics.HideAndSeekStatistics;
import gg.projecteden.nexus.features.minigames.models.statistics.InfectionStatistics;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.WorldEditUtils;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Regenerating({"spawndoor", "expansion"})
@MatchStatisticsClass(InfectionStatistics.class)
public class Infection extends TeamMechanic {

	@Override
	public @NotNull String getName() {
		return "Infection";
	}

	@Override
	public @NotNull String getDescription() {
		return "Humans hide from the zombies, else they get infected and become a zombie themself";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.ZOMBIE_HEAD);
	}

	@Override
	public boolean canDropItem(@NotNull ItemStack item) {
		return item.getType() == Material.ARROW;
	}

	@Override
	public boolean usesAutoBalancing() {
		return false;
	}

	@Override
	public boolean doesAllowSpectating(Match match) { return true; }

	public Team getZombieTeam(Arena arena) {
		Optional<Team> teamOptional = arena.getTeams().stream().filter(team -> team.getChatColor() == ChatColor.RED).findFirst();
		return teamOptional.orElse(null);
	}

	public Team getHumanTeam(Arena arena) {
		Optional<Team> teamOptional = arena.getTeams().stream().filter(team -> team.getChatColor() != ChatColor.RED).findFirst();
		return teamOptional.orElse(null);
	}

	public Team getZombieTeam(Match match) {
		return getZombieTeam(match.getArena());
	}

	public Team getHumanTeam(Match match) {
		return getHumanTeam(match.getArena());
	}

	public List<Minigamer> getZombies(Match match) {
		return match.getAliveMinigamers().stream().filter(minigamer -> minigamer != null && minigamer.getTeam() != null && minigamer.getTeam().getChatColor() == ChatColor.RED).collect(Collectors.toList());
	}

	public List<Minigamer> getHumans(Match match) {
		return match.getAliveMinigamers().stream().filter(minigamer -> minigamer != null && minigamer.getTeam() != null && minigamer.getTeam().getChatColor() != ChatColor.RED).collect(Collectors.toList());
	}

	public boolean isZombie(Minigamer minigamer) {
		return minigamer != null && minigamer.getTeam() != null && minigamer.getTeam().getChatColor() == ChatColor.RED;
	}

	public boolean isHuman(Minigamer minigamer) {
		return minigamer != null && minigamer.getTeam() != null && minigamer.getTeam().getChatColor() != ChatColor.RED;
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		Arena arena = event.getMatch().getArena();

		if (event.getMatch().getOnlinePlayers().size() >= arena.getMinPlayers() * 2) {
			WorldEditUtils worldedit = new WorldEditUtils(Minigames.getWorld());

			AtomicInteger amount = new AtomicInteger();
			arena.getRegionsLike("expansion").forEach(region -> {
				String schemName = arena.getSchematicName(region.getId().replaceFirst(arena.getRegionBaseName().toLowerCase() + "_", "")) + "_open";
				worldedit.paster().file(schemName).at(region.getMinimumPoint()).pasteAsync();
				amount.getAndIncrement();
			});
			if (amount.get() > 0)
				event.getMatch().broadcast(new JsonBuilder("&eNew passage ways have opened up and the map has expanded!"));
		}
	}

	@Override
	public void onBegin(@NotNull MatchBeginEvent event) {
		super.onBegin(event);
		WorldEditUtils worldedit = new WorldEditUtils(Minigames.getWorld());

		AtomicInteger amount = new AtomicInteger();
		Arena arena = event.getMatch().getArena();
		arena.getRegionsLike("spawndoor").forEach(region -> {
			amount.getAndIncrement();
			String schemName = arena.getSchematicName(region.getId().replaceFirst(arena.getRegionBaseName().toLowerCase() + "_", "")) + "_open";
			worldedit.paster().file(schemName).at(region.getMinimumPoint()).pasteAsync();
		});
		if (amount.get() > 0)
			announceRelease(event.getMatch());
		else
			getZombies(event.getMatch()).forEach(Minigamer::respawn); // Maintain old ways if no door regions exist
	}

	public void announceRelease(Match match) {
		match.broadcast(new JsonBuilder("&cThe zombies have been released!"));
	}

	@Override
	public void announceWinners(@NotNull Match match) {
		boolean humansAlive = !getHumans(match).isEmpty();

		Team winningTeam = !humansAlive || match.getTimer().getTime() != 0 ? getZombieTeam(match) : getHumanTeam(match);

		Component broadcast = Component.text("The ").append(winningTeam.asComponent())
			.append(Component.text(" have won ").append(match.getArena().asComponent()));

		Minigames.broadcast(broadcast);

		if (winningTeam.equals(getHumanTeam(match))) {
			winningTeam.getMinigamers(match).forEach(winner -> {
				if (this instanceof HideAndSeek)
					match.getMatchStatistics().award(HideAndSeekStatistics.HIDER_WINS, winner);
				else if (this instanceof Infection)
					match.getMatchStatistics().award(InfectionStatistics.HUMAN_WINS, winner);
			});
		}
		winningTeam.getMinigamers(match).forEach(winner -> {
			match.getMatchStatistics().award(HideAndSeekStatistics.WINS, winner);
		});
	}

	// TODO: Validation on start (e.g. only two teams, one has lives, balance percentages)

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		Minigamer victim = event.getMinigamer();
		Minigamer attacker = event.getAttacker();

		Match match = victim.getMatch();
		Team zombies = getZombieTeam(match);

		if (zombies == null) {
			Nexus.severe("Could not find zombie team on infection map, team color must be light red");
			return;
		}

		if (victim.getTeam() != zombies) {
			event.broadcastDeathMessage();
			event.showDeathMessage(false);
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
