package me.pugabyte.bncore.features.minigames.mechanics;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.HoleInTheWallArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.bncore.features.minigames.models.matchdata.HoleInTheWallMatchData;
import me.pugabyte.bncore.features.minigames.models.matchdata.HoleInTheWallMatchData.Track;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.getLocationString;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

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
		return GameMode.SURVIVAL;
	}

	@Override
	public boolean allowFly() {
		return true;
	}

	@Override
	public boolean shuffleSpawnpoints() {
		return false;
	}

	public static final int BASE_TICK_SPEED = 16;
	public static final int TICK_DECREASE_EVERY_X_WALLS = 10;
	public static final int BASE_EMPTY_BLOCKS = 4;
	public static final int EXTRA_EMPTY_BLOCK_EVERY_X_WALLS = 3;
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

			Track track = matchData.new Track(trackRegions.iterator().next(), location);
			track.reset();
			matchData.getTracks().add(track);
		}
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);

		Match match = event.getMatch();
		HoleInTheWallArena arena = event.getMatch().getArena();
		HoleInTheWallMatchData matchData = event.getMatch().getMatchData();

		matchData.getTracks().forEach(track -> {
			Optional<Minigamer> minigamer = arena.getWGUtils().getPlayersInRegion(track.getRegion()).stream()
					.map(PlayerManager::get)
					.filter(_minigamer -> _minigamer.isPlaying(match))
					.findFirst();

			minigamer.ifPresent(track::setMinigamer);
		});

		Tasks.Countdown.builder()
				.duration(Time.SECOND.x(5))
				.onSecond(i -> match.broadcast("&7Starting in &e" + i + "..."))
				.onComplete(() -> {
					match.broadcast("Go!");
					matchData.getTracks().stream()
							.filter(track -> track.getMinigamer() != null)
							.forEach(Track::start);
				})
				.start();
	}

	@Override
	public void onQuit(MatchQuitEvent event) {
		HoleInTheWallMatchData matchData = event.getMatch().getMatchData();

		Track track = matchData.getTrack(event.getMinigamer());
		if (track != null)
			track.reset();

		super.onQuit(event);
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		HoleInTheWallMatchData matchData = event.getMatch().getMatchData();
		matchData.getTracks().forEach(Track::reset);

		super.onEnd(event);
	}

	private boolean isInAnswerRegion(Minigamer minigamer, Location location) {
		Match match = minigamer.getMatch();
		HoleInTheWallArena arena = match.getArena();
		HoleInTheWallMatchData matchData = match.getMatchData();

		Set<ProtectedRegion> answerRegions = arena.getNumberedRegionsLikeAt("answer", location);
		if (answerRegions.size() == 1) {
			Track track = matchData.getTrack(answerRegions.iterator().next());
			return track != null && minigamer.equals(track.getMinigamer());
		}
		return false;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (!isInAnswerRegion(minigamer, event.getBlock().getLocation()))
			event.setCancelled(true);
	}

	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		Match match = minigamer.getMatch();
		HoleInTheWallArena arena = match.getArena();
		HoleInTheWallMatchData matchData = match.getMatchData();

		if (event.getClickedBlock() != null) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK)
				if (MaterialTag.STAINED_GLASS.isTagged(event.getClickedBlock().getType())) {
					if (isInAnswerRegion(minigamer, event.getClickedBlock().getLocation())) {
						Player player = event.getPlayer();
						player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1F, 1F);
						player.getInventory().addItem(new ItemStack(event.getClickedBlock().getType()));
						event.getClickedBlock().setType(Material.AIR);
					}
				}

			if (Arrays.asList(Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK).contains(event.getAction()))
				if (MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())) {
					Sign sign = (Sign) event.getClickedBlock().getState();
					if (stripColor(sign.getLine(2)).contains("Fast Forward")) {
						Track track = matchData.getTrack(minigamer);
						if (track != null) {
							UUID uuid = minigamer.getPlayer().getUniqueId();
							String type = "HoleInTheWall-Skip";
							if (new CooldownService().check(uuid, type, Time.SECOND.x(3)))
								track.skip();
							else
								minigamer.tell("You must wait &e" + new CooldownService().getDiff(uuid, type));
						}
					}
				}
		}
	}

}
