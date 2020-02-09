package me.pugabyte.bncore.features.minigames.mechanics.common;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Regenerating("floor")
public abstract class SpleefMechanic extends TeamlessMechanic {

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public void onInitialize(MatchInitializeEvent event) {
		super.onInitialize(event);
		resetFloors(event.getMatch());
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		resetFloors(event.getMatch());
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		new AntiCampingTask(event.getMatch()).start();
	}

	private void resetFloors(Match match) {
		Minigames.getWorldGuardUtils().getRegionsLike(getName() + "_" + match.getArena().getName() + "_floor_[0-9]+")
				.forEach(floor -> {
					String file = (getName() + "/" + floor.getId().replaceFirst(getName().toLowerCase() + "_", "")).toLowerCase();
					Minigames.getWorldEditUtils().paste(file, floor.getMinimumPoint());
				});
	}

	public boolean breakBlock(Match match, Location location) {
		for (ProtectedRegion region : Minigames.getWorldGuardUtils().getRegionsAt(location.clone().add(0, .1, 0))) {
			if (!match.getArena().ownsRegion(region.getId(), "floor")) continue;

			Material type = location.getBlock().getType();
			if (!type.equals(Material.TNT) && !match.getArena().canUseBlock(type))
				return false;

			boolean spawnTnt = type == Material.TNT;

			playBlockBreakSound(location);
			location.getBlock().setType(Material.AIR);

			if (spawnTnt) spawnTnt(location);

			return true;
		}
		return false;
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!event.getEntityType().equals(EntityType.PRIMED_TNT)) return;

		Match match = MatchManager.getActiveMatchFromLocation(this, event.getLocation());
		if (match == null) return;

		event.blockList().forEach(block -> breakBlock(match, block.getLocation()));
		event.blockList().clear();
	}

	public void spawnTnt(Location location) {
		Location spawnLocation = location.add(0.5, 0, 0.5);
		TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(spawnLocation, EntityType.PRIMED_TNT);
		tnt.setYield(3);
		tnt.setFuseTicks(0);
	}

	public abstract void playBlockBreakSound(Location location);

	@RequiredArgsConstructor
	private static class AntiCampingTask {
		@NonNull
		Match match;
		int taskId;

		private Map<Minigamer, Integer> secondsCamping = new HashMap<>();
		private Map<Minigamer, Location> recentLocations = new HashMap<>();

		public void start() {
			final int anticampingWarn = 6;
			final int anticampingTeleport = 10;
			taskId = match.getTasks().repeat(20, 20, () -> {
				for (Minigamer minigamer : match.getAlivePlayers()) {
					if (match.isEnded()) {
						stop();
						return;
					}

					if (recentLocations.containsKey(minigamer)) {
						Location recent = recentLocations.get(minigamer);
						Location now = minigamer.getPlayer().getLocation();

						double dx = Math.abs(now.getX() - recent.getX());
						double dy = Math.abs(now.getY() - recent.getY());
						double dz = Math.abs(now.getZ() - recent.getZ());

						if ((dx < 1D && dy < 2D && dz < 1D) || minigamer.getPlayer().isSneaking()) {
							int seconds = secondsCamping.containsKey(minigamer) ? secondsCamping.get(minigamer) + 1 : 1;

							if (seconds == anticampingWarn)
								minigamer.tell("&4Warning: &cYou will be teleported down if you continue to camp");

							if (seconds == anticampingTeleport) {
								minigamer.tell("&cYou have been teleported down for camping");
								teleport(minigamer);
								secondsCamping.remove(minigamer);
							} else {
								secondsCamping.put(minigamer, seconds);
							}
						} else {
							secondsCamping.remove(minigamer);
						}
					}

					recentLocations.put(minigamer, minigamer.getPlayer().getLocation());
				}
			});
		}

		void stop() {
			Tasks.cancel(taskId);
		}

		private void teleport(Minigamer minigamer) {
			teleport(minigamer, -1);
		}

		private void teleport(Minigamer minigamer, int floorId) {
			Arena arena = minigamer.getMatch().getArena();
			Mechanic mechanic = arena.getMechanic();
			ProtectedRegion floorAt;

			// Floor ID not provided, get it
			if (floorId == -1) {
				floorAt = getFloorAt(minigamer);

				if (floorAt == null)
					throw new InvalidInputException("Could not find spleef floor for player " + minigamer.getName());

				floorId = arena.getRegionTypeId(floorAt);
			} else {
				// Floor ID provided (probably recursively trying to find next floor down)
				floorAt = arena.getProtectedRegion("floor_" + floorId);
			}

			if (floorId < 2)
				mechanic.kill(minigamer);
			else {
				Location location = minigamer.getPlayer().getLocation();
				location.setY(floorAt.getMinimumPoint().getY() - 1);

				Region floorTo = arena.getRegion("floor_" + --floorId);
				Location to = null;

				while (location.getY() >= floorTo.getMinimumPoint().getY()) {
					location.add(0, -1, 0);
					if (!location.getBlock().getType().isSolid())
						continue;

					to = location;
					break;
				}

				if (to != null) {
					teleport(minigamer, to);
					return;
				}

				// Set up correct origin and search radius for nearest block check
				int yDiff = floorTo.getMaximumPoint().getBlockY() - floorTo.getMinimumPoint().getBlockY();
				location.setY(floorTo.getMaximumPoint().getBlockY());
				if (yDiff > 0) {
					yDiff = (yDiff / 2) + 1;
					location.add(0, -yDiff, 0);
				}

				List<Block> blocks = Utils.getBlocksInRadius(location.getBlock(), 4, yDiff, 4).stream()
						.filter(block -> block.getType().isSolid())
						.sorted((block1, block2) -> {
							Double distance1 = block1.getLocation().distance(minigamer.getPlayer().getLocation());
							Double distance2 = block2.getLocation().distance(minigamer.getPlayer().getLocation());
							return distance1.compareTo(distance2);
						})
						.collect(Collectors.toList());

				// No blocks within required radius on below floor, try next one
				if (blocks.size() == 0) {
					teleport(minigamer, floorId);
					return;
				}

				teleport(minigamer, blocks.get(0).getLocation());
			}
		}

		private void teleport(Minigamer minigamer, Location to) {
			to = Utils.getCenteredLocation(to);
			to.setYaw(minigamer.getPlayer().getLocation().getYaw());
			to.setPitch(minigamer.getPlayer().getLocation().getPitch());
			minigamer.teleport(to.add(0, 1, 0));
		}

		private ProtectedRegion getFloorAt(Minigamer minigamer) {
			final String floorRegex = match.getArena().getRegionTypeRegex("floor");
			Location location = minigamer.getPlayer().getLocation();
			ProtectedRegion floor = null;
			for (int i = 0; i < 3; i++) {
				Set<ProtectedRegion> regionsAt = Minigames.getWorldGuardUtils().getRegionsAt(location);
				for (ProtectedRegion region : regionsAt) {
					if (region.getId().matches(floorRegex)) {
						floor = region;
						break;
					}
				}

				if (floor != null)
					break;

				location.add(0, -1, 0);
			}
			return floor;
		}
	}

}
