package gg.projecteden.nexus.features.minigames.mechanics;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.Regenerating;
import gg.projecteden.nexus.features.minigames.models.arenas.UncivilEngineersArena;
import gg.projecteden.nexus.features.minigames.models.arenas.UncivilEngineersArena.MobPoint;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.CheckpointData;
import gg.projecteden.nexus.features.minigames.models.matchdata.UncivilEngineersMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import org.bukkit.*;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;

@Regenerating(value = "regen")
public class UncivilEngineers extends TeamlessMechanic {
	private static final int SLICES = 10;
	private static final int SEPARATOR_Z = -25;
	private static final NamespacedKey NBT_KEY = new NamespacedKey(Nexus.getInstance(), "uncivilengineers-slice");

	public static Location getStart() {
		return new Location(Minigames.getWorld(), 2554, 56, -2677, 90, 0);
	}

	@Override
	public @NotNull String getName() {
		return "Uncivil Engineers";
	}

	@Override
	public @NotNull String getDescription() {
		return "Race to the finish";
	}

	@Override
	public boolean isTestMode() {
		return true;
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.WOODEN_PICKAXE);
	}

	@Override
	public @NotNull GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public boolean shouldClearInventory() {
		return false;
	}

	@Override
	public boolean canDropItem(@NotNull ItemStack item) {
		return true;
	}

	@Override
	public boolean canOpenInventoryBlocks() {
		return true;
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		separatePlayers(event.getMatch());
		spawnEntities(event.getMatch());
	}

	public void separatePlayers(Match match) {
		final UncivilEngineersMatchData matchData = match.getMatchData();
		final Iterator<Minigamer> minigamers = match.getMinigamers().iterator();
		for (int i = 1; i <= SLICES; i++)
			if (minigamers.hasNext())
				matchData.assignSlice(minigamers.next(), i);
	}

	public void spawnEntities(Match match) {
		UncivilEngineersArena arena = match.getArena();
		final UncivilEngineersMatchData matchData = match.getMatchData();
		for (MobPoint point : arena.getMobPoints())
			for (int i = 1; i <= SLICES; i++) {
				final int slice = i;
				if (matchData.getSlices().size() < slice)
					continue;

				Location location = offset(point.getLocation(), i);
				location.getWorld().getChunkAtAsync(location).thenRun(() ->
					match.spawn(location, point.getType().getEntityClass(), entity -> {
					if (entity instanceof Sheep sheep)
						sheep.setColor(DyeColor.WHITE);

					entity.getPersistentDataContainer().set(NBT_KEY, PersistentDataType.INTEGER, slice);
				}));
			}
	}

	public static Location offset(Location location, Minigamer minigamer) {
		return offset(location, getSlice(minigamer));
	}

	public static Location offset(Location location, int slice) {
		return location.clone().add(0, 0, SEPARATOR_Z * (slice - 1));
	}

	public static int getSlice(Minigamer minigamer) {
		return minigamer.getMatch().<UncivilEngineersMatchData>getMatchData().getSlice(minigamer);
	}

	@EventHandler
	public void onFinish(PlayerEnteredRegionEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		if (!minigamer.getMatch().getArena().ownsRegion(event.getRegion().getId(), "finish")) return;
		minigamer.scored();
		minigamer.getMatch().end();
		// TODO wait for 1 min ?
	}

	@EventHandler
	public void onEnterCheckpointRegion(PlayerEnteredRegionEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this))
			return;

		UncivilEngineersArena arena = minigamer.getMatch().getArena();
		CheckpointData matchData = minigamer.getMatch().getMatchData();

		if (arena.ownsRegion(event.getRegion().getId(), "checkpoint")) {
			int checkpointId = Arena.getRegionNumber(event.getRegion());
			matchData.setCheckpoint(minigamer, checkpointId);
		}
	}

	public void toCheckpoint(Minigamer minigamer) {
		UncivilEngineersArena arena = minigamer.getMatch().getArena();
		CheckpointData matchData = minigamer.getMatch().getMatchData();
		minigamer.clearState();

		Location location;
		if (!matchData.getCheckpointTimes().containsKey(minigamer.getOnlinePlayer().getUniqueId()))
			location = getStart().toCenterLocation();
		else
			location = arena.getCheckpoint(matchData.getCheckpointId(minigamer));

		minigamer.teleportAsync(offset(location, minigamer));
	}

	@Override
	public void onDeath(@NotNull Minigamer victim) {
		toCheckpoint(victim);
	}

	@EventHandler
	public void onCustomDeath(PlayerDeathEvent event) {
		Minigamer minigamer = Minigamer.of(event.getEntity());
		if (!minigamer.isPlaying(this))
			return;

		event.setKeepInventory(true);
		event.getDrops().clear();
		event.setDeathMessage(null);
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (!(event.getTarget() instanceof Player player))
			return;

		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this))
			return;

		final Integer spawnSlice = event.getEntity().getPersistentDataContainer().get(NBT_KEY, PersistentDataType.INTEGER);
		if (spawnSlice == null) {
			event.setCancelled(true);
			return;
		}

		if (spawnSlice != getSlice(minigamer))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityTeleport(EntityTeleportEvent event) {
		final Location to = event.getTo();
		if (to == null)
			return;

		if (!(event.getEntity() instanceof Enderman))
			return;

		final Integer spawnSlice = event.getEntity().getPersistentDataContainer().get(NBT_KEY, PersistentDataType.INTEGER);
		if (spawnSlice == null)
			return;

		final Arena arena = ArenaManager.getFromLocation(to);
		if (arena == null) {
			event.setCancelled(true);
			return;
		}

		final Set<ProtectedRegion> regions = arena.getRegionsLikeAt(arena.getRegionTypeRegex("slice"), to);
		if (regions.isEmpty()) {
			event.setCancelled(true);
			return;
		}

		ProtectedRegion region = regions.iterator().next();
		if (Arena.getRegionNumber(region) != spawnSlice)
			event.setCancelled(true);
	}

}
