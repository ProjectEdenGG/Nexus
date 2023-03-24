package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.arenas.Connect4Arena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.Connect4MatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

// TODO: ON END GAME - MAKE BLOCKS FALL
public final class Connect4 extends TeamMechanic {

	@Override
	public @NotNull String getName() {
		return "Connect4";
	}

	@Override
	public @NotNull String getDescription() {
		return "Connect 4 checkers of your color in a row";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.BLUE_CONCRETE);
	}

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		super.onInitialize(event);

		Match match = event.getMatch();
		Connect4Arena arena = (Connect4Arena) match.getArena();

		match.worldedit().getBlocks(arena.getRegion("board")).forEach(block -> block.setType(Material.AIR));
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		Match match = event.getMatch();
		if (!match.isMechanic(this)) return;

		Connect4MatchData matchData = match.getMatchData();
		matchData.setStartingTeam();
		match.broadcast("Starting Team: " + matchData.getStartingTeam());
	}

}
