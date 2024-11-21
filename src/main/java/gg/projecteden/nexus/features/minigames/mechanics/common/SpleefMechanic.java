package gg.projecteden.nexus.features.minigames.mechanics.common;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.annotations.AntiCamp;
import gg.projecteden.nexus.features.minigames.models.annotations.Regenerating;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

@AntiCamp
@Regenerating("floor")
public abstract class SpleefMechanic extends TeamlessMechanic {

	@Override
	public @NotNull GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		new AntiCampingTask(event.getMatch()).start();
	}

	public boolean breakBlock(Match match, Location location) {
		if (!match.isBegun())
			return false;

		for (ProtectedRegion region : match.worldguard().getRegionsAt(location.clone().add(0, .1, 0))) {
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

		event.blockList().forEach(block -> {
			if (RandomUtils.chanceOf(5))
				return;

			breakBlock(match, block.getLocation());
		});
		event.blockList().clear();
	}

	public void spawnTnt(Location location) {
		Location spawnLocation = location.clone().toCenterLocation();
		TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(spawnLocation, EntityType.PRIMED_TNT, SpawnReason.DEFAULT);
		tnt.setYield(4);
		tnt.setFuseTicks(0);
	}

	public abstract void playBlockBreakSound(Location location);

}
