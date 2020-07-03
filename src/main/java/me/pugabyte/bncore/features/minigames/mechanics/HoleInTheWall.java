package me.pugabyte.bncore.features.minigames.mechanics;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.HoleInTheWallArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.bncore.features.minigames.models.matchdata.HoleInTheWallMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;

import static me.pugabyte.bncore.utils.StringUtils.getLocationString;

public class HoleInTheWall extends TeamlessMechanic {
	@Override
	public String getName() {
		return "Hole in the Wall";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.COBBLESTONE_WALL);
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.CREATIVE;
	}

	@Override
	public boolean shuffleSpawnpoints() {
		return false;
	}

	public static final int TICK_INCREASE_EVERY_X_WALLS = 10;
	public static final int BASE_EMPTY_BLOCKS = 4;
	public static final int EXTRA_EMPTY_BLOCK_EVERY_X_WALLS = 5;
	public static final int SKIP_BUTTON_COOLDOWN_IN_TICKS = Time.SECOND.x(3);

	@Override
	public void onInitialize(MatchInitializeEvent event) {
		super.onInitialize(event);

		HoleInTheWallArena arena = event.getMatch().getArena();
		HoleInTheWallMatchData matchData = event.getMatch().getMatchData();
		for (Location location : arena.getDesignHangerLocation()) {
			Set<ProtectedRegion> trackRegions = arena.getNumberedRegionsLikeAt("track", location);
			if (trackRegions.size() != 1)
				throw new MinigameException("Was expecting 1 track region at " + getLocationString(location) + ", but found " + trackRegions.size());

			matchData.getTracks().add(matchData.new Track(trackRegions.iterator().next(), location));
		}
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);

		HoleInTheWallArena arena = event.getMatch().getArena();
		HoleInTheWallMatchData matchData = event.getMatch().getMatchData();
		matchData.getTracks().forEach(track -> {
			Optional<Minigamer> minigamer = arena.getWGUtils().getPlayersInRegion(track.getRegion()).stream()
					.map(PlayerManager::get)
					.filter(_minigamer -> _minigamer.isPlaying(event.getMatch()))
					.findFirst();

			minigamer.ifPresent(track::setMinigamer);

			track.start();
		});
	}

}
